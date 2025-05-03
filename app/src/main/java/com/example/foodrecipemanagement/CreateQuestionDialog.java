package com.example.foodrecipemanagement;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class CreateQuestionDialog extends DialogFragment {
    private EditText editTextContent;
    private EditText editTextUserName;
    private Button buttonCreate;
    private OnQuestionCreatedListener listener;
    private boolean isCreating = false;

    public interface OnQuestionCreatedListener {
        void onQuestionCreated(Question question);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_question, container, false);

        editTextContent = view.findViewById(R.id.editText_question_content);
        editTextUserName = view.findViewById(R.id.editText_user_name);
        Button buttonCancel = view.findViewById(R.id.button_cancel);
        buttonCreate = view.findViewById(R.id.button_create);

        buttonCancel.setOnClickListener(v -> dismissWithKeyboard());

        buttonCreate.setOnClickListener(v -> {
            if (isCreating) {
                android.util.Log.d("CreateQuestionDialog", "Ignoring duplicate click");
                return;
            }
            isCreating = true;
            buttonCreate.setEnabled(false);
            android.util.Log.d("CreateQuestionDialog", "Create button clicked");
            String content = editTextContent.getText().toString().trim();
            String userName = editTextUserName.getText().toString().trim();

            if (content.isEmpty()) {
                editTextContent.setError("Question cannot be empty");
                isCreating = false;
                buttonCreate.setEnabled(true);
                return;
            }

            if (userName.isEmpty()) {
                userName = "Anonymous";
            }

            Question question = new Question(0, content, userName, 0); // Community ID will be set by the activity
            if (listener != null) {
                listener.onQuestionCreated(question);
            }

            dismissWithKeyboard();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void dismissWithKeyboard() {
        if (editTextContent != null && editTextContent.hasFocus()) {
            editTextContent.clearFocus();
        }
        if (editTextUserName != null && editTextUserName.hasFocus()) {
            editTextUserName.clearFocus();
        }

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

        dismiss();
    }
}