package com.example.foodrecipemanagement;

import com.google.gson.annotations.SerializedName;

public class Nutrients {
    @SerializedName("calories")
    private double calories;

    @SerializedName("protein")
    private double protein;

    @SerializedName("fat")
    private double fat;

    @SerializedName("carbohydrates")
    private double carbohydrates;

    // Getters...
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public double getCarbohydrates() { return carbohydrates; }
}