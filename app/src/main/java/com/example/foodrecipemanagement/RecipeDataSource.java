package com.example.foodrecipemanagement;

import java.util.ArrayList;
import java.util.List;

public class RecipeDataSource {
    private static RecipeDataSource instance;
    private final List<Recipe> apiRecipes = new ArrayList<>();
    private final List<Recipe> userRecipes = new ArrayList<>();
    private final List<Recipe> allRecipes = new ArrayList<>();

    public static synchronized RecipeDataSource getInstance() {
        if (instance == null) {
            instance = new RecipeDataSource();
        }
        return instance;
    }

    public void addApiRecipes(List<Recipe> recipes) {
        apiRecipes.addAll(recipes);
    }

    public void addUserRecipe(Recipe recipe) {
        userRecipes.add(recipe);
    }

    public List<Recipe> getUserCreatedRecipes() {
        List<Recipe> userRecipes = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (recipe.isUserCreated()) {
                userRecipes.add(recipe);
            }
        }
        return userRecipes;
    }

    public List<Recipe> getApiRecipes() {
        return new ArrayList<>(apiRecipes);
    }

    public List<Recipe> getUserRecipes() {
        return new ArrayList<>(userRecipes);
    }

    public List<Recipe> getAllRecipes() {
        return new ArrayList<>(allRecipes);
    }

}