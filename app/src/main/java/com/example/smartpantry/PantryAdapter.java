package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// The adapter connects the list of PantryItems to the RecyclerView on screen.
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // The activity implements this so it hears about taps on a row or its Delete button.
    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private List<PantryItem> items;
    private boolean showExpiry;      // comes from the Settings screen
    private Listener listener;

    public PantryAdapter(List<PantryItem> items, boolean showExpiry, Listener listener) {
        this.items = items;
        this.showExpiry = showExpiry;
        this.listener = listener;
    }

    // Holds the views of ONE row so they are not looked up again and again.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvExpiry;
        Button btnDelete;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvQty = view.findViewById(R.id.tvQty);
            tvExpiry = view.findViewById(R.id.tvExpiry);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    // Called when a new row layout is needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    // Called to fill one row with the data of the item at this position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.tvName.setText(item.name);
        holder.tvQty.setText(item.quantity + " " + item.unit);

        if (showExpiry && item.expiry != null && !item.expiry.isEmpty()) {
            holder.tvExpiry.setText("Expires: " + item.expiry);
            holder.tvExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    // How many rows the list has
    @Override
    public int getItemCount() {
        return items.size();
    }
}
