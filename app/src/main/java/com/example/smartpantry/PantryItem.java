package com.example.smartpantry;

// One ingredient the user has at home (one row in the "pantry" table).
public class PantryItem {
    public long id;
    public String name;
    public double quantity;
    public String unit;     // g, kg, ml, l, tsp, tbsp, cup or pcs
    public String expiry;   // "yyyy-MM-dd", or "" if the user left it empty

    public PantryItem(long id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }
}
