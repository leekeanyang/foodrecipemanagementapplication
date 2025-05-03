package com.example.foodrecipemanagement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApi {
    @GET("recipes/random")
    Call<RecipeResponse> getRandomRecipes(
            @Query("apiKey") String apiKey,
            @Query("number") int number
    );

    @GET("recipes/complexSearch")
    Call<SearchResponse> searchRecipes(
            @Query("apiKey") String apiKey,
            @Query("query") String query,
            @Query("number") int number,
            @Query("instructionsRequired") boolean instructionsRequired
    );

    @GET("recipes/{id}/information")
    Call<Recipe> getRecipeDetails(
            @Path("id") int recipeId,
            @Query("apiKey") String apiKey,
            @Query("includeNutrition") boolean includeNutrition
    );
}

class RecipeResponse {
    private List<Recipe> recipes;
    public List<Recipe> getRecipes() { return recipes; }
}

class SearchResponse {
    private List<Recipe> results;
    public List<Recipe> getResults() { return results; }
}