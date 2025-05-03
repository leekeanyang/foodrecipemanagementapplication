package com.example.foodrecipemanagement;

import android.app.Application;
import com.example.foodrecipemanagement.database.AppDatabase;  // <-- Add this import

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.getDatabase(this);
    }
}