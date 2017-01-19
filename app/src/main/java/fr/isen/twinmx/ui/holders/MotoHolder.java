package fr.isen.twinmx.ui.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.listeners.OnMotoHistoryClickListener;
import fr.isen.twinmx.util.CircleTransformation;

/**
 * Created by Clement on 05/01/2017.
 */

public class MotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.history_cardview)
    CardView cardView;

    @BindView(R.id.history_image)
    ImageView image;

    @BindView(R.id.history_name)
    TextView name;

    @BindView(R.id.history_date)
    TextView date;

    private Moto moto;

    private OnMotoHistoryClickListener callback;

    public MotoHolder(View itemView, OnMotoHistoryClickListener callback) {
        super(itemView);
        this.callback = callback;
        itemView.setOnClickListener(this);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Moto moto) {
        this.moto = moto;
        this.name.setText(moto.getName());
        this.date.setText(moto.getDate());
        Picasso.with(TMApplication.getContext())
                .load(moto.getImage())
                .transform(new CircleTransformation())
                .placeholder(R.drawable.ic_motorcycle_black_24dp)
                .into(this.image);
    }

    @Override
    public void onClick(View view) {
        if (this.moto != null)
            this.callback.onMotoHistoryClick(moto);
    }
}
