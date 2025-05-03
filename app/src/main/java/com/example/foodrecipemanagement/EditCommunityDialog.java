package com.example.foodrecipemanagement;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditCommunityDialog extends DialogFragment {
    private Community community;
    private OnCommunityEditedListener listener;

    public interface OnCommunityEditedListener {
        void onCommunityEdited(Community community);
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public void setOnCommunityEditedListener(OnCommunityEditedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_community, null);

        EditText editTextName = view.findViewById(R.id.editText_community_name);
        EditText editTextDescription = view.findViewById(R.id.editText_community_description);
        CheckBox checkBoxIsPublic = view.findViewById(R.id.checkBox_is_public);
        Button buttonSave = view.findViewById(R.id.button_save);
        Button buttonCancel = view.findViewById(R.id.button_cancel);

        // Pre-fill with existing community data
        editTextName.setText(community.getName());
        editTextDescription.setText(community.getDescription());
        checkBoxIsPublic.setChecked(community.isPublic());

        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            boolean isPublic = checkBoxIsPublic.isChecked();

            if (name.isEmpty()) {
                editTextName.setError("Name is required");
                return;
            }

            community.setName(name);
            community.setDescription(description);
            community.setPublic(isPublic);
            listener.onCommunityEdited(community);
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}