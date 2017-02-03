package fr.isen.twinmx.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.model.Moto;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(MotoHolder.LAYOUT, parent, false);
        return new MotoHolder(v, this.callback);
    }

    @Override
    public void onBindViewHolder(MotoHolder holder, int position) {
        if (position < this.getItemCount()) {
            holder.bind(items.get(position));
        }
    }

    public void removeItem(int position)
    {
        try {
            MotoRepository.getInstance().delete(this.items.get(position));
        } catch (RepositoryException e) {
            Log.d("[MA][removeItem]", "Can't remove moto");
        }

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, this.items.size());
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}

