package fr.isen.twinmx.ui.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.isen.twinmx.R;
import fr.isen.twinmx.model.History;
import fr.isen.twinmx.ui.listeners.ClickListener;

/**
 * Created by pierredfc.
 */

public class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private CardView cardView;
    public ImageView image;
    public TextView name;
    public TextView date;

    private ClickListener clickListener;
    private History history;

    public HistoryHolder(View itemView)
    {
        super(itemView);
        this.cardView = (CardView) itemView.findViewById(R.id.history_cardview);
        this.image = (ImageView) itemView.findViewById(R.id.history_image);
        this.name = (TextView) itemView.findViewById(R.id.history_name);
        this.date = (TextView) itemView.findViewById(R.id.history_date);
    }

    public void setView(History history, ClickListener listener)
    {
        this.history = history;
        this.clickListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        this.clickListener.onItemClick(history);
    }
}
