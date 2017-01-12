package fr.isen.twinmx.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;

/**
 * Created by Clement on 06/01/2017.
 */

public class MotoFormActivity extends AppCompatActivity {

    @OnClick(R.id.form_back)
    public void back()
    {
          this.finish();
    }

    @OnClick(R.id.form_moto_addLibrary)
    public void addPhotoFromLibrary()
    {
        // TODO
    }

    @OnClick(R.id.form_moto_launchCamera)
    public void launchCamera()
    {
        // TODO
    }

    @OnClick(R.id.form_moto_create)
    public void create()
    {
        // TODO
          /*  MotoRepository rep = MotoRepository.getInstance();

     try {
     rep.create(new Moto("NewMoto"+Moto.getIndex()));
     } catch (RepositoryException e) {
     e.printStackTrace();
     }
    */
        this.finish();
    }

    @BindView(R.id.form_moto_photo)
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moto_form);
        ButterKnife.bind(this);
    }
}
