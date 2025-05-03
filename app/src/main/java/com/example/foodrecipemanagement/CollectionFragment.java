package com.example.foodrecipemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodrecipemanagement.databinding.FragmentCollectionBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class CollectionFragment extends Fragment implements CollectionAdapter.OnRecipeRemovedListener {
    private FragmentCollectionBinding binding;
    private CollectionAdapter adapter;
    private CollectionRepository collectionRepo;
    private RecipeDataSource recipeDataSource;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCollectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = binding.toolbar;
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        collectionRepo = CollectionRepository.getInstance(requireContext());
        recipeDataSource = new RecipeDataSource();

        setupRecyclerView();
        loadCollection();
        setupEmptyState();
    }

    private void setupRecyclerView() {
        adapter = new CollectionAdapter(new ArrayList<>(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadCollection() {
        List<Recipe> savedRecipes = collectionRepo.getAllSavedRecipes();
        adapter.updateData(savedRecipes);
        updateEmptyState(savedRecipes.isEmpty());
    }

    private void setupEmptyState() {
        binding.emptyState.setText("No recipes in your collection yet");
    }

    private void updateEmptyState(boolean isEmpty) {
        binding.recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRecipeRemoved(Recipe recipe) {
        collectionRepo.removeRecipe(recipe);
        loadCollection();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}