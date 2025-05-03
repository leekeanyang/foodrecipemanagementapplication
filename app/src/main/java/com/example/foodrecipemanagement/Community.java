package com.example.foodrecipemanagement;

public class Community {
    private int id;
    private String name;
    private String description;
    private boolean isPublic;
    private int recipeCount; // Add recipe count
    private int questionCount; // Add question count

    public Community(int id, String name, String description, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.recipeCount = 0;
        this.questionCount = 0;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
    public int getRecipeCount() { return recipeCount; }
    public void setRecipeCount(int recipeCount) { this.recipeCount = recipeCount; }
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Community community = (Community) o;
        return id == community.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}