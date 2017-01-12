package fr.isen.twinmx.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;

/**
 * Created by Clement on 06/01/2017.
 */

public class MotoFormActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moto_form);
        ButterKnife.bind(this);
        this.setSupportActionBar(this.toolbar);
        this.setTitle(R.string.app_name);

    }

}
