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

public class EditQuestionDialog extends DialogFragment {
    private Question question;
    private OnQuestionEditedListener listener;

    public interface OnQuestionEditedListener {
        void onQuestionEdited(Question question);
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setOnQuestionEditedListener(OnQuestionEditedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_question, null);

        EditText editTextUserName = view.findViewById(R.id.editText_user_name);
        EditText editTextQuestion = view.findViewById(R.id.editText_question);
        Button buttonSave = view.findViewById(R.id.button_save);
        Button buttonCancel = view.findViewById(R.id.button_cancel);

        editTextUserName.setText(question.getUserName());
        editTextQuestion.setText(question.getContent());

        buttonSave.setOnClickListener(v -> {
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

            question.setUserName(userName);
            question.setContent(content);
            listener.onQuestionEdited(question);
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}