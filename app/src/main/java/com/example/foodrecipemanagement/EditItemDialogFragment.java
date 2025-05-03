package com.example.foodrecipemanagement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodrecipemanagement.database.CartItem;

import java.util.Calendar;

/**
 * Dialog for editing an existing CartItem.
 */
public class EditItemDialogFragment extends DialogFragment {
    private static final String ARG_ITEM_ID = "itemId";

    /** Callback interface **/
    public interface OnItemEditedListener {
        void onItemEdited(CartItem item);
    }

    private OnItemEditedListener editListener;
    public void setOnItemEditedListener(OnItemEditedListener listener) {
        this.editListener = listener;
    }

    public static EditItemDialogFragment newInstance(int itemId) {
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_ID, itemId);

        EditItemDialogFragment f = new EditItemDialogFragment();
        f.setArguments(args);
        return f;
    }

    // UI references
    private EditText nameInput,
            amountInput,
            notesInput;
    private Spinner metricSpinner,
            categorySpinner;
    private Button btnDatePicker,
            btnAttachImage;
    private TextView imageFilename;

    private CartViewModel viewModel;
    private int itemId;
    private Uri imageUri;
    private long selectedTimestamp;

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        itemId = requireArguments().getInt(ARG_ITEM_ID);
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        AlertDialog.Builder b = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_item_custom, null);

        // findViewById → must match dialog_add_item_custom.xml IDs
        nameInput       = view.findViewById(R.id.edit_item_name);
        amountInput     = view.findViewById(R.id.edit_amount);
        metricSpinner   = view.findViewById(R.id.spinner_metric);
        notesInput      = view.findViewById(R.id.edit_notes);
        categorySpinner = view.findViewById(R.id.spinner_category);
        btnDatePicker   = view.findViewById(R.id.btn_date_picker);
        btnAttachImage  = view.findViewById(R.id.btn_attach_image);
        imageFilename   = view.findViewById(R.id.text_image_filename);

        // Load existing item data
        viewModel.getItemById(itemId).observe(this, item -> {
            if (item == null) return;
            nameInput.setText(item.getName());
            amountInput.setText(item.getAmount());
            // you’ll need to set metricSpinner & categorySpinner selection here
            notesInput.setText(item.getNotes());
            selectedTimestamp = item.getTargetDate();
            btnDatePicker.setText(
                    new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .format(item.getTargetDate())
            );
            imageUri = item.getImagePath() != null
                    ? Uri.parse(item.getImagePath())
                    : null;
            imageFilename.setText(
                    imageUri != null
                            ? imageUri.getLastPathSegment()
                            : "No file chosen"
            );
        });

        // Date picker
        btnDatePicker.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(
                    requireContext(),
                    (dp, y, m, d) -> {
                        c.set(y, m, d);
                        selectedTimestamp = c.getTimeInMillis();
                        btnDatePicker.setText(
                                String.format("%04d-%02d-%02d", y, m+1, d)
                        );
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Attach image (stub)
        btnAttachImage.setOnClickListener(v -> {
            // TODO: launch image picker, then on activity result:
            // imageUri = pickedUri;
            // imageFilename.setText(pickedUri.getLastPathSegment());
        });

        b.setView(view)
                .setTitle("Edit Item")
                .setPositiveButton("Save", (dlg, which) -> {
                    // Build updated CartItem
                    CartItem updated = new CartItem(
                            nameInput.getText().toString().trim(),
                            categorySpinner.getSelectedItem().toString(),
                            amountInput.getText().toString().trim(),
                            metricSpinner.getSelectedItem().toString(),
                            selectedTimestamp,
                            notesInput.getText().toString().trim(),
                            imageUri != null ? imageUri.toString() : null
                    );
                    updated.setId(itemId);

                    // Fire callback
                    if (editListener != null) {
                        editListener.onItemEdited(updated);
                    }
                })
                .setNegativeButton("Cancel", (dlg, which) -> dismiss());

        return b.create();
    }
}
