package com.example.foodrecipemanagement;

import android.app.Dialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.foodrecipemanagement.database.CartItem;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class AddItemDialogFragment extends DialogFragment {

    public interface OnItemAddedListener {
        void onItemAdded(CartItem item);
    }

    private OnItemAddedListener listener;
    public void setOnItemAddedListener(OnItemAddedListener l) {
        listener = l;
    }

    // --- Views ---
    private EditText nameInput;
    private EditText amountInput;
    private Spinner metricSpinner;
    private EditText notesInput;
    private Spinner categorySpinner;
    private Button btnDatePicker;
    private Button btnAttachImage;
    private TextView imageFilename;
    private Button buttonCancel;
    private Button buttonSave;

    private Uri imageUri;
    private long selectedTimestamp = System.currentTimeMillis();

    private List<String> categories = Collections.emptyList();
    public void setCategories(List<String> cats) {
        categories = cats;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 1) Create a bare Dialog with no title bar
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_item_custom);

        // 2) Find your views
        nameInput       = dialog.findViewById(R.id.edit_item_name);
        amountInput     = dialog.findViewById(R.id.edit_amount);
        metricSpinner   = dialog.findViewById(R.id.spinner_metric);
        notesInput      = dialog.findViewById(R.id.edit_notes);
        categorySpinner = dialog.findViewById(R.id.spinner_category);
        btnDatePicker   = dialog.findViewById(R.id.btn_date_picker);
        btnAttachImage  = dialog.findViewById(R.id.btn_attach_image);
        imageFilename   = dialog.findViewById(R.id.text_image_filename);
        buttonCancel    = dialog.findViewById(R.id.button_cancel);
        buttonSave      = dialog.findViewById(R.id.button_save);

        // 3) Populate category spinner
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(catAdapter);

        // 4) Date picker
        btnDatePicker.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(
                    requireContext(),
                    (dp, y, m, d) -> {
                        c.set(y, m, d);
                        selectedTimestamp = c.getTimeInMillis();
                        btnDatePicker.setText(
                                String.format("%04d-%02d-%02d", y, m + 1, d)
                        );
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // 5) Image attach (you fill in your own picker logic)
        btnAttachImage.setOnClickListener(v -> {
            // TODO: launch gallery / camera → set imageUri + imageFilename.setText(...)
        });

        // 6) Cancel / Save
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        buttonSave.setOnClickListener(v -> {
            String name     = nameInput.getText().toString().trim();
            String amount   = amountInput.getText().toString().trim();
            String metric   = metricSpinner.getSelectedItem().toString();
            String notes    = notesInput.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            String path     = imageUri != null ? imageUri.toString() : null;

            if (!name.isEmpty() && listener != null) {
                CartItem item = new CartItem(
                        name, category,
                        amount, metric,
                        selectedTimestamp,
                        notes, path
                );
                listener.onItemAdded(item);
            }
            dialog.dismiss();
        });

        // 7) Optional: make the Window background transparent so only your XML’s card/frame shows
       //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
