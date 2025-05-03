package com.example.foodrecipemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipemanagement.database.CartItem;

import java.util.List;

public class DateSortedFragment extends Fragment implements ItemActionListener {
    private CartViewModel viewModel;
    private RecyclerView recyclerView;
    private DateSortedAdapter adapter;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_date_sorted, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.dateRecyclerView);
        emptyView    = view.findViewById(R.id.emptyDateText);

        // Wire up adapter with our ItemActionListener
        adapter = new DateSortedAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity())
                .get(CartViewModel.class);
        viewModel.getAllCartItems().observe(
                getViewLifecycleOwner(),
                (List<CartItem> items) -> {
                    if (items != null && !items.isEmpty()) {
                        emptyView.setVisibility(View.GONE);
                        adapter.setData(items);
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    @Override
    public void onTogglePurchased(CartItem item) {
        viewModel.updatePurchaseStatus(item.getId(), !item.isPurchased());
    }

    @Override
    public void onEditItem(CartItem item) {
        // Pass only the ID into the dialog
        EditItemDialogFragment dlg =
                EditItemDialogFragment.newInstance(item.getId());
        dlg.setOnItemEditedListener(updated -> viewModel.update(updated));
        dlg.show(getChildFragmentManager(), "edit_item");
    }
}
