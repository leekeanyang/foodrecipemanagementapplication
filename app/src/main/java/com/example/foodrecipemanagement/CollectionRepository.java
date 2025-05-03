package com.example.foodrecipemanagement;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionRepository {
    private static final String PREFS_NAME = "recipe_collection";
    private static final String KEY_SAVED_IDS = "saved_recipe_ids";
    private static CollectionRepository instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Context context;

    public static synchronized CollectionRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CollectionRepository(context);
        }
        return instance;
    }

    private CollectionRepository(Context context) {
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

        Set<String> savedIds = getSavedRecipeIds();
        savedIds.add(String.valueOf(recipe.getId()));

        prefs.edit()
                .putStringSet(getSavedIdsKey(userId), savedIds)
                .putString(getRecipeKey(userId, recipe.getId()), gson.toJson(recipe))
                .apply();
    }

    public void removeRecipe(Recipe recipe) {
        int userId = getCurrentUserId();
        if (userId == -1) return;

        Set<String> savedIds = getSavedRecipeIds();
        savedIds.remove(String.valueOf(recipe.getId()));

        prefs.edit()
                .putStringSet(getSavedIdsKey(userId), savedIds)
                .remove(getRecipeKey(userId, recipe.getId()))
                .apply();
    }

    public List<Recipe> getAllSavedRecipes() {
        int userId = getCurrentUserId();
        if (userId == -1) return new ArrayList<>();

        Set<String> savedIds = getSavedRecipeIds();
        List<Recipe> recipes = new ArrayList<>();

        for (String id : savedIds) {
            Recipe recipe = getRecipeById(userId, id);
            if (recipe != null) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    public boolean isRecipeSaved(Recipe recipe) {
        int userId = getCurrentUserId();
        if (userId == -1) return false;

        return getSavedRecipeIds().contains(String.valueOf(recipe.getId()));
    }

    private Set<String> getSavedRecipeIds() {
        int userId = getCurrentUserId();
        if (userId == -1) return new HashSet<>();

        return new HashSet<>(prefs.getStringSet(getSavedIdsKey(userId), new HashSet<>()));
    }

    private Recipe getRecipeById(int userId, String id) {
        String json = prefs.getString(getRecipeKey(userId, Integer.parseInt(id)), null);
        return json != null ? gson.fromJson(json, Recipe.class) : null;
    }

    public Recipe getRecipeById(int id) {
        int userId = getCurrentUserId();
        if (userId == -1) return null;

        return getRecipeById(userId, String.valueOf(id));
    }

    private String getSavedIdsKey(int userId) {
        return KEY_SAVED_IDS + "_" + userId;
    }

    private String getRecipeKey(int userId, int id) {
        return "recipe_" + userId + "_" + id;
    }

    public void clearCache() {
    }
}