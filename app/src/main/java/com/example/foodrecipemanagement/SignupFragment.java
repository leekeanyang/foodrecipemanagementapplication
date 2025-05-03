package com.example.foodrecipemanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executors;

public class SignupFragment extends Fragment {

    private CheckBox cbTerms;
    private TextInputEditText etUsername, etEmail, etPassword;
    private TextInputLayout usernameLayout, emailLayout, passwordLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        etUsername = view.findViewById(R.id.et_username);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        usernameLayout = view.findViewById(R.id.username_layout);
        emailLayout = view.findViewById(R.id.email_layout);
        passwordLayout = view.findViewById(R.id.password_layout);
        Button btnSignup = view.findViewById(R.id.btn_signup);
        cbTerms = view.findViewById(R.id.cb_terms);

        TextView tvTerms = view.findViewById(R.id.tv_terms_link);

        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnSignup.setEnabled(isChecked);
            btnSignup.setAlpha(isChecked ? 1f : 0.5f);
        });

        btnSignup.setOnClickListener(v -> validateAndSignup());

        tvTerms.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), TermsActivity.class));
        });

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }

    private boolean validateUsername() {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            usernameLayout.setError("Username cannot be empty");
            return false;
        } else {
            usernameLayout.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            emailLayout.setError("Email cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid email format");
            return false;
        } else {
            emailLayout.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = etPassword.getText().toString().trim();
        if (password.isEmpty()) {
            passwordLayout.setError("Password cannot be empty");
            return false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            return false;
        } else {
            passwordLayout.setError(null);
            return true;
        }
    }

    private void validateAndSignup() {
        boolean usernameValid = validateUsername();
        boolean emailValid = validateEmail();
        boolean passwordValid = validatePassword();

        if (!cbTerms.isChecked()) {
            Toast.makeText(requireContext(), "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usernameValid && emailValid && passwordValid) {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String hashedPassword = SecurityUtils.hashPassword(password);

            AppDatabase db = AppDatabase.getInstance(requireContext());
            UserDao userDao = db.userDao();

            Executors.newSingleThreadExecutor().execute(() -> {
                if (userDao.emailExists(email)) {
                    requireActivity().runOnUiThread(() ->
                            emailLayout.setError("Email already registered"));
                } else {
                    User newUser = new User(username, email, hashedPassword);
                    userDao.insert(newUser);
                    User createdUser = userDao.getUserByEmail(email);

                    requireActivity().runOnUiThread(() -> {
                        if (createdUser != null) {
                            SharedPreferences userPrefs = requireContext().getSharedPreferences(
                                    "user_prefs", Context.MODE_PRIVATE
                            );
                            userPrefs.edit()
                                    .putInt("current_user_id", createdUser.id)
                                    .apply();

                            Toast.makeText(requireContext(), "Signup successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireContext(), MainActivity.class));
                            requireActivity().finish();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Registration failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}