package com.example.foodrecipemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CommunityDetailActivity extends AppCompatActivity {
    private int communityId;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabMain;
    private View fabMenu;
    private View menuAskQuestion;
    private View menuAddRecipe;
    private boolean isFabMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        communityId = getIntent().getIntExtra("community_id", -1);
        MaterialToolbar toolbar = findViewById(R.id.toolbar_community_detail);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        fabMain = findViewById(R.id.fab_main);
        fabMenu = findViewById(R.id.fab_menu);
        menuAskQuestion = findViewById(R.id.menu_ask_question);
        menuAddRecipe = findViewById(R.id.menu_add_recipe);

        // Set up the toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("community_name"));
        }

        // Handle back button click
        toolbar.setNavigationOnClickListener(v -> {
            Log.d("CommunityDetailActivity", "Back button clicked");
            onBackPressed();
        });

        setupViewPager();
        setupFabMenu();
    }

    private void setupViewPager() {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return 2;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        RecipeFragment recipeFragment = new RecipeFragment();
                        Bundle args = new Bundle();
                        args.putInt("community_id", communityId);
                        recipeFragment.setArguments(args);
                        return recipeFragment;
                    case 1:
                        return ChatsFragment.newInstance(communityId);
                    default:
                        return new Fragment();
                }
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(position == 0 ? "Recipes" : "Chats")).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) {
                    fabMain.setVisibility(View.VISIBLE);
                } else {
                    fabMain.setVisibility(View.GONE);
                    closeFabMenu();
                }
            }
        });
    }

    private void setupFabMenu() {
        fabMain.setOnClickListener(v -> {
            if (isFabMenuOpen) {
                closeFabMenu();
            } else {
                openFabMenu();
            }
        });

        menuAskQuestion.setOnClickListener(v -> {
            AskQuestionDialog dialog = new AskQuestionDialog();
            dialog.setCommunityId(communityId);
            dialog.setOnQuestionAddedListener(question -> {
                Fragment fragment = getSupportFragmentManager()
                        .findFragmentByTag("f" + viewPager.getCurrentItem());
                if (fragment instanceof ChatsFragment) {
                    ((ChatsFragment) fragment).addNewQuestion(question);
                }
            });
            dialog.show(getSupportFragmentManager(), "add_question");
            closeFabMenu();
        });

        menuAddRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(CommunityDetailActivity.this, CreateRecipeActivity.class);
            intent.putExtra("community_id", communityId);
            startActivity(intent);
            closeFabMenu();
        });
    }

    private void openFabMenu() {
        isFabMenuOpen = true;
        fabMain.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        fabMenu.setVisibility(View.VISIBLE);
    }

    private void closeFabMenu() {
        isFabMenuOpen = false;
        fabMain.setImageResource(android.R.drawable.ic_input_add);
        fabMenu.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (isFabMenuOpen) {
            closeFabMenu();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}