package com.example.foodrecipemanagement;

import com.example.foodrecipemanagement.database.CartItem;

public interface ItemActionListener {
    /** Called when the user taps the tick button */
    void onTogglePurchased(CartItem item);
    /** Called when the user taps the edit button */
    void onEditItem(CartItem item);
}
