package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// SCREEN 1 (home): shows every pantry item and lets the user add, edit or delete them.
public class PantryActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private RecyclerView recycler;
    private TextView tvEmpty;

    // Lifecycle: onCreate runs once when the screen is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        setTitle("My Pantry");

        db = new DatabaseHelper(this);
        recycler = findViewById(R.id.recyclerPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // "Add ingredient" button -> opens the Add/Edit screen using an Intent
        findViewById(R.id.btnAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));

        // "What can I cook?" button -> opens the Suggested Recipes screen
        findViewById(R.id.btnCook).setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));
    }

    // Lifecycle: onResume runs every time the screen becomes visible again,
    // so the list refreshes after the user adds / edits something.
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<PantryItem> items = db.getAllItems();

        boolean showExpiry = getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("show_expiry", true);

        recycler.setAdapter(new PantryAdapter(items, showExpiry, this));

        // Feedback when the pantry is empty
        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    // User tapped a row -> open the edit screen and pass the item's id in the Intent
    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra("item_id", item.id);
        startActivity(intent);
    }

    // User tapped Delete -> ask first, then delete
    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.name + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteItem(item.id);
                    Toast.makeText(this, item.name + " deleted", Toast.LENGTH_SHORT).show();
                    loadItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ----- Toolbar menu (the app's navigation element) -----
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_recipes) {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
