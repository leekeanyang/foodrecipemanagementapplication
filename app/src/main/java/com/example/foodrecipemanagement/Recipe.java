package com.example.foodrecipemanagement;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Recipe implements Parcelable {
    private int id;
    private String title;
    private String image;
    private int readyInMinutes;
    private int servings;
    private List<ExtendedIngredient> extendedIngredients;
    private String instructions;
    private String summary;
    private boolean isUserCreated;

    public Recipe(int id, String title, String image, int readyInMinutes, int servings,
                  List<ExtendedIngredient> extendedIngredients, String instructions, String summary, boolean isUserCreated) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.readyInMinutes = readyInMinutes;
        this.servings = servings;
        this.extendedIngredients = extendedIngredients;
        this.instructions = instructions;
        this.summary = summary;
        this.isUserCreated = isUserCreated;
    }

    protected Recipe(Parcel in) {
        id = in.readInt();
        title = in.readString();
        image = in.readString();
        readyInMinutes = in.readInt();
        servings = in.readInt();
        extendedIngredients = in.createTypedArrayList(ExtendedIngredient.CREATOR);
        instructions = in.readString();
        summary = in.readString();
        isUserCreated = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeInt(readyInMinutes);
        dest.writeInt(servings);
        dest.writeTypedList(extendedIngredients);
        dest.writeString(instructions);
        dest.writeString(summary);
        dest.writeByte((byte) (isUserCreated ? 1 : 0));
    }

    public List<ExtendedIngredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public static class ExtendedIngredient implements Parcelable {
        private final String original;

        public ExtendedIngredient(String original) {
            this.original = original;
        }

        protected ExtendedIngredient(Parcel in) {
            original = in.readString();
        }

        public static final Creator<ExtendedIngredient> CREATOR = new Creator<ExtendedIngredient>() {
            @Override
            public ExtendedIngredient createFromParcel(Parcel in) {
                return new ExtendedIngredient(in);
            }

            @Override
            public ExtendedIngredient[] newArray(int size) {
                return new ExtendedIngredient[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(original);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getOriginal() {
            return original;
        }
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
    public int getReadyInMinutes() { return readyInMinutes; }
    public int getServings() { return servings; }
    public String getInstructions() { return instructions; }
    public String getSummary() { return summary; }

    public boolean isUserCreated() { return isUserCreated; }
}