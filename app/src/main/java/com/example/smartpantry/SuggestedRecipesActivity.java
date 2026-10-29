package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// SCREEN 3: shows ONLY the recipes the user can make with what is in the pantry.
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private DatabaseHelper db;
    private RecyclerView recycler;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setTitle("Suggested Recipes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        recycler = findViewById(R.id.recyclerRecipes);
        tvEmpty = findViewById(R.id.tvEmptyRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    // Matching runs in onResume so it always uses the latest pantry.
    @Override
    protected void onResume() {
        super.onResume();

        List<PantryItem> pantry = db.getAllItems();
        List<Recipe> suggestions = new ArrayList<>();

        // Keep a recipe ONLY if the strict-matching rule says yes
        for (Recipe recipe : db.getAllRecipes()) {
            if (IngredientMatcher.canMake(recipe, pantry)) {
                suggestions.add(recipe);
            }
        }

        recycler.setAdapter(new RecipeAdapter(suggestions, this));

        // Feedback when zero recipes match
        if (suggestions.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    // User tapped a recipe -> open the detail screen and pass the recipe id
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.id);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
