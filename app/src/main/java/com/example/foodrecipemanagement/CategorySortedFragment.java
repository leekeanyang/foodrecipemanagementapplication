package com.example.foodrecipemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipemanagement.database.CartItem;

import java.util.List;
import java.util.Map;

public class CategorySortedFragment extends Fragment implements ItemActionListener {
    private CartViewModel viewModel;
    private RecyclerView recyclerView;
    private CategorySortedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_category_sorted, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.categoryRecyclerView);
        adapter = new CategorySortedAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity())
                .get(CartViewModel.class);
        viewModel.getGroupedItemsByCategoryMap().observe(
                getViewLifecycleOwner(),
                (Map<String, List<CartItem>> grouped) -> {
                    adapter.setData(grouped);
                }
        );
    }

    @Override
    public void onTogglePurchased(CartItem item) {
        viewModel.updatePurchaseStatus(item.getId(), !item.isPurchased());
    }

    @Override
    public void onEditItem(CartItem item) {
        EditItemDialogFragment dlg =
                EditItemDialogFragment.newInstance(item.getId());
        dlg.setOnItemEditedListener(updated -> viewModel.update(updated));
        dlg.show(getChildFragmentManager(), "edit_item");
    }
}
