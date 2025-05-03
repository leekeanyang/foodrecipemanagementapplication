package com.example.foodrecipemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipemanagement.database.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategorySortedAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM   = 1;

    private final List<Object> displayList = new ArrayList<>();
    private final ItemActionListener listener;

    public CategorySortedAdapter(ItemActionListener listener) {
        this.listener = listener;
    }

    public void setData(Map<String, List<CartItem>> grouped) {
        displayList.clear();
        if (grouped != null) {
            for (String category : grouped.keySet()) {
                displayList.add(category);
                List<CartItem> items = grouped.get(category);
                if (items != null) displayList.addAll(items);
            }
        }
        notifyDataSetChanged();
    }

    @Override public int getItemCount()         { return displayList.size(); }
    @Override public int getItemViewType(int p) {
        return displayList.get(p) instanceof String
                ? TYPE_HEADER
                : TYPE_ITEM;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType
    ) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderHolder(
                    inf.inflate(R.layout.item_category_header, parent, false));
        } else {
            return new ItemHolder(
                    inf.inflate(R.layout.item_cart_fullwidth, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder, int pos
    ) {
        if (getItemViewType(pos) == TYPE_HEADER) {
            ((HeaderHolder) holder).title
                    .setText((String) displayList.get(pos));
        } else {
            CartItem ci = (CartItem) displayList.get(pos);
            ItemHolder h = (ItemHolder) holder;

            h.name.setText(ci.getName());
            h.amount.setText(ci.getAmount() + " " + ci.getMetric());
            if (ci.getNotes() != null && !ci.getNotes().isEmpty()) {
                h.note.setVisibility(View.VISIBLE);
                h.note.setText(ci.getNotes());
            } else {
                h.note.setVisibility(View.GONE);
            }

            // TINT THE TICK ICON
            int tickColor = ci.isPurchased()
                    ? ContextCompat.getColor(h.buttonTick.getContext(),
                    R.color.tick_selected)
                    : ContextCompat.getColor(h.buttonTick.getContext(),
                    R.color.tick_default);
            h.buttonTick.setColorFilter(tickColor);

            // CLICK HANDLERS
            h.buttonTick.setOnClickListener(v ->
                    listener.onTogglePurchased(ci));
            h.buttonEdit.setOnClickListener(v ->
                    listener.onEditItem(ci));

            if (ci.getImagePath() != null && !ci.getImagePath().isEmpty()) {
                Glide.with(h.image.getContext())
                        .load(ci.getImagePath())
                        .into(h.image);
            } else {
                h.image.setImageResource(R.drawable.ic_placeholder_image);
            }
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView title;
        HeaderHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.textCategoryTitle);
        }
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, amount, note;
        ImageButton buttonTick, buttonEdit;

        ItemHolder(@NonNull View v) {
            super(v);
            image      = v.findViewById(R.id.imageItem);
            name       = v.findViewById(R.id.textItemName);
            amount     = v.findViewById(R.id.textItemAmount);
            note       = v.findViewById(R.id.textItemNote);
            buttonTick = v.findViewById(R.id.buttonTick);
            buttonEdit = v.findViewById(R.id.buttonEdit);
        }
    }
}
