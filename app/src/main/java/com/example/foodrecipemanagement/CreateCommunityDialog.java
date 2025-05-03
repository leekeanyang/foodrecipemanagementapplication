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

public class CreateCommunityDialog extends DialogFragment {
    private OnCommunityCreatedListener listener;
    private boolean isSubmitted = false;

    public interface OnCommunityCreatedListener {
        void onCommunityCreated(Community community);
    }

    public void setOnCommunityCreatedListener(OnCommunityCreatedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_create_community, null);

        EditText editTextName = view.findViewById(R.id.editText_community_name);
        EditText editTextDescription = view.findViewById(R.id.editText_community_description);
        CheckBox checkBoxIsPublic = view.findViewById(R.id.checkBox_is_public);
        Button buttonCreate = view.findViewById(R.id.button_create);
        Button buttonCancel = view.findViewById(R.id.button_cancel);

        buttonCreate.setOnClickListener(v -> {
            if (isSubmitted) {
                return;
            }
            isSubmitted = true;

            String name = editTextName.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            boolean isPublic = checkBoxIsPublic.isChecked();

            if (name.isEmpty()) {
                editTextName.setError("Name is required");
                isSubmitted = false;
                return;
            }

            Community community = new Community(0, name, description, isPublic);
            listener.onCommunityCreated(community);
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}