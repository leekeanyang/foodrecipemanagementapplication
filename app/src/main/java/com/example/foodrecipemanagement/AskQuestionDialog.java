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

public class AskQuestionDialog extends DialogFragment {
    private int communityId;
    private OnQuestionAddedListener listener;

    public interface OnQuestionAddedListener {
        void onQuestionAdded(Question question);
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public void setOnQuestionAddedListener(OnQuestionAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_ask_question, null);

        EditText editTextUserName = view.findViewById(R.id.editText_user_name);
        EditText editTextQuestion = view.findViewById(R.id.editText_question);
        Button buttonSubmit = view.findViewById(R.id.button_submit);
        Button buttonCancel = view.findViewById(R.id.button_cancel);

        buttonSubmit.setOnClickListener(v -> {
            String userName = editTextUserName.getText().toString().trim();
            String content = editTextQuestion.getText().toString().trim();

            if (userName.isEmpty()) {
                editTextUserName.setError("Name is required");
                return;
            }
            if (content.isEmpty()) {
                editTextQuestion.setError("Question is required");
                return;
            }

            Question question = new Question(0, content, userName, communityId);
            listener.onQuestionAdded(question);
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}