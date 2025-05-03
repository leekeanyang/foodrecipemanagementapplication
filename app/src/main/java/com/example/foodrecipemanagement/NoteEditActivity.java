package com.example.foodrecipemanagement;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodrecipemanagement.databinding.ActivityNoteEditBinding;

public class NoteEditActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "com.example.foodrecipemanagement.NOTE_ID";
    private static final long NEW_NOTE_ID = -1L; // Indicator for a new note

    private ActivityNoteEditBinding binding;
    private NoteRepository noteRepository;
    private Note currentNote; // Holds the note being edited, or null if new
    private long currentNoteId = NEW_NOTE_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteRepository = NoteRepository.getInstance(this);

        // Get note ID from intent
        currentNoteId = getIntent().getLongExtra(EXTRA_NOTE_ID, NEW_NOTE_ID);

        setupToolbar();
        setupSaveButton();

        if (currentNoteId != NEW_NOTE_ID) {
            Log.d("NoteEditActivity", "Editing existing note with ID: " + currentNoteId);
            loadNoteData();
        } else {
            Log.d("NoteEditActivity", "Creating a new note.");
            // Set title for new note (optional)
            binding.toolbarNoteEdit.setTitle(R.string.new_note_title); // Define: "New Note"
            currentNote = null; // Explicitly null for new note
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarNoteEdit);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Title will be set dynamically or in loadNoteData
        }
    }

    private void setupSaveButton() {
        binding.buttonSaveNote.setOnClickListener(v -> saveNote());
    }

    private void loadNoteData() {
        currentNote = noteRepository.getNoteById(currentNoteId);
        if (currentNote != null) {
            binding.editTextNoteTitle.setText(currentNote.getTitle());
            binding.editTextNoteContent.setText(currentNote.getContent());
            // Set Toolbar title to the note's title for existing notes
            if (getSupportActionBar() != null && !TextUtils.isEmpty(currentNote.getTitle())) {
                getSupportActionBar().setTitle(currentNote.getTitle());
            } else if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.edit_note_title); // Define: "Edit Note"
            }

        } else {
            Log.e("NoteEditActivity", "Failed to load note data for ID: " + currentNoteId);
            Toast.makeText(this, R.string.error_loading_note, Toast.LENGTH_SHORT).show(); // Define: "Error loading note"
            finish(); // Close activity if note can't be loaded
        }
    }

    private void saveNote() {
        String title = binding.editTextNoteTitle.getText().toString().trim();
        String content = binding.editTextNoteContent.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, R.string.note_empty_warning, Toast.LENGTH_SHORT).show(); // Define: "Note is empty, cannot save."
            return; // Don't save empty notes
        }

        // If title is empty but content isn't, use a default title
        if (title.isEmpty()) {
            title = getString(R.string.untitled_note); // Define: "Untitled Note"
        }

        if (currentNote == null) {
            // Creating a new note
            Log.d("NoteEditActivity", "Saving new note.");
            currentNote = new Note(title, content);
        } else {
            // Updating existing note
            Log.d("NoteEditActivity", "Updating existing note ID: " + currentNote.getId());
            currentNote.setTitle(title);
            currentNote.setContent(content);
            // lastModified will be updated in repository's saveNote
        }

        noteRepository.saveNote(currentNote);
        Toast.makeText(this, R.string.note_saved_success, Toast.LENGTH_SHORT).show(); // Define: "Note saved"

        // Set result OK so the list activity knows to refresh (though launcher handles it)
        setResult(RESULT_OK);
        finish(); // Close the edit screen after saving
    }

    // Handle Toolbar Back Button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Optional: Check for unsaved changes before finishing
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Clean up view binding
    }
}