package fr.isen.twinmx.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.util.CircleTransformation;

/**
 * Created by pierredfc.
 */

public class MotoDetailActivity extends AppCompatActivity {

    private Moto moto;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.moto_detail_photo)
    ImageView photo;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.moto = MotoRepository.getInstance().findById(this.getIntent().getExtras().getLong("motoID"));

        setContentView(R.layout.activity_moto_detail);
        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.setTitle(this.moto.getName());

        Picasso.with(TMApplication.getContext())
                .load(moto.getImage())
                .transform(new CircleTransformation())
                .placeholder(R.drawable.ic_motorcycle_black_24dp)
                .into(this.photo);
    }
}
