package fr.isen.twinmx.ui.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.model.History;
import fr.isen.twinmx.ui.listeners.ClickListener;

/**
 * Created by Clement on 05/01/2017.
 */

public class MotoHolder extends RecyclerView.ViewHolder {
    private CardView cardView;
    private ImageView image;
    private TextView name;
    private TextView date;

    private Moto moto;

    public MotoHolder(View itemView)
    {
        super(itemView);
        this.cardView = (CardView) itemView.findViewById(R.id.history_cardview);
        this.image = (ImageView) itemView.findViewById(R.id.history_image);
        this.name = (TextView) itemView.findViewById(R.id.history_name);
        this.date = (TextView) itemView.findViewById(R.id.history_date);
    }

    public void bind(Moto moto)
    {
        this.moto = moto;
        this.name.setText(moto.getName());
        this.date.setText(moto.getDate());
        Picasso.with(TMApplication.getContext())
                .load(moto.getImage())
                .placeholder(R.drawable.ic_motorcycle_black_24dp)
                .into(this.image);


    }

}
