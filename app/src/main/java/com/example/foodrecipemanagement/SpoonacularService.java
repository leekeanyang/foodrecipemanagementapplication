package com.example.foodrecipemanagement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SpoonacularService {

    // https://api.spoonacular.com/mealplanner/generate?timeFrame=week&targetCalories=2000&diet=vegetarian&exclude=shellfish&apiKey=YOUR_API_KEY
    @GET("mealplanner/generate")
    Call<WeeklyMealPlanResponse> generateMealPlan(
            @Query("apiKey") String apiKey,
            @Query("timeFrame") String timeFrame,     // "day" or "week"
            @Query("targetCalories") Integer targetCalories, // Using Integer to allow null
            @Query("diet") String diet,               // For example, "vegetarian", "vegan", "gluten free", etc. (optional)
            @Query("exclude") String exclude          //Comma separated list of excluded ingredients (optional)
    );
}