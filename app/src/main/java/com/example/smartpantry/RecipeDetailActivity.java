package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// SCREEN 4: full ingredient list and method of one recipe.
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Read the id that SuggestedRecipesActivity put in the Intent
        long recipeId = getIntent().getLongExtra("recipe_id", -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(recipeId);
        setTitle(recipe.name);

        // Build the ingredient list as text, one line per ingredient
        String text = "";
        for (RecipeIngredient ing : recipe.ingredients) {
            text = text + "\u2022 " + ing.quantity + " " + ing.unit + " " + ing.name + "\n";
        }

        ((TextView) findViewById(R.id.tvIngredients)).setText(text);
        ((TextView) findViewById(R.id.tvSteps)).setText(recipe.steps);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
