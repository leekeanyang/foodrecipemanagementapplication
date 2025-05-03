package com.example.foodrecipemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodrecipemanagement.databinding.FragmentMealPlanBinding;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class MealPlanFragment extends Fragment implements MealPlanAdapter.OnDayClickListener {

    private FragmentMealPlanBinding binding;
    private MealPlanAdapter mealPlanAdapter;
    private List<MealPlanDay> currentWeekDays;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMealPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(); // Toolbar setup remains for title etc.
        setupRecyclerView();
        loadCurrentWeek();
        setupNoteButton();
        setupChatButton(); // *** Add call to setup the new chat button ***
    }

    private void setupToolbar() {
        // *** Remove menu inflation and listener from Toolbar ***
        binding.toolbarMealPlan.setTitle(R.string.meal_plan_title); // Or "My Plan" if you changed the string
        // binding.toolbarMealPlan.getMenu().clear(); // No longer needed
        // binding.toolbarMealPlan.inflateMenu(R.menu.meal_plan_menu); // No longer needed
        // binding.toolbarMealPlan.setOnMenuItemClickListener(item -> { ... }); // No longer needed
    }

    // --- setupRecyclerView, loadCurrentWeek, setupNoteButton remain the same ---
    private void setupRecyclerView() {
        currentWeekDays = new ArrayList<>();
        mealPlanAdapter = new MealPlanAdapter(currentWeekDays, this);
        binding.recyclerViewMealPlanWeek.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewMealPlanWeek.setAdapter(mealPlanAdapter);
    }

    private void loadCurrentWeek() {
        currentWeekDays.clear();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = monday.plusDays(i);
            currentWeekDays.add(new MealPlanDay(currentDay));
        }
        mealPlanAdapter.notifyDataSetChanged();
    }

    private void setupNoteButton() {
        binding.buttonGoToNotes.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NoteListActivity.class);
            startActivity(intent);
        });
    }

    // *** Add setup for the new Chat Button ***
    private void setupChatButton() {
        binding.buttonGoToChat.setOnClickListener(v -> {
            // Assuming you renamed ChatActivity to AIChatActivity
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            startActivity(intent);
        });
    }

    // --- onDayClick, onDestroyView remain the same ---
    @Override
    public void onDayClick(MealPlanDay mealPlanDay) {
        Intent intent = new Intent(requireContext(), MealPlanEditActivity.class);
        intent.putExtra(MealPlanEditActivity.EXTRA_DATE_REPO_STRING, mealPlanDay.getDateStringForRepo());
        intent.putExtra(MealPlanEditActivity.EXTRA_DATE_DISPLAY_STRING, mealPlanDay.getFormattedDate());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}