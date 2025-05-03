package com.example.foodrecipemanagement;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImage;
    private TextView title, time, servings, ingredients, instructions;
    private Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initializeViews();

        if (getIntent().hasExtra("recipe")) {
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            if (recipe != null) {
                populateUI(recipe);
            }
        } else {
            int recipeId = getIntent().getIntExtra("recipe_id", -1);
            if (recipeId == -1) {
                showError("Invalid recipe");
                return;
            }

            Recipe userRecipe = UserRecipeRepository.getInstance(this)
                    .getAllUserRecipes()
                    .stream()
                    .filter(r -> r.getId() == recipeId)
                    .findFirst()
                    .orElse(null);

            if (userRecipe != null) {
                populateUI(userRecipe);
            } else {
                Recipe collectionRecipe = CollectionRepository.getInstance(this)
                        .getRecipeById(recipeId);

                if (collectionRecipe != null) {
                    populateUI(collectionRecipe);
                } else {
                    fetchRecipeDetails(recipeId);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem saveItem = menu.findItem(R.id.action_save);
        if (currentRecipe != null) {
            boolean isSaved = CollectionRepository.getInstance(this).isRecipeSaved(currentRecipe);
            saveItem.setIcon(isSaved ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            toggleSaveRecipe(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleSaveRecipe(MenuItem item) {
        if (currentRecipe == null) return;

        CollectionRepository repo = CollectionRepository.getInstance(this);
        boolean isSaved = repo.isRecipeSaved(currentRecipe);

        if (isSaved) {
            repo.removeRecipe(currentRecipe);
        } else {
            repo.saveRecipe(currentRecipe);
        }

        item.setIcon(isSaved ? R.drawable.ic_star_outline : R.drawable.ic_star_filled);
        Toast.makeText(this, isSaved ? "Removed from collection" : "Saved to collection", Toast.LENGTH_SHORT).show();
    }

    private void initializeViews() {
        recipeImage = findViewById(R.id.recipe_image);
        title = findViewById(R.id.title);
        time = findViewById(R.id.time);
        servings = findViewById(R.id.servings);
        ingredients = findViewById(R.id.ingredients);
        instructions = findViewById(R.id.instructions);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchRecipeDetails(int recipeId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpoonacularApi api = retrofit.create(SpoonacularApi.class);
        api.getRecipeDetails(recipeId, "bb3cda5f6f7947468b31a3612d552fff", false)
                .enqueue(new Callback<Recipe>() {
                    @Override
                    public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            populateUI(response.body());
                        } else {
                            showError("Failed to load details: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Recipe> call, Throwable t) {
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void populateUI(Recipe recipe) {
        title.setText(recipe.getTitle() != null ? recipe.getTitle() : "No Title");
        currentRecipe = recipe;
        invalidateOptionsMenu();

        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            if (recipe.getImage().startsWith("content://") || recipe.getImage().startsWith("file://")) {
                Glide.with(this)
                        .load(Uri.parse(recipe.getImage()))
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error_image)
                        .into(recipeImage);
            } else {
                Glide.with(this)
                        .load(recipe.getImage())
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error_image)
                        .into(recipeImage);
            }
        } else {
            recipeImage.setImageResource(R.drawable.ic_placeholder);
        }

        int readyInMinutes = recipe.getReadyInMinutes();
        if (readyInMinutes > 0) {
            time.setText(readyInMinutes + " mins");
            time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time, 0, 0, 0);
        } else {
            time.setText("Time not specified");
            time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        int servingsCount = recipe.getServings();
        if (servingsCount > 0) {
            servings.setText("Serves " + servingsCount);
            servings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_servings, 0, 0, 0);
        } else {
            servings.setText("Servings not specified");
            servings.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        StringBuilder ingredientsText = new StringBuilder();
        List<Recipe.ExtendedIngredient> ingredientsList = recipe.getExtendedIngredients();
        if (ingredientsList != null && !ingredientsList.isEmpty()) {
            for (Recipe.ExtendedIngredient ingredient : ingredientsList) {
                ingredientsText.append("• ").append(ingredient.getOriginal()).append("\n");
            }
        } else {
            ingredientsText.append("No ingredients information available");
        }
        ingredients.setText(ingredientsText.toString());

        String instructionText = recipe.getInstructions();
        if (instructionText != null && !instructionText.isEmpty()) {
            if (instructionText.contains("<ol>") || instructionText.contains("<li>")) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    instructions.setText(Html.fromHtml(instructionText, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    instructions.setText(Html.fromHtml(instructionText));
                }
            } else {
                instructions.setText(instructionText);
            }
        } else {
            instructions.setText("No cooking instructions provided");
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}