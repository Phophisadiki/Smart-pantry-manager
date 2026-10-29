package com.example.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

// SCREEN 5: one setting - show or hide expiry dates in the pantry list.
// It is saved with SharedPreferences, so it is remembered after the app closes.
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SwitchCompat switchExpiry = findViewById(R.id.switchExpiry);

        // Show the saved value, then save again whenever the user flips the switch
        switchExpiry.setChecked(prefs.getBoolean("show_expiry", true));
        switchExpiry.setOnCheckedChangeListener((button, isChecked) ->
                prefs.edit().putBoolean("show_expiry", isChecked).apply());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
