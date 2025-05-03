package com.example.foodrecipemanagement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class MealPlanDay {
    private LocalDate date;
    private String dayName;
    private String formattedDate;
    private String dateStringForRepo;

    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault());
    // Formatter for Repository Key
    private static final DateTimeFormatter REPO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    public MealPlanDay(LocalDate date) {
        this.date = date;
        // Get the name of the day of the week (e.g. "Monday")
        this.dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        // Formats a date for display (e.g. "April 28, 2025")
        this.formattedDate = date.format(DISPLAY_DATE_FORMATTER);
        // Formatting dates for use in Repository
        this.dateStringForRepo = date.format(REPO_DATE_FORMATTER);
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDayName() {
        return dayName;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getDateStringForRepo() {
        return dateStringForRepo;
    }
}