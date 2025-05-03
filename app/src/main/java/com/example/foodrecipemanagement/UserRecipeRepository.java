package com.example.foodrecipemanagement;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRecipeRepository {
    private static final String PREFS_NAME = "user_recipes";
    private static final String KEY_RECIPE_IDS = "user_recipe_ids";
    private static UserRecipeRepository instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Context context;

    public static synchronized UserRecipeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRecipeRepository(context);
        }
        return instance;
    }

    private UserRecipeRepository(Context context) {
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private int getCurrentUserId() {
        SharedPreferences userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return userPrefs.getInt("current_user_id", -1);
    }

    public void saveRecipe(Recipe recipe) {
        int userId = getCurrentUserId();
        if (userId == -1) return;

        Set<String> recipeIds = getSavedRecipeIds();
        recipeIds.add(String.valueOf(recipe.getId()));

        prefs.edit()
                .putStringSet(getRecipeIdsKey(userId), recipeIds)
                .putString(getRecipeKey(userId, recipe.getId()), gson.toJson(recipe))
                .apply();
    }

    public List<Recipe> getAllUserRecipes() {
        int userId = getCurrentUserId();
        if (userId == -1) return new ArrayList<>();

        Set<String> recipeIds = getSavedRecipeIds();
        List<Recipe> recipes = new ArrayList<>();

        for (String id : recipeIds) {
            String json = prefs.getString(getRecipeKey(userId, Integer.parseInt(id)), null);
            if (json != null) {
                Recipe recipe = gson.fromJson(json, Recipe.class);
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    public void removeRecipe(Recipe recipe) {
        int userId = getCurrentUserId();
        if (userId == -1) return;

        Set<String> recipeIds = getSavedRecipeIds();
        recipeIds.remove(String.valueOf(recipe.getId()));

        prefs.edit()
                .putStringSet(getRecipeIdsKey(userId), recipeIds)
                .remove(getRecipeKey(userId, recipe.getId()))
                .apply();
    }

    private Set<String> getSavedRecipeIds() {
        int userId = getCurrentUserId();
        if (userId == -1) return new HashSet<>();

        return new HashSet<>(prefs.getStringSet(getRecipeIdsKey(userId), new HashSet<>()));
    }

    private String getRecipeIdsKey(int userId) {
        return KEY_RECIPE_IDS + "_" + userId;
    }

    private String getRecipeKey(int userId, int id) {
        return "user_recipe_" + userId + "_" + id;
    }

    public void clearCache() {
    }

    public Recipe getRecipeById(int recipeId) {
        List<Recipe> allUserRecipes = getAllUserRecipes();
        if (allUserRecipes != null) {
            for (Recipe recipe : allUserRecipes) {
                if (recipe != null && recipe.getId() == recipeId) {
                    return recipe;
                }
            }
        }
        return null;
    }
}