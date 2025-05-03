package com.example.foodrecipemanagement;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipemanagement.databinding.FragmentRecipeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeFragment extends Fragment {
    private FragmentRecipeBinding binding;
    private RecipeAdapter adapter;
    private RecyclerView recyclerRecipes;
    private CollectionRepository collectionRepo;
    private UserRecipeRepository userRecipeRepo;
    private RecyclerView recyclerMyRecipes;
    private RecipeAdapter myRecipesAdapter;
    private boolean isFabMenuVisible = false;
    private static final int CREATE_RECIPE_REQUEST = 1001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        collectionRepo = CollectionRepository.getInstance(requireContext());
        userRecipeRepo = UserRecipeRepository.getInstance(requireContext());
        setupRecyclerView();
        setupUI();
        setupFabMenu();
        fetchRecipes();
        loadMyRecipes();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_RECIPE_REQUEST && resultCode == RESULT_OK) {
            loadMyRecipes();
        }
    }

    private void setupFabMenu() {
        final FloatingActionButton fabMain = binding.fabMain;
        final View fabMenu = binding.fabMenu;
        final View menuCreate = binding.menuCreateRecipe;
        final View menuCollection = binding.menuMyCollection;

        fabMenu.setVisibility(View.GONE);
        fabMenu.setAlpha(0f);
        fabMenu.setTranslationY(100f);

        fabMain.setOnClickListener(v -> toggleFabMenu(fabMenu, fabMain));

        menuCreate.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), CreateRecipeActivity.class));
            hideFabMenu(fabMenu, fabMain);
        });

        menuCollection.setOnClickListener(v -> {
            navigateToCollection();
            hideFabMenu(fabMenu, fabMain);
        });

        binding.getRoot().setOnClickListener(v -> {
            if (isFabMenuVisible) {
                hideFabMenu(fabMenu, fabMain);
            }
        });
    }

    private void toggleFabMenu(View fabMenu, FloatingActionButton fabMain) {
        if (isFabMenuVisible) {
            hideFabMenu(fabMenu, fabMain);
        } else {
            showFabMenu(fabMenu, fabMain);
        }
    }

    private void showFabMenu(View fabMenu, FloatingActionButton fabMain) {
        isFabMenuVisible = true;
        fabMenu.setVisibility(View.VISIBLE);
        fabMenu.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(200)
                .start();

        fabMain.animate()
                .rotationBy(180f)
                .setDuration(200)
                .start();
    }

    private void hideFabMenu(View fabMenu, FloatingActionButton fabMain) {
        isFabMenuVisible = false;
        fabMenu.animate()
                .alpha(0f)
                .translationY(100f)
                .setDuration(200)
                .withEndAction(() -> fabMenu.setVisibility(View.GONE))
                .start();

        fabMain.animate()
                .rotationBy(180f)
                .setDuration(200)
                .start();
    }

    private void setupRecyclerView() {
        recyclerRecipes = binding.recyclerRecipes;
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new RecipeAdapter(RecipeDataSource.getInstance().getApiRecipes(),
                collectionRepo, (recipe, position) -> {
            toggleCollectionStatus(recipe, position);
        });
        recyclerRecipes.setAdapter(adapter);


        recyclerMyRecipes = binding.recyclerMyRecipes;
        recyclerMyRecipes.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        myRecipesAdapter = new RecipeAdapter(
                new ArrayList<>(),
                collectionRepo,
                (recipe, position) -> toggleCollectionStatus(recipe, position)
        );
        recyclerMyRecipes.setAdapter(myRecipesAdapter);
        loadMyRecipes();
    }

    private void toggleCollectionStatus(Recipe recipe, int position) {
        CollectionRepository collectionRepo = CollectionRepository.getInstance(requireContext());

        if (collectionRepo.isRecipeSaved(recipe)) {
            collectionRepo.removeRecipe(recipe);
        } else {
            collectionRepo.saveRecipe(recipe);
        }

        adapter.notifyItemChanged(position);
        myRecipesAdapter.notifyItemChanged(position);
    }

    private void fetchRecipes() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpoonacularApi api = retrofit.create(SpoonacularApi.class);
        api.getRandomRecipes("bb3cda5f6f7947468b31a3612d552fff", 10)
                .enqueue(new Callback<RecipeResponse>() {
                    @Override
                    public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Recipe> newRecipes = response.body().getRecipes();
                            RecipeDataSource.getInstance().addApiRecipes(newRecipes);
                            adapter.updateData(RecipeDataSource.getInstance().getApiRecipes());
                        }
                    }

                    @Override
                    public void onFailure(Call<RecipeResponse> call, Throwable t) {
                        Toast.makeText(requireContext(), "Error loading recipes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupUI() {
        binding.settingsButton.setOnClickListener(v -> {
            try {
                openSettings();
            } catch (Exception e) {
                showErrorToast();
            }
        });

        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = v.getText().toString().trim();
                if (!query.isEmpty()) navigateToSearchResults(query);
                return true;
            }
            return false;
        });

        TextInputEditText searchEditText = binding.searchLayout.findViewById(R.id.searchEditText);
        searchEditText.clearFocus();

        binding.seeAll.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AllRecipesFragment())
                    .addToBackStack("all_recipes")
                    .commit();
        });
    }

    private void navigateToCollection() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CollectionFragment())
                .addToBackStack("collection")
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMyRecipes();
    }

    private void loadMyRecipes() {
        List<Recipe> userRecipes = UserRecipeRepository.getInstance(requireContext())
                .getAllUserRecipes();
        myRecipesAdapter.updateData(userRecipes);
    }

    private void openSettings() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .addToBackStack("settings")
                .commit();
    }

    private void navigateToSearchResults(String query) {
        Bundle args = new Bundle();
        args.putString("search_query", query);

        SearchResultsFragment fragment = new SearchResultsFragment();
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("search_results")
                .commit();
    }

    private void showErrorToast() {
        Toast.makeText(requireContext(), "Error opening settings", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}