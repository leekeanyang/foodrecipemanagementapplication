package com.example.foodrecipemanagement.database;

public class CategoryGroup {
    private final String name;
    private final int totalAmount;
    private final String metric;
    private final String notes;

    public CategoryGroup(String name, int totalAmount, String metric, String notes) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.metric = metric;
        this.notes = notes;
    }

    public String getName() { return name; }
    public int getTotalAmount() { return totalAmount; }
    public String getMetric() { return metric; }
    public String getNotes() { return notes; }
}
