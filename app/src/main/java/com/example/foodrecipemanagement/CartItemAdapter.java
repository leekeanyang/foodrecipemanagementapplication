package com.example.foodrecipemanagement;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipemanagement.database.CartItem;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    private List<CartItem> cartItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CartItem item);
        void onArchiveClick(CartItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public CartItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem currentItem = cartItems.get(position);
        holder.itemName.setText(currentItem.getName());
        holder.itemQuantity.setText(currentItem.getAmount() + " " + currentItem.getMetric());

        if (currentItem.getImagePath() != null && !currentItem.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(currentItem.getImagePath()))
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(currentItem);
        });
        holder.archiveButton.setOnClickListener(v -> {
            if (listener != null) listener.onArchiveClick(currentItem);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        ImageButton archiveButton;
        ImageView itemImage;

        CartItemViewHolder(View view) {
            super(view);
            itemName      = view.findViewById(R.id.item_name);
            itemQuantity  = view.findViewById(R.id.item_quantity);
            archiveButton = view.findViewById(R.id.archive_button);
            itemImage     = view.findViewById(R.id.item_image);
        }
    }
}
