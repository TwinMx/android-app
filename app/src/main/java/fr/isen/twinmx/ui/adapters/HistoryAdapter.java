package fr.isen.twinmx.ui.adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.model.History;
import fr.isen.twinmx.ui.holders.HistoryHolder;
import fr.isen.twinmx.ui.listeners.ClickListener;

/**
 * Created by pierredfc.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {

    private List<History> history;
    private ClickListener listener;

    public HistoryAdapter(List<History> results, ClickListener listener)
    {
        this.history = results;
        this.listener = listener;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryHolder(v);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position)
    {
        if (position < this.getItemCount())
        {
            final History currentHistory = this.history.get(position);

            if (currentHistory != null)
            {
                holder.name.setText(currentHistory.getName());
                holder.date.setText(currentHistory.getDate());

                Picasso.with(TMApplication.getContext())
                        .load(currentHistory.getImage())
                        .placeholder(R.drawable.ic_motorcycle_black_24dp)
                        .into(holder.image);

                holder.setView(currentHistory, this.listener);
            }
        }
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}