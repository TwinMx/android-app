package fr.isen.twinmx.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.listeners.OnMotoHistoryClickListener;

/**
 * Created by Clement on 27/01/2017.
 */

public class DialogMotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static final int LAYOUT = R.layout.dialog_moto_item;
    private final OnMotoHistoryClickListener callback;

    @BindView(R.id.dialog_moto_item_name)
    TextView name;

    @BindView(R.id.dialog_moto_item_image)
    ImageView image;

    private Moto moto;

    public DialogMotoHolder(View itemView, OnMotoHistoryClickListener callback) {
        super(itemView);
        this.callback = callback;
        itemView.setOnClickListener(this);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Moto moto) {
        this.moto = moto;
        this.setText(moto);
        this.setImage(this.moto != null);
    }

    private void setText(Moto moto) {
        if (moto != null) {
            this.name.setText(moto.getName());
        }
        else {
            this.name.setText(TMApplication.loadString(R.string.new_moto));
        }
    }

    private void setImage(boolean isMoto) {
        if (!isMoto) {
            this.image.setImageResource(R.drawable.ic_add_white_24dp);
            this.image.setBackgroundResource(R.drawable.circular_image_view_green);
        }
        else {
            this.image.setImageResource(R.drawable.ic_motorcycle_white_24dp);
            this.image.setBackgroundResource(R.drawable.circular_image_view_grey);
        }
    }

    @Override
    public void onClick(View v) {
        this.callback.onMotoHistoryClick(moto);
    }
}
