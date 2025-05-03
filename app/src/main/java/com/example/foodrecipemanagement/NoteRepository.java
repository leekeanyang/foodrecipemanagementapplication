package com.example.foodrecipemanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoteRepository {
    private static final String PREFS_NAME = "note_prefs";
    private static final String KEY_NOTE_IDS = "note_ids"; // Key for the Set of IDs
    private static final String NOTE_PREFIX = "note_"; // Prefix for individual note keys
    private static NoteRepository instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public static synchronized NoteRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NoteRepository(context.getApplicationContext());
        }
        return instance;
    }

    private NoteRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Generates the SharedPreferences key for a specific note
    private String getNoteKey(long noteId) {
        return NOTE_PREFIX + noteId;
    }

    // Retrieves the Set of stored Note IDs
    private Set<String> getNoteIds() {
        // Using HashSet constructor ensures mutability
        return new HashSet<>(prefs.getStringSet(KEY_NOTE_IDS, new HashSet<>()));
    }

    /**
     * Saves a note (creates if new, updates if ID exists).
     * @param note The Note object to save.
     */
    public void saveNote(Note note) {
        // Ensure last modified time is updated on save
        note.setLastModified(System.currentTimeMillis());

        String noteJson = gson.toJson(note);
        String noteKey = getNoteKey(note.getId());
        Log.d("NoteRepository", "Saving Note ID: " + note.getId());

        // Get current IDs, add the new/updated one
        Set<String> currentIds = getNoteIds();
        currentIds.add(String.valueOf(note.getId()));

        // Save both the ID set and the note JSON
        prefs.edit()
                .putStringSet(KEY_NOTE_IDS, currentIds) // Save the updated set of IDs
                .putString(noteKey, noteJson)        // Save/Overwrite the note data
                .apply();
    }

    /**
     * Retrieves a specific note by its ID.
     * @param noteId The ID of the note to retrieve.
     * @return The Note object, or null if not found.
     */
    public Note getNoteById(long noteId) {
        String noteKey = getNoteKey(noteId);
        String noteJson = prefs.getString(noteKey, null);

        if (noteJson != null) {
            try {
                return gson.fromJson(noteJson, Note.class);
            } catch (Exception e) {
                Log.e("NoteRepository", "Error deserializing note ID: " + noteId, e);
                return null;
            }
        }
        return null;
    }

    /**
     * Retrieves all saved notes, sorted by last modified descending (newest first).
     * @return A list of all Note objects.
     */
    public List<Note> getAllNotes() {
        Set<String> noteIds = getNoteIds();
        List<Note> notes = new ArrayList<>();

        Log.d("NoteRepository", "Loading notes for IDs: " + noteIds);

        for (String idStr : noteIds) {
            try {
                long id = Long.parseLong(idStr);
                Note note = getNoteById(id);
                if (note != null) {
                    notes.add(note);
                } else {
                    Log.w("NoteRepository", "Note data missing for ID: " + idStr + ". Removing stale ID.");
                    // Optional: Clean up stale ID if data is missing
                    // removeNoteId(idStr); // Be careful with concurrent modification if looping directly
                }
            } catch (NumberFormatException e) {
                Log.e("NoteRepository", "Invalid note ID format found: " + idStr);
                // Optional: Clean up invalid ID
            }
        }

        // Sort notes by last modified date, newest first
        Collections.sort(notes, (n1, n2) -> Long.compare(n2.getLastModified(), n1.getLastModified()));

        Log.d("NoteRepository", "Loaded " + notes.size() + " notes.");
        return notes;
    }

    /**
     * Deletes a specific note.
     * @param note The Note object to delete.
     */
    public void deleteNote(Note note) {
        if (note == null) return;
        deleteNoteById(note.getId());
    }

    /**
     * Deletes a specific note by its ID.
     * @param noteId The ID of the note to delete.
     */
    public void deleteNoteById(long noteId) {
        Set<String> currentIds = getNoteIds();
        String idStr = String.valueOf(noteId);
        String noteKey = getNoteKey(noteId);

        if (currentIds.contains(idStr)) {
            currentIds.remove(idStr);
            prefs.edit()
                    .remove(noteKey) // Remove the note data
                    .putStringSet(KEY_NOTE_IDS, currentIds) // Save the updated ID set
                    .apply();
            Log.d("NoteRepository", "Deleted Note ID: " + noteId);
        } else {
            Log.w("NoteRepository", "Attempted to delete non-existent Note ID: " + noteId);
            // Also remove note data just in case ID set was corrupted
            prefs.edit().remove(noteKey).apply();
        }
    }

    /**
     * Updates only the title of an existing note.
     * @param noteId The ID of the note to update.
     * @param newTitle The new title for the note.
     * @return true if update was successful, false otherwise.
     */
    public boolean updateNoteTitle(long noteId, String newTitle) {
        Note note = getNoteById(noteId);
        if (note != null) {
            note.setTitle(newTitle);
            // Note: We call saveNote which also updates lastModified time
            saveNote(note);
            return true;
        }
        Log.e("NoteRepository", "Failed to update title for non-existent Note ID: " + noteId);
        return false;
    }
}