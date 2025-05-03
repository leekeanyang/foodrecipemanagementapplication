package com.example.foodrecipemanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.foodrecipemanagement.CollectionRepository;
import com.example.foodrecipemanagement.databinding.ActivityMealPlanEditBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MealPlanEditActivity extends AppCompatActivity implements
        MealPlanEntryAdapter.OnMealEntryDeleteListener,
        MealPlanEntryAdapter.OnMealEntryClickListener {

    public static final String EXTRA_DATE_REPO_STRING = "com.example.foodrecipemanagement.DATE_REPO_STRING";
    public static final String EXTRA_DATE_DISPLAY_STRING = "com.example.foodrecipemanagement.DATE_DISPLAY_STRING";

    private ActivityMealPlanEditBinding binding;
    private MealPlanRepository mealPlanRepository;
    private CollectionRepository collectionRepository;
    private UserRecipeRepository userRecipeRepo;
    private MealPlanEntryAdapter adapter;
    private List<MealPlanEntry> currentEntries;
    private String dateStringForRepo;
    private String dateDisplayString;
    private final String[] mealTags = {"Breakfast", "Lunch", "Dinner", "Snack"};

    // --- onCreate ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMealPlanEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_DATE_REPO_STRING) && intent.hasExtra(EXTRA_DATE_DISPLAY_STRING)) {
            dateStringForRepo = intent.getStringExtra(EXTRA_DATE_REPO_STRING);
            dateDisplayString = intent.getStringExtra(EXTRA_DATE_DISPLAY_STRING);
        } else {
            Log.e("MealPlanEditActivity", "Date information missing in Intent.");
            Toast.makeText(this, "Error: Date information missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Repositories
        mealPlanRepository = MealPlanRepository.getInstance(this);
        userRecipeRepo = UserRecipeRepository.getInstance(this);
        collectionRepository = CollectionRepository.getInstance(this);


        setupToolbar();
        setupRecyclerView();
        loadMealPlanEntries();
        setupButtons();
        updateEmptyState(currentEntries.isEmpty());
    }

    // --- setupToolbar, setupRecyclerView, loadMealPlanEntries, setupButtons, updateEmptyState 保持不變 ---
    private void setupToolbar() {
        binding.toolbarMealPlanEdit.setTitle(dateDisplayString);
        setSupportActionBar(binding.toolbarMealPlanEdit);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupRecyclerView() {
        currentEntries = new ArrayList<>();
        adapter = new MealPlanEntryAdapter(this, currentEntries, this, this);
        binding.recyclerViewMealPlanEntries.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMealPlanEntries.setAdapter(adapter);
    }

    private void loadMealPlanEntries() {
        List<MealPlanEntry> loadedEntries = mealPlanRepository.getMealPlanForDay(dateStringForRepo);
        currentEntries.clear();
        currentEntries.addAll(loadedEntries);
        adapter.notifyDataSetChanged(); // 如果 adapter 是 RecyclerView.Adapter
        Log.d("MealPlanEditActivity", "Loaded " + currentEntries.size() + " entries for " + dateStringForRepo);
        updateEmptyState(currentEntries.isEmpty());
    }

    private void setupButtons() {
        binding.fabAddMealEntry.setOnClickListener(v -> showSelectRecipeDialog());
        binding.buttonSaveMealPlan.setOnClickListener(v -> saveMealPlanAndFinish());
    }

    private void updateEmptyState(boolean isEmpty) {
        binding.recyclerViewMealPlanEntries.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.textViewEmptyMealPlan.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void showSelectRecipeDialog() {
        List<Recipe> userRecipes = userRecipeRepo.getAllUserRecipes();
        List<Recipe> collectedRecipes = collectionRepository.getAllSavedRecipes();

        Map<Integer, Recipe> combinedRecipeMap = new LinkedHashMap<>();

        if (userRecipes != null) {
            for (Recipe recipe : userRecipes) {
                if (recipe != null) {
                    combinedRecipeMap.put(recipe.getId(), recipe);
                }
            }
        }
        if (collectedRecipes != null) {
            for (Recipe recipe : collectedRecipes) {
                if (recipe != null) { // 添加 null 檢查
                    combinedRecipeMap.putIfAbsent(recipe.getId(), recipe);
                }
            }
        }

        List<Recipe> allAddableRecipes = new ArrayList<>(combinedRecipeMap.values());

        if (allAddableRecipes.isEmpty()) {
            Toast.makeText(this, R.string.no_recipes_to_add, Toast.LENGTH_SHORT).show(); // Define: "No recipes available to add (created or collected)."
            return;
        }

        List<String> displayItems = new ArrayList<>();
        for (Recipe recipe : allAddableRecipes) {
            if (recipe != null && recipe.getTitle() != null) {
                String prefix = recipe.isUserCreated() ? "[Created] " : "[Collected] ";
                displayItems.add(prefix + recipe.getTitle());
            } else {
                // Optionally handle recipes with no title in the combined list
                displayItems.add("[Unknown Recipe]");
            }
        }

        // Handle case where combined list exists but has no displayable titles
        if (displayItems.isEmpty()) {
            Toast.makeText(this, R.string.recipes_no_titles, Toast.LENGTH_SHORT).show(); // Define: "Cannot add recipes without titles."
            return;
        }


        String[] items = displayItems.toArray(new String[0]);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.select_recipe_to_add) // Define: "Select Recipe to Add"
                .setItems(items, (dialog, which) -> {
                    // User selected an item title
                    // Find the corresponding Recipe object from the combined list
                    Recipe selectedRecipe = null;
                    if (which >= 0 && which < allAddableRecipes.size()) {
                        selectedRecipe = allAddableRecipes.get(which);
                    }

                    if (selectedRecipe != null) {
                        showSelectTagDialog(selectedRecipe); // Show tag selection dialog
                    } else {
                        Log.e("MealPlanEditActivity", "Selected recipe at index " + which + " was null or index out of bounds from combined list.");
                        Toast.makeText(this, R.string.error_selecting_recipe, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showSelectTagDialog(Recipe selectedRecipe) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.select_tag)
                .setItems(mealTags, (dialog, which) -> {
                    String selectedTag = mealTags[which];
                    addMealEntry(selectedRecipe, selectedTag);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void addMealEntry(Recipe recipe, String tag) {
        MealPlanEntry newEntry = new MealPlanEntry(recipe, tag);
        if (!currentEntries.contains(newEntry)) {
            currentEntries.add(newEntry);
            adapter.notifyItemInserted(currentEntries.size() - 1);
            updateEmptyState(currentEntries.isEmpty());
            binding.recyclerViewMealPlanEntries.smoothScrollToPosition(currentEntries.size() - 1);
        } else {
            Toast.makeText(this, R.string.meal_entry_already_exists, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMealEntryDelete(MealPlanEntry entry, int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_delete_meal_entry_title)
                .setMessage(getString(R.string.confirm_delete_meal_entry_message, entry.getRecipeTitle(), entry.getTag()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (position >= 0 && position < currentEntries.size()) {
                        currentEntries.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, currentEntries.size() - position);
                        updateEmptyState(currentEntries.isEmpty());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onMealEntryClick(MealPlanEntry entry) {
        Log.d("MealPlanEditActivity", "Clicked on meal entry: " + entry.getRecipeTitle() + " (ID: " + entry.getRecipeId() + ")");
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        int recipeId = entry.getRecipeId();

        Recipe userRecipe = userRecipeRepo.getRecipeById(recipeId);

        if (userRecipe != null) {
            Log.d("MealPlanEditActivity", "Found recipe in UserRecipeRepository. Passing Parcelable.");
            intent.putExtra("recipe", userRecipe);
            startActivity(intent);
        } else {
            Log.d("MealPlanEditActivity", "Recipe not found in UserRecipeRepository. Checking CollectionRepository.");
            Recipe collectedRecipe = collectionRepository.getRecipeById(recipeId);

            if (collectedRecipe != null) {
                Log.d("MealPlanEditActivity", "Found recipe in CollectionRepository. Passing ID.");
                intent.putExtra("recipe_id", recipeId);
                startActivity(intent);
            } else {
                Log.e("MealPlanEditActivity", "Could not find recipe with ID: " + recipeId + " in either repository for detail view.");
                Toast.makeText(this, R.string.error_finding_recipe_details, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveMealPlanAndFinish() {
        mealPlanRepository.saveMealPlanForDay(dateStringForRepo, currentEntries);
        Log.d("MealPlanEditActivity", "Saved " + currentEntries.size() + " entries for " + dateStringForRepo);
        Toast.makeText(this, R.string.meal_plan_saved, Toast.LENGTH_SHORT).show();
        finish();
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