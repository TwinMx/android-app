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
import fr.isen.twinmx.fragments.ChartFragment;
import fr.isen.twinmx.listeners.OnMotoHistoryClickListener;
import fr.isen.twinmx.ui.holders.DialogMotoHolder;
import fr.isen.twinmx.ui.holders.MotoHolder;
import fr.isen.twinmx.util.TMDeviceHolder;

/**
 * Created by Clement on 27/01/2017.
 */
public class DialogMotoAdapter extends RecyclerView.Adapter<DialogMotoHolder> {

    private List<Moto> items;

    private OnMotoHistoryClickListener callback;

    public DialogMotoAdapter(List<Moto> items, OnMotoHistoryClickListener callback) {
        this.items = items;
        this.callback = callback;
    }

    public void setItems(List<Moto> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public DialogMotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(DialogMotoHolder.LAYOUT, parent, false);
        return new DialogMotoHolder(v, this.callback);
    }

    @Override
    public void onBindViewHolder(DialogMotoHolder holder, int position) {
        if (position < this.getItemCount() - 1) {
            holder.bind(items.get(position));
        } else if (position == this.getItemCount() - 1) {
            holder.bind(null);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() + 1 : 1;
    }
}
