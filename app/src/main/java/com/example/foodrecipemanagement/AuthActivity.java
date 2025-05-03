package com.example.foodrecipemanagement;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

public class AuthActivity extends AppCompatActivity {

    private TextView tabLogin, tabSignup;
    private View indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLogin = findViewById(R.id.tab_login);
        tabSignup = findViewById(R.id.tab_signup);
        indicator = findViewById(R.id.indicator);

        setupTabs();
        loadInitialFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupTabs() {
        tabLogin.setOnClickListener(v -> {
            switchFragment(new LoginFragment(), 0);
            updateTabStyles(0);
        });

        tabSignup.setOnClickListener(v -> {
            switchFragment(new SignupFragment(), 1);
            updateTabStyles(1);
        });
    }

    private void loadInitialFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
        updateTabStyles(0);
    }

    private void switchFragment(Fragment fragment, int position) {
        animateIndicator(position);
        updateTabStyles(position);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void animateIndicator(int position) {
        View target = position == 0 ? tabLogin : tabSignup;
        ValueAnimator animator = ValueAnimator.ofFloat(indicator.getX(), target.getX());
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            float x = (float) animation.getAnimatedValue();
            indicator.setX(x);
        });
        animator.start();
    }

    private void updateTabStyles(int selectedPosition) {
        tabLogin.setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
        tabSignup.setTextColor(ContextCompat.getColor(this, R.color.textSecondary));

        if(selectedPosition == 0) {
            tabLogin.setTextColor(ContextCompat.getColor(this, R.color.primaryDarkColor));
        } else {
            tabSignup.setTextColor(ContextCompat.getColor(this, R.color.primaryDarkColor));
        }
    }
}