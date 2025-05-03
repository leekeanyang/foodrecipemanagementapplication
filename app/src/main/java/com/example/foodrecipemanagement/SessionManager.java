package com.example.foodrecipemanagement;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";

    private SharedPreferences sharedPreferences;
    private static SessionManager instance;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void loginUser(int userId, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getInt(KEY_USER_ID, -1) != -1;
    }

    public int getCurrentUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getCurrentUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_EMAIL);
        editor.apply();
    }
}