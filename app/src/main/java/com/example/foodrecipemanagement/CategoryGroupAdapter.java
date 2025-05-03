package com.example.foodrecipemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipemanagement.database.CategoryGroup;

import java.util.ArrayList;
import java.util.List;

public class CategoryGroupAdapter
        extends RecyclerView.Adapter<CategoryGroupAdapter.GroupViewHolder> {

    private List<CategoryGroup> items = new ArrayList<>();

    public CategoryGroupAdapter() {
        // no-arg constructor
    }

    /** Swap in a new list of grouped items */
    public void setItems(List<CategoryGroup> items) {
        this.items = (items != null ? items : new ArrayList<>());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_group, parent, false);
        return new GroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        CategoryGroup cg = items.get(position);
        holder.name.setText(cg.getName());
        holder.amount.setText(cg.getTotalAmount() + " " + cg.getMetric());
        if (cg.getNotes() != null && !cg.getNotes().isEmpty()) {
            holder.notes.setVisibility(View.VISIBLE);
            holder.notes.setText(cg.getNotes());
        } else {
            holder.notes.setVisibility(View.GONE);
        }
        // placeholder image; replace with your own if you have one
        holder.image.setImageResource(R.drawable.ic_placeholder_image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, notes;
        ImageView image;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            name   = itemView.findViewById(R.id.itemNameText);
            amount = itemView.findViewById(R.id.itemAmountText);
            notes  = itemView.findViewById(R.id.itemNotesText);
            image  = itemView.findViewById(R.id.itemImageView);
        }
    }
}

