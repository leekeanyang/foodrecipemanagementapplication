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

public class AddReplyDialog extends DialogFragment {
    private int questionId;
    private OnReplyAddedListener listener;

    public interface OnReplyAddedListener {
        void onReplyAdded(Reply reply);
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setOnReplyAddedListener(OnReplyAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_add_reply, null);

        EditText editTextUserName = view.findViewById(R.id.editText_user_name);
        EditText editTextReply = view.findViewById(R.id.editText_reply);
        Button buttonSubmit = view.findViewById(R.id.button_submit);
        Button buttonCancel = view.findViewById(R.id.button_cancel);

        buttonSubmit.setOnClickListener(v -> {
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

            Reply reply = new Reply(0, content, userName, questionId);
            listener.onReplyAdded(reply);
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}