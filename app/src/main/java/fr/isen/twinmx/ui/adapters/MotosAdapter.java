package fr.isen.twinmx.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.isen.twinmx.R;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.fragments.HistoryFragment;
import fr.isen.twinmx.listeners.OnMotoHistoryClickListener;
import fr.isen.twinmx.ui.holders.MotoHolder;

/**
 * Created by Clement on 05/01/2017.
 */

public class MotosAdapter extends RecyclerView.Adapter<MotoHolder> {


    private List<Moto> items;

    private OnMotoHistoryClickListener callback;

    public MotosAdapter(List<Moto> items, OnMotoHistoryClickListener callback) {
        this.items = items;
        this.callback = callback;
    }

    public List<Moto> getItems() {
        return this.items;
    }

    public void setItems(List<Moto> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public MotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new MotoHolder(v, this.callback);
    }

    @Override
    public void onBindViewHolder(MotoHolder holder, int position) {
        if (position < this.getItemCount()) {
            holder.bind(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}

