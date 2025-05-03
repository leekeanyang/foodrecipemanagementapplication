package com.example.foodrecipemanagement;

import com.google.gson.annotations.SerializedName;

public class Meal {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("imageType")
    private String imageType;

    @SerializedName("readyInMinutes")
    private int readyInMinutes;

    @SerializedName("servings")
    private int servings;

    @SerializedName("sourceUrl")
    private String sourceUrl;

    // Getters...
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImageType() { return imageType; }
    public int getReadyInMinutes() { return readyInMinutes; }
    public int getServings() { return servings; }
    public String getSourceUrl() { return sourceUrl; }
}