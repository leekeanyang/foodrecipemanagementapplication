package com.example.foodrecipemanagement.database;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRepository {
    private final CartDao cartDao;
    private final LiveData<List<CartItem>> allActiveItems;
    private final LiveData<List<CartItem>> allArchivedItems;
    private final LiveData<List<String>> uniqueCategories;
    private final LiveData<List<Category>> allCategories;
    private final ExecutorService executor;

    public CartRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        cartDao = database.cartDao();
        allActiveItems   = cartDao.getAllActiveItems();
        allArchivedItems = cartDao.getAllArchivedItems();
        uniqueCategories = cartDao.getUniqueCategories();
        allCategories    = cartDao.getAllCategories();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<CartItem>> getAllActiveItems() {
        return allActiveItems;
    }

    public LiveData<List<CartItem>> getAllArchivedItems() {
        return allArchivedItems;
    }

    public LiveData<List<String>> getUniqueCategories() {
        return uniqueCategories;
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<CartItem>> getActiveItemsByCategory() {
        return cartDao.getActiveItemsByCategory();
    }

    public LiveData<List<CartItem>> getItemsByDateRange(long start, long end) {
        return cartDao.getItemsByDateRange(start, end);
    }

    public LiveData<List<CategoryGroup>> getGroupedItemsByCategory() {
        return cartDao.getGroupedItemsByCategory();
    }

    /** NEW: expose single‐item lookup by ID */
    public LiveData<CartItem> getItemById(int id) {
        return cartDao.getItemById(id);
    }

    // ✅ NEW: Group items by category into a Map for UI
    public LiveData<Map<String, List<CartItem>>> getItemsGroupedByCategoryMap() {
        return Transformations.map(allActiveItems, items -> {
            Map<String, List<CartItem>> grouped = new LinkedHashMap<>();
            for (CartItem item : items) {
                String cat = item.getCategory() != null
                        ? item.getCategory() : "Uncategorized";
                grouped.computeIfAbsent(cat, k -> new ArrayList<>())
                        .add(item);
            }
            return grouped;
        });
    }

    public void insert(CartItem item) {
        executor.execute(() -> cartDao.insertItem(item));
    }

    public void update(CartItem item) {
        executor.execute(() -> cartDao.updateItem(item));
    }

    public void delete(CartItem item) {
        executor.execute(() -> cartDao.deleteItem(item));
    }

    public void updateItemStatus(int id, ItemStatus status) {
        executor.execute(() -> cartDao.updateItemStatus(id, status));
    }

    public void archiveItem(int id) {
        executor.execute(() -> cartDao.updateItemStatus(id, ItemStatus.ARCHIVED));
    }

    public void updatePurchaseStatus(int id, boolean isPurchased) {
        executor.execute(() -> cartDao.updatePurchaseStatus(id, isPurchased));
    }

    public void archiveByCategory(String category) {
        executor.execute(() -> cartDao.archiveByCategory(category));
    }
}
