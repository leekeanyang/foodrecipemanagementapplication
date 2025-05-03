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

public class LoginFragment extends Fragment {

    private CheckBox cbTerms;
    private TextInputEditText etEmail, etPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        emailLayout = view.findViewById(R.id.email_layout);
        passwordLayout = view.findViewById(R.id.password_layout);
        btnLogin = view.findViewById(R.id.btn_login);
        cbTerms = view.findViewById(R.id.cb_terms);

        TextView tvTerms = view.findViewById(R.id.tv_terms_link);

        btnLogin.setOnClickListener(v -> validateAndLogin());

        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnLogin.setEnabled(isChecked);
            btnLogin.setAlpha(isChecked ? 1f : 0.5f);
        });

        tvTerms.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), TermsActivity.class));
        });

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
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

    private void validateAndLogin() {
        boolean emailValid = validateEmail();
        boolean passwordValid = validatePassword();

        if (!cbTerms.isChecked()) {
            Toast.makeText(requireContext(), "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        if (emailValid && passwordValid) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String hashedPassword = SecurityUtils.hashPassword(password);

            AppDatabase db = AppDatabase.getInstance(requireContext());
            UserDao userDao = db.userDao();

            Executors.newSingleThreadExecutor().execute(() -> {
                User user = userDao.getUserByEmail(email);

                requireActivity().runOnUiThread(() -> {
                    if (user != null && user.password.equals(hashedPassword)) {
                        SharedPreferences userPrefs = requireContext().getSharedPreferences(
                                "user_prefs", Context.MODE_PRIVATE
                        );
                        userPrefs.edit()
                                .putInt("current_user_id", user.id)
                                .apply();

                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(requireContext(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        emailLayout.setError("Invalid credentials");
                        passwordLayout.setError("Invalid credentials");
                    }
                });
            });
        }
    }
}