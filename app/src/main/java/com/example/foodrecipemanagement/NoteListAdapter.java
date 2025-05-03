package com.example.foodrecipemanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;

// Using ListAdapter for better performance with DiffUtil
public class NoteListAdapter extends ListAdapter<Note, NoteListAdapter.ViewHolder> {

    private final OnNoteInteractionListener listener;
    private final Context context; // Needed for date formatting

    // Interface for handling interactions
    public interface OnNoteInteractionListener {
        void onNoteClick(Note note);
        void onNoteDelete(Note note);
        void onNoteRename(Note note);
    }

    public NoteListAdapter(Context context, @NonNull OnNoteInteractionListener listener) {
        // DiffUtil helps efficiently update the list
        super(new DiffUtil.ItemCallback<Note>() {
            @Override
            public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                // Check relevant fields if content comparison is needed
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.getContent().equals(newItem.getContent()) && // Compare content for preview changes
                        oldItem.getLastModified() == newItem.getLastModified();
            }
        });
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = getItem(position);
        holder.bind(note, listener, context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView previewTextView;
        ImageButton renameButton;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textView_note_title);
            previewTextView = itemView.findViewById(R.id.textView_note_preview);
            renameButton = itemView.findViewById(R.id.button_edit_note_title);
            deleteButton = itemView.findViewById(R.id.button_delete_note);
        }

        public void bind(final Note note, final OnNoteInteractionListener listener, Context context) {
            titleTextView.setText(note.getTitle());
            // Show first line or two of content as preview
            previewTextView.setText(note.getContent());

            // Optional: Format last modified date for display
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            previewTextView.append("\nModified: " + dateFormat.format(new Date(note.getLastModified())));


            // --- Click Listeners ---
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoteClick(note);
                }
            });

            renameButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoteRename(note);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoteDelete(note);
                }
            });
        }
    }
}