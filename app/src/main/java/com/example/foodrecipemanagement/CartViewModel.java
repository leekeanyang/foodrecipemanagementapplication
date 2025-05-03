package com.example.foodrecipemanagement;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.foodrecipemanagement.database.CartItem;
import com.example.foodrecipemanagement.database.CartRepository;
import com.example.foodrecipemanagement.database.Category;
import com.example.foodrecipemanagement.database.CategoryGroup;

import java.util.List;
import java.util.Map;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository repository;
    private final LiveData<List<CartItem>> allCartItems;
    private final LiveData<List<String>> uniqueCategories;
    private final LiveData<List<Category>> allCategories;
    private LiveData<List<CategoryGroup>> groupedItemsByCategory;
    private final LiveData<Map<String, List<CartItem>>> groupedItemsByCategoryMap;

    public CartViewModel(Application application) {
        super(application);
        repository = new CartRepository(application);
        allCartItems           = repository.getAllActiveItems();
        uniqueCategories       = repository.getUniqueCategories();
        allCategories          = repository.getAllCategories();
        groupedItemsByCategoryMap = repository.getItemsGroupedByCategoryMap();
    }

    public LiveData<List<CartItem>> getAllCartItems() {
        return allCartItems;
    }

    public LiveData<List<String>> getUniqueCategories() {
        return uniqueCategories;
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<CartItem>> getItemsByDateRange(long start, long end) {
        return repository.getItemsByDateRange(start, end);
    }

    public LiveData<List<CategoryGroup>> getGroupedItemsByCategory() {
        if (groupedItemsByCategory == null) {
            groupedItemsByCategory = repository.getGroupedItemsByCategory();
        }
        return groupedItemsByCategory;
    }

    // ✅ NEW: expose single‐item lookup by ID
    public LiveData<CartItem> getItemById(int id) {
        return repository.getItemById(id);
    }

    public LiveData<Map<String, List<CartItem>>> getGroupedItemsByCategoryMap() {
        return groupedItemsByCategoryMap;
    }

    public void insert(CartItem item) {
        repository.insert(item);
    }

    public void update(CartItem item) {
        repository.update(item);
    }

    public void delete(CartItem item) {
        repository.delete(item);
    }

    public void archiveItem(int id) {
        repository.archiveItem(id);
    }

    public void updatePurchaseStatus(int id, boolean isPurchased) {
        repository.updatePurchaseStatus(id, isPurchased);
    }

    public void archiveByCategory(String category) {
        repository.archiveByCategory(category);
    }
}
