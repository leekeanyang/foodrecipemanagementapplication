package com.example.foodrecipemanagement;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodrecipemanagement.database.CartItem;

public class CartFragment extends Fragment {
    private CartViewModel viewModel;
    private boolean sortByCategory = false; // start with date sort

    public CartFragment() { /* Required empty constructor */ }

    @Nullable @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        ImageButton sortBtn    = v.findViewById(R.id.sortButton);
        ImageButton addBtn     = v.findViewById(R.id.addButton);
        ImageButton archiveBtn = v.findViewById(R.id.archiveButton);
        ImageButton moreBtn    = v.findViewById(R.id.overflowMenu);

        sortBtn.setOnClickListener(__ -> {
            sortByCategory = !sortByCategory;
            loadChildFragment();
        });

        addBtn.setOnClickListener(__ -> {
            AddItemDialogFragment dlg = new AddItemDialogFragment();
            // preload categories before showing
            viewModel.getUniqueCategories().observe(getViewLifecycleOwner(), cats -> {
                dlg.setCategories(cats);
                dlg.setOnItemAddedListener(item -> viewModel.insert(item));
                dlg.show(getChildFragmentManager(), "add_item");
            });
        });

        archiveBtn.setOnClickListener(__ -> {
            // TODO: your archive logic here
        });

        moreBtn.setOnClickListener(__ -> {
            PopupMenu popup = new PopupMenu(requireContext(), moreBtn);
            popup.inflate(R.menu.cart_overflow_menu);
            popup.show();
        });

        // initial child
        loadChildFragment();
    }

    private void loadChildFragment() {
        Fragment child = sortByCategory
                ? new CategorySortedFragment()
                : new DateSortedFragment();

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.contentContainer, child);
        ft.commit();
    }
}