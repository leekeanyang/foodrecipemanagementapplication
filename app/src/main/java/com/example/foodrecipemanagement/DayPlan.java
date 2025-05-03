package com.example.foodrecipemanagement;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DayPlan {
    @SerializedName("meals")
    private List<Meal> meals;

    @SerializedName("nutrients")
    private Nutrients nutrients;

    // Getters...
    public List<Meal> getMeals() { return meals; }
    public Nutrients getNutrients() { return nutrients; }
}