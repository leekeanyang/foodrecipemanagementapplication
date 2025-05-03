package com.example.foodrecipemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipemanagement.database.CartItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM   = 1;

    private final List<Object> displayList = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.getDefault());

    public DateGroupAdapter() { }

    public void setItems(List<CartItem> cartItems) {
        displayList.clear();
        if (cartItems != null) {
            Collections.sort(cartItems, (a, b) -> Long.compare(b.getTargetDate(), a.getTargetDate()));
            long lastDate = -1;
            for (CartItem ci : cartItems) {
                long date = ci.getTargetDate();
                if (date != lastDate) {
                    displayList.add(date);
                    lastDate = date;
                }
                displayList.add(ci);
            }
        }
        notifyDataSetChanged();
    }

    @Override public int getItemCount() { return displayList.size(); }

    @Override
    public int getItemViewType(int position) {
        return (displayList.get(position) instanceof Long) ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_date_header, parent, false);
            return new HeaderHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_cart_fullwidth, parent, false);
            return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        if (getItemViewType(pos) == TYPE_HEADER) {
            long date = (Long) displayList.get(pos);
            ((HeaderHolder) holder).header.setText(sdf.format(new Date(date)));
        } else {
            CartItem ci = (CartItem) displayList.get(pos);
            ItemHolder vh = (ItemHolder) holder;
            vh.name.setText(ci.getName());
            vh.amount.setText(ci.getAmount() + " " + ci.getMetric());
            if (ci.getNotes() != null && !ci.getNotes().isEmpty()) {
                vh.note.setVisibility(View.VISIBLE);
                vh.note.setText(ci.getNotes());
            } else {
                vh.note.setVisibility(View.GONE);
            }
            if (ci.getImagePath() != null && !ci.getImagePath().isEmpty()) {
                Glide.with(vh.image.getContext())
                        .load(ci.getImagePath())
                        .into(vh.image);
            } else {
                vh.image.setImageResource(R.drawable.ic_placeholder_image);
            }
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView header;
        HeaderHolder(@NonNull View view) {
            super(view);
            header = view.findViewById(R.id.dateHeaderText);
        }
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, amount, note;
        ItemHolder(@NonNull View view) {
            super(view);
            image  = view.findViewById(R.id.imageItem);
            name   = view.findViewById(R.id.textItemName);
            amount = view.findViewById(R.id.textItemAmount);
            note   = view.findViewById(R.id.textItemNote);
        }
    }
}