package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// Everything that talks to the SQLite database lives in this one class.
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "smart_pantry.db", null, 1);
    }

    // Runs ONCE, the first time the app is opened: creates the tables and adds the recipes.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, quantity REAL, unit TEXT, expiry TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, steps TEXT)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER, name TEXT, quantity REAL, unit TEXT)");
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    // ===================== PANTRY: Create, Read, Update, Delete =====================

    // CREATE
    public void addItem(PantryItem item) {
        getWritableDatabase().insert("pantry", null, itemToValues(item));
    }

    // UPDATE
    public void updateItem(PantryItem item) {
        getWritableDatabase().update("pantry", itemToValues(item),
                "id = ?", new String[]{String.valueOf(item.id)});
    }

    // DELETE
    public void deleteItem(long id) {
        getWritableDatabase().delete("pantry", "id = ?", new String[]{String.valueOf(id)});
    }

    // READ one item (used by the edit screen)
    public PantryItem getItem(long id) {
        Cursor c = getReadableDatabase().query("pantry", null,
                "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (c.moveToFirst()) {
            item = readItem(c);
        }
        c.close();
        return item;
    }

    // READ all items (used by the pantry list), sorted A-Z
    public List<PantryItem> getAllItems() {
        List<PantryItem> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("pantry", null, null, null,
                null, null, "name COLLATE NOCASE");
        while (c.moveToNext()) {
            list.add(readItem(c));
        }
        c.close();
        return list;
    }

    // Turns a PantryItem into the key/value pairs SQLite needs
    private ContentValues itemToValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put("name", item.name);
        values.put("quantity", item.quantity);
        values.put("unit", item.unit);
        values.put("expiry", item.expiry);
        return values;
    }

    // Turns the current database row into a PantryItem.
    // Column order is: 0=id, 1=name, 2=quantity, 3=unit, 4=expiry
    private PantryItem readItem(Cursor c) {
        return new PantryItem(c.getLong(0), c.getString(1), c.getDouble(2),
                c.getString(3), c.getString(4));
    }

    // ===================== RECIPES (read only) =====================

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("recipes", null, null, null, null, null, "name");
        while (c.moveToNext()) {
            Recipe recipe = new Recipe(c.getLong(0), c.getString(1), c.getString(2));
            loadIngredients(recipe);
            list.add(recipe);
        }
        c.close();
        return list;
    }

    public Recipe getRecipe(long id) {
        Cursor c = getReadableDatabase().query("recipes", null,
                "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        Recipe recipe = null;
        if (c.moveToFirst()) {
            recipe = new Recipe(c.getLong(0), c.getString(1), c.getString(2));
            loadIngredients(recipe);
        }
        c.close();
        return recipe;
    }

    // Fills recipe.ingredients from the recipe_ingredients table
    private void loadIngredients(Recipe recipe) {
        Cursor c = getReadableDatabase().query("recipe_ingredients", null,
                "recipe_id = ?", new String[]{String.valueOf(recipe.id)}, null, null, null);
        // Column order: 0=id, 1=recipe_id, 2=name, 3=quantity, 4=unit
        while (c.moveToNext()) {
            recipe.ingredients.add(new RecipeIngredient(c.getString(2), c.getDouble(3), c.getString(4)));
        }
        c.close();
    }

    // ===================== SEED DATA: 18 recipes =====================

    // Saves one recipe. Each ingredient is written as "name|quantity|unit".
    private void addRecipe(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("steps", steps);
        long recipeId = db.insert("recipes", null, recipeValues);

        for (String text : ingredients) {
            String[] parts = text.split("\\|");
            ContentValues v = new ContentValues();
            v.put("recipe_id", recipeId);
            v.put("name", parts[0]);
            v.put("quantity", Double.parseDouble(parts[1]));
            v.put("unit", parts[2]);
            db.insert("recipe_ingredients", null, v);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Melt butter in a pan.\n3. Whisk eggs and scramble gently.\n4. Serve on toast.",
                "egg|3|pcs", "bread|2|pcs", "butter|10|g");
        addRecipe(db, "Tomato Pasta",
                "1. Boil pasta until soft.\n2. Fry garlic in olive oil.\n3. Add chopped tomatoes and simmer 10 minutes.\n4. Mix with the pasta.",
                "pasta|200|g", "tomato|4|pcs", "garlic|2|pcs", "olive oil|2|tbsp");
        addRecipe(db, "Cheese Omelette",
                "1. Whisk the eggs.\n2. Melt butter in a pan and pour in the eggs.\n3. Add grated cheese, fold and serve.",
                "egg|3|pcs", "cheese|50|g", "butter|10|g");
        addRecipe(db, "Pancakes",
                "1. Mix flour, sugar, eggs and milk into a batter.\n2. Fry spoonfuls in a buttered pan.\n3. Flip when bubbles appear.",
                "flour|200|g", "milk|300|ml", "egg|2|pcs", "sugar|2|tbsp", "butter|20|g");
        addRecipe(db, "Egg Fried Rice",
                "1. Fry chopped onion in oil.\n2. Add cooked rice and stir-fry.\n3. Push aside and scramble the eggs.\n4. Add soy sauce.",
                "rice|200|g", "egg|2|pcs", "onion|1|pcs", "soy sauce|2|tbsp", "vegetable oil|1|tbsp");
        addRecipe(db, "Potato Soup",
                "1. Fry onion in butter.\n2. Add cubed potatoes and water, simmer 20 minutes.\n3. Blend and stir in milk.",
                "potato|4|pcs", "onion|1|pcs", "butter|20|g", "milk|250|ml");
        addRecipe(db, "Garlic Bread",
                "1. Mix butter with crushed garlic.\n2. Spread on the bread.\n3. Bake 10 minutes at 200C.",
                "bread|4|pcs", "butter|40|g", "garlic|3|pcs");
        addRecipe(db, "Chicken Stir-fry",
                "1. Slice the chicken and peppers.\n2. Fry chicken in oil until cooked.\n3. Add onion, peppers and soy sauce.",
                "chicken breast|2|pcs", "onion|1|pcs", "bell pepper|2|pcs", "soy sauce|2|tbsp", "vegetable oil|1|tbsp");
        addRecipe(db, "Tuna Mayo Sandwich",
                "1. Drain the tuna and mix with mayonnaise.\n2. Spread on bread and close the sandwich.",
                "bread|2|pcs", "tuna|1|pcs", "mayonnaise|2|tbsp");
        addRecipe(db, "Lentil Curry",
                "1. Fry onion and garlic.\n2. Add curry powder, tomatoes and lentils.\n3. Add water and simmer 25 minutes.",
                "lentil|200|g", "onion|1|pcs", "tomato|2|pcs", "garlic|2|pcs", "curry powder|2|tsp");
        addRecipe(db, "Honey Porridge",
                "1. Heat oats and milk for 5 minutes, stirring.\n2. Add honey on top.",
                "oats|80|g", "milk|300|ml", "honey|1|tbsp");
        addRecipe(db, "Mac and Cheese",
                "1. Boil the macaroni.\n2. Melt butter, add milk and cheese to make a sauce.\n3. Mix in the macaroni.",
                "macaroni|200|g", "cheese|150|g", "milk|250|ml", "butter|20|g");
        addRecipe(db, "Banana Smoothie",
                "1. Blend bananas, milk and honey until smooth.",
                "banana|2|pcs", "milk|300|ml", "honey|1|tbsp");
        addRecipe(db, "Guacamole",
                "1. Mash the avocados.\n2. Stir in diced tomato and onion.\n3. Squeeze in the lime juice.",
                "avocado|2|pcs", "tomato|1|pcs", "onion|1|pcs", "lime|1|pcs");
        addRecipe(db, "Veggie Omelette",
                "1. Dice onion, pepper and tomato.\n2. Soften them in a pan.\n3. Pour in whisked eggs and cook through.",
                "egg|3|pcs", "onion|1|pcs", "bell pepper|1|pcs", "tomato|1|pcs");
        addRecipe(db, "Chickpea Salad",
                "1. Drain the chickpeas.\n2. Chop tomatoes and cucumber.\n3. Mix with olive oil and lemon juice.",
                "chickpea|400|g", "tomato|2|pcs", "cucumber|1|pcs", "olive oil|2|tbsp", "lemon|1|pcs");
        addRecipe(db, "Loaded Baked Potato",
                "1. Bake potatoes for 60 minutes at 200C.\n2. Split open and add butter and grated cheese.",
                "potato|2|pcs", "butter|20|g", "cheese|50|g");
        addRecipe(db, "French Toast",
                "1. Whisk eggs and milk.\n2. Soak the bread slices.\n3. Fry in butter until golden.",
                "bread|4|pcs", "egg|2|pcs", "milk|100|ml", "butter|15|g");
    }
}
