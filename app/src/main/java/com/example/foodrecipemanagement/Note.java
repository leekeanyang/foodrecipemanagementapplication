package com.example.foodrecipemanagement;

import java.io.Serializable;
import java.util.Objects;

public class Note implements Serializable { // Serializable for passing via Intent
    private long id; // Unique ID for the note (using timestamp)
    private String title;
    private String content;
    private long lastModified; // Timestamp for sorting or display

    // Constructor for new notes (ID generated automatically)
    public Note(String title, String content) {
        this.id = System.currentTimeMillis(); // Use current time as a simple unique ID
        this.title = title;
        this.content = content;
        this.lastModified = this.id;
    }

    // Constructor for Gson deserialization & updates (requires ID)
    public Note(long id, String title, String content, long lastModified) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.lastModified = lastModified;
    }

    // Default constructor needed for Gson
    public Note() {}

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getLastModified() {
        return lastModified;
    }

    // Setters
    public void setId(long id) { // Setter for ID might be needed for updates
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    // equals() and hashCode() based on ID for easy identification
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}