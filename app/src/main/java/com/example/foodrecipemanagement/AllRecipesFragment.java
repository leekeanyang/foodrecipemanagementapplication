package com.example.foodrecipemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodrecipemanagement.databinding.FragmentAllRecipesBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AllRecipesFragment extends Fragment implements CollectionAdapter.OnRecipeRemovedListener {
    private FragmentAllRecipesBinding binding;
    private CollectionAdapter adapter;
    private UserRecipeRepository userRecipeRepo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllRecipesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userRecipeRepo = UserRecipeRepository.getInstance(requireContext());

        MaterialToolbar toolbar = binding.toolbar;
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        setupRecyclerView();
        loadCreatedRecipes();
    }

    private void setupRecyclerView() {
        adapter = new CollectionAdapter(new ArrayList<>(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadCreatedRecipes() {
        List<Recipe> createdRecipes = userRecipeRepo.getAllUserRecipes();
        adapter.updateData(createdRecipes);
        binding.emptyState.setVisibility(createdRecipes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRecipeRemoved(Recipe recipe) {
        userRecipeRepo.removeRecipe(recipe);
        CollectionRepository.getInstance(requireContext()).removeRecipe(recipe);
        loadCreatedRecipes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}