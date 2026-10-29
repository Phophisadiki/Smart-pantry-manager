package com.example.smartpantry;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// SCREEN 2: one form used for BOTH adding and editing an ingredient.
public class AddEditIngredientActivity extends AppCompatActivity {

    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs"};

    private DatabaseHelper db;
    private EditText etName, etQty, etExpiry;
    private Spinner spUnit;
    private long itemId = -1;      // -1 means "adding new", anything else means "editing"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // back arrow

        db = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQty);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);

        spUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, UNITS));

        // Did PantryActivity send us an item_id? Then we are editing.
        itemId = getIntent().getLongExtra("item_id", -1);

        if (itemId == -1) {
            setTitle("Add Ingredient");
        } else {
            setTitle("Edit Ingredient");
            PantryItem item = db.getItem(itemId);
            etName.setText(item.name);
            etQty.setText(String.valueOf(item.quantity));
            etExpiry.setText(item.expiry);
            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equals(item.unit)) {
                    spUnit.setSelection(i);
                }
            }
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveItem());
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyText = etQty.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String unit = spUnit.getSelectedItem().toString();

        // ---------- INPUT VALIDATION ----------
        if (name.isEmpty()) {
            etName.setError("Please enter a name");
            return;
        }
        if (!name.matches("[A-Za-z ]+")) {
            etName.setError("Use letters only");
            return;
        }
        if (qtyText.isEmpty()) {
            etQty.setError("Please enter a quantity");
            return;
        }
        double quantity = Double.parseDouble(qtyText);
        if (quantity <= 0) {
            etQty.setError("Quantity must be more than 0");
            return;
        }
        // Expiry is optional, but if typed it must look like 2026-12-31
        if (!expiry.isEmpty() && !expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
            etExpiry.setError("Use the format yyyy-MM-dd");
            return;
        }

        // ---------- SAVE ----------
        if (itemId == -1) {
            db.addItem(new PantryItem(0, name, quantity, unit, expiry));        // CREATE
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.updateItem(new PantryItem(itemId, name, quantity, unit, expiry)); // UPDATE
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();   // close this screen and go back to the list
    }

    // Back arrow in the toolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
