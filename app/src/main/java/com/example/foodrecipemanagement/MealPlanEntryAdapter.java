package com.example.foodrecipemanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MealPlanEntryAdapter extends RecyclerView.Adapter<MealPlanEntryAdapter.ViewHolder> {

    private final List<MealPlanEntry> entries;
    private final OnMealEntryDeleteListener deleteListener;
    private final OnMealEntryClickListener clickListener;
    private final Context context;

    // *** New: Interface for project click listener ***
    public interface OnMealEntryClickListener {
        void onMealEntryClick(MealPlanEntry entry);
    }

    public interface OnMealEntryDeleteListener {
        void onMealEntryDelete(MealPlanEntry entry, int position);
    }

    // *** Modification: Added new clickListener to the constructor***
    public MealPlanEntryAdapter(Context context, List<MealPlanEntry> entries,
                                OnMealEntryDeleteListener deleteListener,
                                OnMealEntryClickListener clickListener) { // Add clickListener
        this.context = context;
        this.entries = entries;
        this.deleteListener = deleteListener;
        this.clickListener = clickListener; // Initialize clickListener
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_meal_plan_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealPlanEntry entry = entries.get(position);
        // *** Edit: Pass clickListener to bind method ***
        holder.bind(entry, context, deleteListener, clickListener);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void updateEntries(List<MealPlanEntry> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        notifyDataSetChanged();
    }

    public void addEntry(MealPlanEntry entry) {
        entries.add(entry);
        notifyItemInserted(entries.size() - 1);
    }

    public void removeEntry(int position) {
        if (position >= 0 && position < entries.size()) {
            entries.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, entries.size());
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImageView;
        TextView titleTextView;
        TextView tagTextView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.imageView_meal_entry_recipe);
            titleTextView = itemView.findViewById(R.id.textView_meal_entry_title);
            tagTextView = itemView.findViewById(R.id.textView_meal_entry_tag);
            deleteButton = itemView.findViewById(R.id.button_delete_meal_entry);
        }

        // *** Modification: Add clickListener to bind method ***
        public void bind(final MealPlanEntry entry, Context context,
                         final OnMealEntryDeleteListener deleteListener,
                         final OnMealEntryClickListener clickListener) { // add clickListener

            titleTextView.setText(entry.getRecipeTitle());
            tagTextView.setText(entry.getTag());

            Glide.with(context)
                    .load(entry.getRecipeImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(recipeImageView);

            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        deleteListener.onMealEntryDelete(entry, currentPosition);
                    }
                }
            });

            // *** New: Set the click event of the entire item ***
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int currentPosition = getAdapterPosition(); // Make sure the location is valid
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        clickListener.onMealEntryClick(entry); // Call interface method
                    }
                }
            });
        }
    }
}