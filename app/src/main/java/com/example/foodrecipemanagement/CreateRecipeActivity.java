package com.example.foodrecipemanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CreateRecipeActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText titleEditText, summaryEditText, servingSizeEditText;
    private TextInputEditText ingredientEditText, instructionEditText;
    private ImageView coverImage;
    private TextView ingredientsList, instructionsList;

    private TextInputLayout titleInputLayout, servingSizeInputLayout;
    private TextInputLayout ingredientsInputLayout, instructionsInputLayout;
    private Button btnDecrementTime, btnIncrementTime;
    private TextInputEditText cookingTimeEditText;
    private TextInputLayout textInputLayoutCookingTime;

    private List<Recipe.ExtendedIngredient> ingredients = new ArrayList<>();
    private List<String> instructionSteps = new ArrayList<>();

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        initializeViews();

        ingredientsInputLayout.setEndIconOnClickListener(v -> addIngredient());
        instructionsInputLayout.setEndIconOnClickListener(v -> addInstruction());

        setupListeners();
        setupTextWatchers();
    }

    private void initializeViews() {

        titleEditText = findViewById(R.id.titleEditText);
        summaryEditText = findViewById(R.id.descriptionEditText);
        servingSizeEditText = findViewById(R.id.servingSizeEditText);
        ingredientEditText = findViewById(R.id.ingredientEditText);
        instructionEditText = findViewById(R.id.instructionEditText);

        ingredientsList = findViewById(R.id.ingredientsList);
        instructionsList = findViewById(R.id.instructionsList);

        coverImage = findViewById(R.id.coverImage);

        titleInputLayout = findViewById(R.id.textInputLayoutTitle);
        servingSizeInputLayout = findViewById(R.id.textInputLayoutServingSize);
        ingredientsInputLayout = findViewById(R.id.textInputLayoutIngredients);
        instructionsInputLayout = findViewById(R.id.textInputLayoutInstructions);
        btnDecrementTime = findViewById(R.id.btnDecrementTime);
        btnIncrementTime = findViewById(R.id.btnIncrementTime);
        cookingTimeEditText = findViewById(R.id.cookingTimeEditText);
        textInputLayoutCookingTime = findViewById(R.id.textInputLayoutCookingTime);
    }

    private void setupListeners() {

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.saveButton).setOnClickListener(v -> saveRecipe());

        coverImage.setOnClickListener(v -> openImagePicker());

        findViewById(R.id.btnIncrement).setOnClickListener(v -> adjustServingSize(1));
        findViewById(R.id.btnDecrement).setOnClickListener(v -> adjustServingSize(-1));

        btnDecrementTime.setOnClickListener(v -> adjustCookingTime(-5));
        btnIncrementTime.setOnClickListener(v -> adjustCookingTime(5));
    }

    private void setupTextWatchers() {

        titleEditText.addTextChangedListener(new ClearErrorTextWatcher(() ->
                titleInputLayout.setError(null)));

        servingSizeEditText.addTextChangedListener(new ClearErrorTextWatcher(() ->
                servingSizeInputLayout.setError(null)));

        ingredientEditText.addTextChangedListener(new ClearErrorTextWatcher(() ->
                ingredientsInputLayout.setError(null)));

        instructionEditText.addTextChangedListener(new ClearErrorTextWatcher(() ->
                instructionsInputLayout.setError(null)));

        cookingTimeEditText.addTextChangedListener(new ClearErrorTextWatcher(() ->
                textInputLayoutCookingTime.setError(null)));
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            coverImage.setImageURI(selectedImageUri);
        }
    }

    private void adjustServingSize(int delta) {
        try {
            int current = Integer.parseInt(servingSizeEditText.getText().toString());
            current = Math.max(1, Math.min(100, current + delta));
            servingSizeEditText.setText(String.valueOf(current));
            servingSizeInputLayout.setError(null);
        } catch (NumberFormatException e) {
            servingSizeEditText.setText("1");
        }
    }

    private void adjustCookingTime(int delta) {
        try {
            int current = Integer.parseInt(cookingTimeEditText.getText().toString());
            current = Math.max(0, current + delta);
            cookingTimeEditText.setText(String.valueOf(current));
            textInputLayoutCookingTime.setError(null);
        } catch (NumberFormatException e) {
            cookingTimeEditText.setText("0");
        }
    }

    private void saveRecipe() {
        clearAllErrors();

        String title = titleEditText.getText().toString().trim();
        String summary = summaryEditText.getText().toString().trim();
        String servingsText = servingSizeEditText.getText().toString().trim();
        boolean isValid = true;
        String imageUri = selectedImageUri != null ? selectedImageUri.toString() : "";
        String cookingTimeText = cookingTimeEditText.getText().toString().trim();
        int cookingTime = 0;
        boolean hasCookingTimeError = false;
        int userId = getCurrentUserId();

        if (userId == -1) {
            Toast.makeText(this, "Please log in to create recipes", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            titleInputLayout.setError("Recipe title is required");
            isValid = false;
        }

        if (ingredients.isEmpty()) {
            ingredientsInputLayout.setError("Add at least one ingredient");
            isValid = false;
        }

        if (instructionSteps.isEmpty()) {
            instructionsInputLayout.setError("Add at least one instruction step");
            isValid = false;
        }

        if (cookingTimeText.isEmpty()) {
            textInputLayoutCookingTime.setError("Cooking time is required");
            isValid = false;
            hasCookingTimeError = true;
        } else {
            try {
                cookingTime = Integer.parseInt(cookingTimeText);
                if (cookingTime <= 0) {
                    textInputLayoutCookingTime.setError("Must be at least 1 minute");
                    isValid = false;
                    hasCookingTimeError = true;
                }
            } catch (NumberFormatException e) {
                textInputLayoutCookingTime.setError("Invalid time format");
                isValid = false;
                hasCookingTimeError = true;
            }
        }

        if (!hasCookingTimeError) {
            textInputLayoutCookingTime.setError(null);
        }

        int servings = 0;
        try {
            servings = Integer.parseInt(servingsText);
            if (servings < 1 || servings > 100) {
                servingSizeInputLayout.setError("Must be between 1-100");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            servingSizeInputLayout.setError("Invalid serving size");
            isValid = false;
        }

        if (!isValid) return;

        StringBuilder instructionsBuilder = new StringBuilder();
        for (int i = 0; i < instructionSteps.size(); i++) {
            instructionsBuilder.append(i + 1)
                    .append(". ")
                    .append(instructionSteps.get(i))
                    .append("\n");
        }

        int id = (int) System.currentTimeMillis();
        if (id < 0) id *= -1;
        Recipe recipe = new Recipe(
                id,
                title,
                imageUri.toString(),
                cookingTime,
                servings,
                ingredients,
                instructionsBuilder.toString(),
                summary,
                true
        );

        UserRecipeRepository.getInstance(this).saveRecipe(recipe);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("NEW_RECIPE", true);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private int getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getInt("current_user_id", -1);
    }

    private void clearAllErrors() {
        titleInputLayout.setError(null);
        servingSizeInputLayout.setError(null);
        ingredientsInputLayout.setError(null);
        instructionsInputLayout.setError(null);
        textInputLayoutCookingTime.setError(null);
    }

    private void addIngredient() {
        String ingredient = ingredientEditText.getText().toString().trim();
        if (!ingredient.isEmpty()) {
            ingredients.add(new Recipe.ExtendedIngredient(ingredient));
            updateIngredientsDisplay();
            ingredientEditText.setText("");
            ingredientsInputLayout.setError(null);
        }
    }

    private void addInstruction() {
        String instruction = instructionEditText.getText().toString().trim();
        if (!instruction.isEmpty()) {
            instructionSteps.add(instruction);
            updateInstructionsDisplay();
            instructionEditText.setText("");
            instructionsInputLayout.setError(null);
        }
    }

    private void updateIngredientsDisplay() {
        StringBuilder sb = new StringBuilder();
        for (Recipe.ExtendedIngredient ingredient : ingredients) {
            sb.append("• ").append(ingredient.getOriginal()).append("\n");
        }
        ingredientsList.setText(sb.toString());
    }

    private void updateInstructionsDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < instructionSteps.size(); i++) {
            sb.append(i + 1).append(". ").append(instructionSteps.get(i)).append("\n");
        }
        instructionsList.setText(sb.toString());
    }

    private static class ClearErrorTextWatcher implements TextWatcher {
        private final Runnable clearAction;

        ClearErrorTextWatcher(Runnable clearAction) {
            this.clearAction = clearAction;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            clearAction.run();
        }
    }
}