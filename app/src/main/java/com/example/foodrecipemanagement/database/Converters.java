package com.example.foodrecipemanagement.database;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static ItemStatus toStatus(String value) {
        return value == null ? null : ItemStatus.valueOf(value);
    }

    @TypeConverter
    public static String fromStatus(ItemStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static ItemSource toSource(String value) {
        return value == null ? null : ItemSource.valueOf(value);
    }

    @TypeConverter
    public static String fromSource(ItemSource source) {
        return source == null ? null : source.name();
    }
}
