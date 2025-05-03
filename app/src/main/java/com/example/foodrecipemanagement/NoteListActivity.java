package com.example.foodrecipemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodrecipemanagement.databinding.ActivityNoteListBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class NoteListActivity extends AppCompatActivity implements NoteListAdapter.OnNoteInteractionListener {

    private ActivityNoteListBinding binding;
    private NoteRepository noteRepository;
    private NoteListAdapter noteListAdapter;

    private final ActivityResultLauncher<Intent> noteEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("NoteListActivity", "Returned from NoteEditActivity, refreshing list.");
                loadNotes();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteRepository = NoteRepository.getInstance(this);

        setupToolbar();
        setupRecyclerView();
        setupFab();

        loadNotes();
    }

    private void loadNotes() {
        List<Note> notes = noteRepository.getAllNotes();
        Log.d("NoteListActivity", "Loading " + notes.size() + " notes into adapter.");
        noteListAdapter.submitList(notes);
        updateEmptyState(notes.isEmpty());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarNoteList);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.notes_title);
        }
    }

    private void setupRecyclerView() {
        noteListAdapter = new NoteListAdapter(this, this);
        binding.recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewNotes.setAdapter(noteListAdapter);
    }

    private void setupFab() {
        binding.fabAddNote.setOnClickListener(v -> {
            Log.d("NoteListActivity", "FAB clicked - launching NoteEditActivity for new note.");
            Intent intent = new Intent(NoteListActivity.this, NoteEditActivity.class);
            intent.putExtra(NoteEditActivity.EXTRA_NOTE_ID, -1L);
            noteEditLauncher.launch(intent);
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        binding.recyclerViewNotes.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.textViewEmptyNotes.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onNoteClick(Note note) {
        Log.d("NoteListActivity", "Note clicked - launching NoteEditActivity for ID: " + note.getId());
        Intent intent = new Intent(NoteListActivity.this, NoteEditActivity.class);
        intent.putExtra(NoteEditActivity.EXTRA_NOTE_ID, note.getId());
        noteEditLauncher.launch(intent);
    }

    @Override
    public void onNoteDelete(Note note) {
        Log.d("NoteListActivity", "Delete requested for Note ID: " + note.getId());
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_note_confirm_title)
                .setMessage(getString(R.string.delete_note_confirm_message, note.getTitle()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    noteRepository.deleteNote(note);
                    Toast.makeText(this, R.string.note_deleted_success, Toast.LENGTH_SHORT).show();
                    loadNotes();
                })
                .show();
    }

    @Override
    public void onNoteRename(Note note) {
        Log.d("NoteListActivity", "Rename requested for Note ID: " + note.getId());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.rename_note_title);

        // *** Modification: Use FrameLayout to wrap EditText to set Padding ***
        FrameLayout container = new FrameLayout(this);
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setText(note.getTitle());
        input.setSelection(input.getText().length());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // Set EditText's margin (instead of padding)
        int margin = (int) (16 * getResources().getDisplayMetrics().density); // 16dp
        params.setMargins(margin, margin/2, margin, margin/2);
        input.setLayoutParams(params);
        container.addView(input); // Add EditText to FrameLayout

        builder.setView(container);

        // Make sure R.string.rename exists and import DialogInterface
        builder.setPositiveButton(R.string.rename, (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty() && !newTitle.equals(note.getTitle())) {
                boolean success = noteRepository.updateNoteTitle(note.getId(), newTitle);
                if (success) {
                    Toast.makeText(this, R.string.note_renamed_success, Toast.LENGTH_SHORT).show();
                    loadNotes();
                } else {
                    Toast.makeText(this, R.string.note_rename_failed, Toast.LENGTH_SHORT).show();
                }
            } else if (newTitle.isEmpty()) {
                Toast.makeText(this, R.string.note_title_empty, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}