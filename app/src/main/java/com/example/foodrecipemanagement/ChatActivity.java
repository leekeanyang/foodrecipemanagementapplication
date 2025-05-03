package com.example.foodrecipemanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodrecipemanagement.databinding.ActivityChatBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.SPOONACULAR_API_KEY;

    private ActivityChatBinding binding;
    private SpoonacularService spoonacularService;
    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        // Initializing Retrofit Service
        spoonacularService = ApiClient.getClient().create(SpoonacularService.class);

        setupGenerateButton();

    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarChat);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.title_generate_meal_plan); // Define: "Generate Meal Plan"
        }
    }

    private void setupGenerateButton() {
        binding.buttonGeneratePlan.setOnClickListener(v -> {
            String targetCaloriesStr = binding.editTextCalories.getText().toString().trim();
            String diet = binding.editTextDiet.getText().toString().trim();
            String exclude = binding.editTextExclude.getText().toString().trim();

            Integer targetCalories = null;
            if (!targetCaloriesStr.isEmpty()) {
                try {
                    targetCalories = Integer.parseInt(targetCaloriesStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, R.string.error_invalid_calories, Toast.LENGTH_SHORT).show(); // Define: "Invalid calories format"
                    return;
                }
            } else {
                Toast.makeText(this, R.string.error_calories_required, Toast.LENGTH_SHORT).show(); // Define: "Target calories are required"
                return;
            }


            // Show loading indicator
            binding.progressBarLoading.setVisibility(View.VISIBLE);
            binding.scrollViewResult.setVisibility(View.GONE);
            binding.textViewMealPlanResult.setText(""); // Clear old results

            Log.d(TAG, "Generating plan with: calories=" + targetCalories + ", diet=" + diet + ", exclude=" + exclude);

            // call API
            Call<WeeklyMealPlanResponse> call = spoonacularService.generateMealPlan(
                    API_KEY,
                    "week", // timeFrame
                    targetCalories,
                    diet.isEmpty() ? null : diet, // Pass null if empty
                    exclude.isEmpty() ? null : exclude // Pass null if empty
            );

            // 非同步執行請求
            call.enqueue(new Callback<WeeklyMealPlanResponse>() {
                @Override
                public void onResponse(@NonNull Call<WeeklyMealPlanResponse> call, @NonNull Response<WeeklyMealPlanResponse> response) {
                    binding.progressBarLoading.setVisibility(View.GONE); // Hide Loading
                    binding.scrollViewResult.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null && response.body().getWeek() != null) {
                        Log.d(TAG, "API call successful.");
                        WeeklyMealPlanResponse mealPlan = response.body();
                        // *** mealPlan data is processed and displayed here ***
                        displayMealPlan(mealPlan);
                    } else {
                        // Handle API errors (e.g. 401 Unauthorized, 402 Credit Exhausted, 404, 500, etc.)
                        String errorMsg = "Error: " + response.code();
                        if (response.errorBody() != null) {
                            try {
                                errorMsg += " - " + response.errorBody().string();
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body", e);
                            }
                        }
                        Log.e(TAG, "API call failed: " + errorMsg);
                        binding.textViewMealPlanResult.setText(getString(R.string.error_api_failed, errorMsg)); // Define: "API request failed: %1$s"
                        Toast.makeText(ChatActivity.this, R.string.error_generating_plan, Toast.LENGTH_LONG).show(); // Define: "Failed to generate plan"
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeeklyMealPlanResponse> call, @NonNull Throwable t) {
                    binding.progressBarLoading.setVisibility(View.GONE); // Hide Loading
                    binding.scrollViewResult.setVisibility(View.VISIBLE);
                    Log.e(TAG, "API call failure (Network/Other): ", t);
                    binding.textViewMealPlanResult.setText(getString(R.string.error_network_failed, t.getMessage())); // Define: "Network error: %1$s"
                    Toast.makeText(ChatActivity.this, R.string.error_generating_plan_network, Toast.LENGTH_LONG).show(); // Define: "Network error generating plan"
                }
            });
        });
    }

    // Simple display method (put JSON or parsed content into TextView)
    private void displayMealPlan(WeeklyMealPlanResponse mealPlan) {

        StringBuilder displayText = new StringBuilder();
        if (mealPlan.getWeek() != null) {
            appendDayPlan(displayText, "Monday", mealPlan.getWeek().getMonday());
            appendDayPlan(displayText, "Tuesday", mealPlan.getWeek().getTuesday());
            appendDayPlan(displayText, "Wednesday", mealPlan.getWeek().getWednesday());
            appendDayPlan(displayText, "Thursday", mealPlan.getWeek().getThursday());
            appendDayPlan(displayText, "Friday", mealPlan.getWeek().getFriday());
            appendDayPlan(displayText, "Saturday", mealPlan.getWeek().getSaturday());
            appendDayPlan(displayText, "Sunday", mealPlan.getWeek().getSunday());
        } else {
            displayText.append(getString(R.string.no_plan_generated)); // Define: "No meal plan generated."
        }
        binding.textViewMealPlanResult.setText(displayText.toString());

        // TODO: 未來可以將資料傳遞給 RecyclerView Adapter 來更美觀地顯示
    }

    private void appendDayPlan(StringBuilder builder, String dayName, DayPlan dayPlan) {
        if (dayPlan == null || dayPlan.getMeals() == null || dayPlan.getMeals().isEmpty()) return;

        builder.append("--- ").append(dayName).append(" ---\n");
        if (dayPlan.getNutrients() != null) {
            builder.append(String.format(" Calories: %.0f, P: %.0fg, F: %.0fg, C: %.0fg\n",
                    dayPlan.getNutrients().getCalories(),
                    dayPlan.getNutrients().getProtein(),
                    dayPlan.getNutrients().getFat(),
                    dayPlan.getNutrients().getCarbohydrates()));
        }
        int mealNum = 1;
        for (Meal meal : dayPlan.getMeals()) {
            if (meal != null) {
                builder.append(" Meal ").append(mealNum++).append(": ").append(meal.getTitle()).append("\n");
            }
        }
        builder.append("\n");
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}