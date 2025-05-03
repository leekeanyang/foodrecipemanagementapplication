package com.example.foodrecipemanagement;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditReplyDialog extends DialogFragment {
    private Reply reply;
    private OnReplyEditedListener listener;

    public interface OnReplyEditedListener {
        void onReplyEdited(Reply reply);
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public void setOnReplyEditedListener(OnReplyEditedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_reply, null);

        EditText editTextUserName = view.findViewById(R.id.editText_user_name);
        EditText editTextReply = view.findViewById(R.id.editText_reply);
        Button buttonSave = view.findViewById(R.id.button_save);
        Button buttonCancel = view.findViewById(R.id.button_cancel);

        // Pre-fill with existing reply data
        editTextUserName.setText(reply.getUserName());
        editTextReply.setText(reply.getContent());

        buttonSave.setOnClickListener(v -> {
            String userName = editTextUserName.getText().toString().trim();
            String content = editTextReply.getText().toString().trim();

            if (userName.isEmpty()) {
                editTextUserName.setError("Name is required");
                return;
            }
            if (content.isEmpty()) {
                editTextReply.setError("Reply is required");
                return;
            }

            reply.setUserName(userName);
            reply.setContent(content);
            listener.onReplyEdited(reply);
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}