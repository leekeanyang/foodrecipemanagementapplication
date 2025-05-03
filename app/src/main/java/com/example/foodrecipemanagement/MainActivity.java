package com.example.foodrecipemanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("current_user_id", -1);
        if (userId == -1) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.fragment_container, new RecipeFragment(), "recipe")
                .commit();
        activeFragment = new RecipeFragment();

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                FragmentManager fm = getSupportFragmentManager();
                Fragment newFragment = activeFragment;

                int itemId = item.getItemId();

                if (itemId == R.id.nav_recipe) {
                    newFragment = new RecipeFragment();
                } else if (itemId == R.id.nav_meal_plan) {
                    newFragment = new MealPlanFragment();
                } else if (itemId == R.id.nav_cart) {
                    newFragment = new CartFragment();
                } else if (itemId == R.id.nav_community) {
                    newFragment = new CommunityFragment();
                }

                if (newFragment != activeFragment) {
                    fm.beginTransaction()
                            .replace(R.id.fragment_container, newFragment)
                            .commit();
                    activeFragment = newFragment;
                }
                return true;
            };
}