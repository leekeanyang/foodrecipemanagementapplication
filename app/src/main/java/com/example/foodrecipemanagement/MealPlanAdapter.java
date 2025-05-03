package com.example.foodrecipemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.ViewHolder> {

    private final List<MealPlanDay> weekDays;
    private final OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(MealPlanDay mealPlanDay);
    }

    public MealPlanAdapter(List<MealPlanDay> weekDays, OnDayClickListener listener) {
        this.weekDays = weekDays;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_plan_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealPlanDay day = weekDays.get(position);
        holder.bind(day, listener);
    }

    @Override
    public int getItemCount() {
        return weekDays.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayNameTextView;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNameTextView = itemView.findViewById(R.id.textView_day_name);
            dateTextView = itemView.findViewById(R.id.textView_date);
        }

        public void bind(final MealPlanDay mealPlanDay, final OnDayClickListener listener) {
            dayNameTextView.setText(mealPlanDay.getDayName());
            dateTextView.setText(mealPlanDay.getFormattedDate());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDayClick(mealPlanDay);
                }
            });
        }
    }
}