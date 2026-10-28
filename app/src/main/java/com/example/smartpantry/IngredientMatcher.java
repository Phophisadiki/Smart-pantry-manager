package com.example.smartpantry;

import java.util.HashMap;
import java.util.List;

// THE STRICT-MATCHING RULE.
// A recipe is "makeable" only if EVERY ingredient is in the pantry
// in at least the required quantity. One missing ingredient = not suggested.
public class IngredientMatcher {

    // STEP 1 - Make names comparable: "Tomatoes " and "tomato" both become "tomato".
    public static String cleanName(String name) {
        String n = name.toLowerCase().trim().replaceAll("\\s+", " ");
        if (n.length() <= 3) return n;                       // too short to be a plural

        if (n.endsWith("ies")) {
            return n.substring(0, n.length() - 3) + "y";     // berries -> berry
        }
        if (n.endsWith("oes") || n.endsWith("ches") || n.endsWith("shes")) {
            return n.substring(0, n.length() - 2);           // tomatoes -> tomato
        }
        if (n.endsWith("ss") || n.endsWith("us")) {
            return n;                                        // hummus stays hummus
        }
        if (n.endsWith("s")) {
            return n.substring(0, n.length() - 1);           // eggs -> egg
        }
        return n;
    }

    // STEP 2 - Convert any amount to a base unit (grams, millilitres, or a plain count).
    public static double toBase(double quantity, String unit) {
        switch (unit.toLowerCase()) {
            case "kg":   return quantity * 1000;   // kilograms -> grams
            case "l":    return quantity * 1000;   // litres -> millilitres
            case "tsp":  return quantity * 5;      // teaspoons -> millilitres
            case "tbsp": return quantity * 15;     // tablespoons -> millilitres
            case "cup":  return quantity * 240;    // cups -> millilitres
            default:     return quantity;          // g, ml and pcs are already base units
        }
    }

    // Weight, volume and count can't be compared with each other (grams vs litres).
    public static String unitType(String unit) {
        switch (unit.toLowerCase()) {
            case "g":
            case "kg":
                return "weight";
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
            case "cup":
                return "volume";
            default:
                return "count";
        }
    }

    // A name plus unit type makes one "key", e.g. "tomato|count" or "flour|weight".
    private static String makeKey(String name, String unit) {
        return cleanName(name) + "|" + unitType(unit);
    }

    // STEP 3 - The actual rule.
    public static boolean canMake(Recipe recipe, List<PantryItem> pantry) {

        // 3a. Add up what the user has, per ingredient (so 200 g + 0.3 kg flour = 500 g).
        HashMap<String, Double> have = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = makeKey(item.name, item.unit);
            double amount = toBase(item.quantity, item.unit);
            if (have.containsKey(key)) {
                amount = amount + have.get(key);
            }
            have.put(key, amount);
        }

        // 3b. Check every ingredient the recipe needs.
        for (RecipeIngredient need : recipe.ingredients) {
            String key = makeKey(need.name, need.unit);

            if (!have.containsKey(key)) {
                return false;                                  // ingredient missing -> reject
            }
            if (have.get(key) < toBase(need.quantity, need.unit) - 0.0001) {
                return false;                                  // not enough -> reject
            }
        }

        // 3c. Nothing was missing, so the recipe qualifies.
        return true;
    }
}
