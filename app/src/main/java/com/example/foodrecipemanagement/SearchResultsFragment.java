package com.example.foodrecipemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodrecipemanagement.databinding.FragmentSearchResultsBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultsFragment extends Fragment {
    private FragmentSearchResultsBinding binding;
    private SearchResultsAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchResultsBinding.inflate(inflater, container, false);
        setupRecyclerView();
        performSearch();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SearchResultsAdapter(new ArrayList<>());
        binding.recyclerResults.setAdapter(adapter);
        binding.recyclerResults.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private void performSearch() {
        String query = getArguments() != null ? getArguments().getString("search_query") : "";

        showLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpoonacularApi api = retrofit.create(SpoonacularApi.class);
        api.searchRecipes("bb3cda5f6f7947468b31a3612d552fff", query, 20, true)
                .enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Recipe> results = response.body().getResults();
                            if (results.isEmpty()) {
                                showEmptyState();
                            } else {
                                binding.emptyState.setVisibility(View.GONE);
                                adapter.updateData(results);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.recyclerResults.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        binding.emptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        binding.emptyState.setVisibility(View.VISIBLE);
        binding.recyclerResults.setVisibility(View.GONE);
    }
}
