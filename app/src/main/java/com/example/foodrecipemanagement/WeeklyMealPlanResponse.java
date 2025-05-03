package com.example.foodrecipemanagement;

import com.google.gson.annotations.SerializedName;

public class WeeklyMealPlanResponse {
    @SerializedName("week")
    private WeekPlan week;

    // Getters...
    public WeekPlan getWeek() { return week; }
}