package com.example.foodrecipemanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setupToolbar(view);
        setupMenuItems(view);
        return view;
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void setupMenuItems(View view) {
        ViewGroup container = view.findViewById(R.id.settings_container);

        addSettingItem(container, R.drawable.ic_login, "Login/Sign Up", v -> {
            startActivity(new Intent(requireActivity(), AuthActivity.class));
        });

        addSettingItem(container, R.drawable.ic_info, "About Us", v -> {
            startActivity(new Intent(requireActivity(), AboutActivity.class));
        });

        addSettingItem(container, R.drawable.ic_contact, "Contact Us", v -> {
            startActivity(new Intent(requireActivity(), ContactActivity.class));
        });

        addSettingItem(container, R.drawable.ic_logout, "Log Out", v -> {
            performLogout();
        });
    }

    private void addSettingItem(ViewGroup parent, int iconRes, String title, View.OnClickListener listener) {
        View item = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_setting, parent, false);

        ImageView icon = item.findViewById(R.id.icon);
        TextView titleView = item.findViewById(R.id.title);

        icon.setImageResource(iconRes);
        titleView.setText(title);
        item.setOnClickListener(listener);

        parent.addView(item);
    }

    private void performLogout() {
        SharedPreferences userPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userPrefs.edit().remove("current_user_id").apply();

        CollectionRepository.getInstance(requireContext()).clearCache();
        UserRecipeRepository.getInstance(requireContext()).clearCache();

        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(requireActivity(), AuthActivity.class));
        requireActivity().finish();
    }
}