package com.example.foodrecipemanagement;

import java.io.Serializable;
import java.util.Objects;

// Serializable allows us to pass this object through Intent
public class MealPlanEntry implements Serializable {
    private int recipeId;
    private String recipeTitle;
    private String recipeImageUrl;
    private String tag;

    // Need a no-argument constructor for Gson deserialization
    public MealPlanEntry() {}

    public MealPlanEntry(Recipe recipe, String tag) {
        this.recipeId = recipe.getId();
        this.recipeTitle = recipe.getTitle();
        this.recipeImageUrl = recipe.getImage(); // 假設 Recipe 有 getImage() 方法
        this.tag = tag;
    }

    // --- Getters ---
    public int getRecipeId() {
        return recipeId;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public void setRecipeImageUrl(String recipeImageUrl) {
        this.recipeImageUrl = recipeImageUrl;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    // --- equals() 和 hashCode() ---
    // Make sure we can compare and delete items correctly (based on recipeId and tag)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealPlanEntry that = (MealPlanEntry) o;
        return recipeId == that.recipeId && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId, tag);
    }
}