package com.example.foodrecipemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommunityFragment extends Fragment {
    private RecyclerView recyclerCommunities;
    private CommunityAdapter communityAdapter;
    private FloatingActionButton fabCreateCommunity;
    private CommunityRepository communityRepo;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private SearchView searchView;
    private List<Community> allCommunities;
    private FirebaseFirestore db;
    private final boolean useFirebase = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        communityRepo = CommunityRepository.getInstance(requireContext());
        recyclerCommunities = view.findViewById(R.id.recycler_communities);
        fabCreateCommunity = view.findViewById(R.id.fab_create_community);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        emptyView = view.findViewById(R.id.empty_view);
        searchView = view.findViewById(R.id.search_view);

        recyclerCommunities.setContentDescription("List of communities");
        fabCreateCommunity.setContentDescription("Create new community");
        emptyView.setContentDescription("No communities message");

        setupRecyclerView();
        if (useFirebase) {
            setupFirebaseListener();
        } else {
            loadCommunitiesFromRepository();
        }

        setupFab();
        setupSwipeRefresh();
        setupSearchView();

        return view;
    }

    private void setupRecyclerView() {
        recyclerCommunities.setLayoutManager(new LinearLayoutManager(requireContext()));
        allCommunities = new ArrayList<>();
        communityAdapter = new CommunityAdapter(allCommunities, community -> {
            android.util.Log.d("CommunityFragment", "Launching CommunityDetailActivity with name: " + community.getName());
            Intent intent = new Intent(requireContext(), CommunityDetailActivity.class);
            intent.putExtra("community_id", community.getId());
            intent.putExtra("community_name", community.getName());
            startActivity(intent);
        }, community -> {
            EditCommunityDialog dialog = new EditCommunityDialog();
            dialog.setCommunity(community);
            dialog.setOnCommunityEditedListener(editedCommunity -> {
                communityRepo.updateCommunity(editedCommunity);
                if (!useFirebase) {
                    loadCommunitiesFromRepository();
                }
            });
            dialog.show(getParentFragmentManager(), "edit_community");
        }, community -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Community")
                .setMessage("Are you sure you want to delete " + community.getName() + "? This will also delete all associated recipes and questions.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    communityRepo.deleteCommunity(community.getId());
                    if (!useFirebase) {
                        loadCommunitiesFromRepository();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show());
        recyclerCommunities.setAdapter(communityAdapter);
        updateEmptyView(allCommunities);
    }

    private void setupFab() {
        fabCreateCommunity.setOnClickListener(v -> {
            CreateCommunityDialog dialog = new CreateCommunityDialog();
            dialog.setOnCommunityCreatedListener(community -> {
                android.util.Log.d("CommunityFragment", "Adding community: " + community.getName() + " with ID: " + community.getId());
                communityRepo.addCommunity(community);
                if (!useFirebase) {
                    loadCommunitiesFromRepository();
                }
            });
            dialog.show(getParentFragmentManager(), "create_community");
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!useFirebase) {
                loadCommunitiesFromRepository();
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCommunities(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCommunities(newText);
                return true;
            }
        });
    }

    private void loadCommunitiesFromRepository() {
        List<Community> communities = communityRepo != null ? communityRepo.getCommunities() : new ArrayList<>();
        android.util.Log.d("CommunityFragment", "Loaded communities from SQLite: " + communities.size());
        // Deduplicate based on ID
        Set<Integer> seenIds = new HashSet<>();
        allCommunities.clear();
        for (Community community : communities) {
            if (seenIds.add(community.getId())) {
                allCommunities.add(community);
            } else {
                android.util.Log.w("CommunityFragment", "Duplicate community ID found in SQLite: " + community.getId());
            }
        }
        communityAdapter.updateData(allCommunities);
        updateEmptyView(allCommunities);
    }

    private void setupFirebaseListener() {
        try {
            db = FirebaseFirestore.getInstance();
            db.collection("communities")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            android.util.Log.e("CommunityFragment", "Firebase error: " + error.getMessage());
                            loadCommunitiesFromRepository();
                            return;
                        }
                        android.util.Log.d("CommunityFragment", "Firebase snapshot received with documents: " + (value != null ? value.size() : 0));
                        Set<Integer> seenIds = new HashSet<>();
                        allCommunities.clear();
                        if (value != null) {
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                int id = Integer.parseInt(doc.getId());
                                if (seenIds.add(id)) {
                                    String name = doc.getString("name");
                                    String description = doc.getString("description");
                                    boolean isPublic = Boolean.TRUE.equals(doc.getBoolean("isPublic"));
                                    Community community = new Community(id, name, description, isPublic);

                                    // Fetch recipe count
                                    db.collection("recipes")
                                            .whereEqualTo("communityId", id)
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                community.setRecipeCount(queryDocumentSnapshots.size());
                                                communityAdapter.notifyDataSetChanged();
                                            });

                                    // Fetch question count
                                    db.collection("questions")
                                            .whereEqualTo("communityId", id)
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                community.setQuestionCount(queryDocumentSnapshots.size());
                                                communityAdapter.notifyDataSetChanged();
                                            });

                                    allCommunities.add(community);
                                } else {
                                    android.util.Log.w("CommunityFragment", "Duplicate community ID found in Firebase: " + id);
                                }
                            }
                        }
                        communityAdapter.updateData(allCommunities);
                        updateEmptyView(allCommunities);
                    });
        } catch (Exception e) {
            android.util.Log.e("CommunityFragment", "Firebase setup error: " + e.getMessage());
            loadCommunitiesFromRepository();
        }
    }

    private void filterCommunities(String query) {
        List<Community> filteredList = new ArrayList<>();
        for (Community community : allCommunities) {
            if (community.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(community);
            }
        }
        communityAdapter.updateData(filteredList);
        updateEmptyView(filteredList);
    }

    private void updateEmptyView(List<Community> communities) {
        if (communities.isEmpty()) {
            recyclerCommunities.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerCommunities.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}