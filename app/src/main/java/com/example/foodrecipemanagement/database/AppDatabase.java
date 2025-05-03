package com.example.foodrecipemanagement.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Executors;

@Database(
        entities = {CartItem.class, Category.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract CartDao cartDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "shopping_list_db")
                            .fallbackToDestructiveMigration()  // will wipe & recreate on version change
                            .build();
                }
            }
        }

        // seed dummy data exactly once after recreation
        Executors.newSingleThreadExecutor().execute(() -> {
            CartDao dao = INSTANCE.cartDao();
            if (dao.countItems() == 0) {
                // --- MARCH 2025 entries ---
                dao.insertItem(new CartItem("Apple",      "Fruit",      "3",   "pcs", getDate("2025-03-03"), null, null));
                dao.insertItem(new CartItem("Apple",      "Fruit",      "1",   "pcs", getDate("2025-03-06"), null, null));
                dao.insertItem(new CartItem("Apple",      "Fruit",      "1",   "pcs", getDate("2025-03-12"), null, null));
                dao.insertItem(new CartItem("Strawberry", "Fruit",    "100",   "g",  getDate("2025-03-04"), null, null));
                dao.insertItem(new CartItem("Strawberry", "Fruit",    "300",   "g",  getDate("2025-03-06"), null, null));
                dao.insertItem(new CartItem("Soda",       "Drink",      "2", "cans", getDate("2025-03-08"), "Low sugar", null));
                dao.insertItem(new CartItem("Bread",      "Bakery",     "1",  "loaf",getDate("2025-03-09"), null, null));
                dao.insertItem(new CartItem("Toothpaste", "Toiletries","1",  "tube",getDate("2025-03-07"), null, null));
                dao.insertItem(new CartItem("Eggs",       "Dairy",     "12", "pcs",  getDate("2025-03-05"), null, null));

                // --- APRIL 2025 entries ---
                dao.insertItem(new CartItem("Milk",       "Dairy",      "2",   "l",   getDate("2025-04-02"), null, null));
                dao.insertItem(new CartItem("Cheese",     "Dairy",    "200",   "g",   getDate("2025-04-15"), null, null));

                // --- JUNE 2025 entries ---
                dao.insertItem(new CartItem("Tomatoes",   "Vegetable",  "5",  "pcs",  getDate("2025-06-01"), null, null));
                dao.insertItem(new CartItem("Cucumber",   "Vegetable",  "3",  "pcs",  getDate("2025-06-10"), null, null));
            }
        });

        return INSTANCE;
    }

    private static long getDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.parse(dateStr).getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}
