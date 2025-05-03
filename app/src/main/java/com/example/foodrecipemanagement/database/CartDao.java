package com.example.foodrecipemanagement.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {
    @Insert
    long insertItem(CartItem item);

    @Update
    void updateItem(CartItem item);

    @Delete
    void deleteItem(CartItem item);

    // NOW ORDERED BY targetDate ASC instead of dateAdded
    @Query("SELECT * FROM cart_items WHERE status = 'ACTIVE' ORDER BY targetDate ASC")
    LiveData<List<CartItem>> getAllActiveItems();

    // Archived items also sorted by targetDate ASC
    @Query("SELECT * FROM cart_items WHERE status = 'ARCHIVED' ORDER BY targetDate ASC")
    LiveData<List<CartItem>> getAllArchivedItems();

    @Query("SELECT * FROM cart_items WHERE status = 'ACTIVE' ORDER BY category")
    LiveData<List<CartItem>> getActiveItemsByCategory();

    @Query("SELECT * FROM cart_items WHERE id = :id")
    LiveData<CartItem> getItemById(int id);

    @Query("UPDATE cart_items SET status = :status WHERE id = :id")
    void updateItemStatus(int id, ItemStatus status);

    @Query("UPDATE cart_items SET isPurchased = :isPurchased WHERE id = :id")
    void updatePurchaseStatus(int id, boolean isPurchased);

    @Query("SELECT DISTINCT category FROM cart_items WHERE status = 'ACTIVE'")
    LiveData<List<String>> getUniqueCategories();

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM cart_items WHERE targetDate BETWEEN :start AND :end ORDER BY targetDate ASC")
    LiveData<List<CartItem>> getItemsByDateRange(long start, long end);

    @Insert
    void insertCategory(Category category);

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT name, category, SUM(CAST(amount AS INTEGER)) AS totalAmount, metric, notes " +
            "FROM cart_items " +
            "WHERE status = 'ACTIVE' " +
            "GROUP BY name, category, metric, notes")
    LiveData<List<CategoryGroup>> getGroupedItemsByCategory();

    @Query("SELECT COUNT(*) FROM cart_items")
    int countItems();

    @Query("UPDATE cart_items SET status = 'ARCHIVED' WHERE category = :category")
    void archiveByCategory(String category);
}