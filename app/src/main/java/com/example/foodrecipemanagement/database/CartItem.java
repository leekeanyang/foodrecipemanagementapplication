package com.example.foodrecipemanagement.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "cart_items")
@TypeConverters(Converters.class)
public class CartItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String category;
    private String amount;
    private String metric;
    private long targetDate;
    private String notes;
    private String imagePath;
    private ItemStatus status;
    private boolean isPurchased;
    private long dateAdded; // NEW FIELD

    public CartItem(String name, String category, String amount, String metric, long targetDate,
                    String notes, String imagePath) {
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.metric = metric;
        this.targetDate = targetDate;
        this.notes = notes;
        this.imagePath = imagePath;
        this.status = ItemStatus.ACTIVE;
        this.isPurchased = false;
        this.dateAdded = System.currentTimeMillis(); // set when created
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public String getCategory() { return category; }

    public String getAmount() { return amount; }

    public String getMetric() { return metric; }

    public long getTargetDate() { return targetDate; }

    public String getNotes() { return notes; }

    public String getImagePath() { return imagePath; }

    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public boolean isPurchased() { return isPurchased; }
    public void setPurchased(boolean purchased) { isPurchased = purchased; }

    public long getDateAdded() { return dateAdded; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }
}