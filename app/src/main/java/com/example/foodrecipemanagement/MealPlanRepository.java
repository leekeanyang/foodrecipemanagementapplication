package com.example.foodrecipemanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MealPlanRepository {
    private static final String PREFS_NAME = "meal_plan_prefs";
    private static final String MEAL_PLAN_PREFIX = "meal_plan_";
    private static MealPlanRepository instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public static synchronized MealPlanRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MealPlanRepository(context.getApplicationContext());
        }
        return instance;
    }

    private MealPlanRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save a list of meal plans for a certain day
     * @param dateString date string (e.g. "2025-04-30")
     * @param entries MealPlanEntry list for that day
     */
    public void saveMealPlanForDay(String dateString, List<MealPlanEntry> entries) {
        String key = MEAL_PLAN_PREFIX + dateString;
        String json = gson.toJson(entries);
        Log.d("MealPlanRepository", "Saving for key: " + key + ", data: " + json);
        prefs.edit().putString(key, json).apply();
    }

    /**
     * Get a list of meal plans for a certain day
     * @param dateString date string (e.g. "2025-04-30")
     * @return MealPlanEntry list for that day, or an empty list if it has not been saved
     */
    public List<MealPlanEntry> getMealPlanForDay(String dateString) {
        String key = MEAL_PLAN_PREFIX + dateString;
        String json = prefs.getString(key, null);
        Log.d("MealPlanRepository", "Loading for key: " + key);

        if (json == null) {
            Log.d("MealPlanRepository", "No data found for key: " + key);
            return new ArrayList<>(); // Return an empty list to avoid null
        }

        // Using TypeToken to handle deserialization of generic lists
        Type type = new TypeToken<ArrayList<MealPlanEntry>>() {}.getType();
        try {
            List<MealPlanEntry> entries = gson.fromJson(json, type);
            Log.d("MealPlanRepository", "Loaded data: " + entries.size() + " entries");
            return entries != null ? entries : new ArrayList<>();
        } catch (Exception e) {
            Log.e("MealPlanRepository", "Error deserializing meal plan for key: " + key, e);
            return new ArrayList<>(); // Returns an empty list on error
        }
    }

    /**
     * Delete meal plan for a certain day (if necessary)
     * @param dateString date string (e.g. "2025-04-30")
     */
    public void deleteMealPlanForDay(String dateString) {
        String key = MEAL_PLAN_PREFIX + dateString;
        Log.d("MealPlanRepository", "Deleting data for key: " + key);
        prefs.edit().remove(key).apply();
    }
}