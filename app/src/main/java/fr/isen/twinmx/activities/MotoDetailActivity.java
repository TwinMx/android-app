package fr.isen.twinmx.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.MaintenanceRepository;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.listeners.OnMotoMaintenanceClickListener;
import fr.isen.twinmx.ui.adapters.MaintenanceAdapter;
import fr.isen.twinmx.util.CircleTransformation;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by pierredfc.
 */

public class MotoDetailActivity extends AppCompatActivity implements OnMotoMaintenanceClickListener {

    private Moto moto;

    private static final int REQUEST_SELECT_PHOTO = 2;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.moto_detail_photo)
    ImageView photo;

    @BindView(R.id.acquisition_recycler)
    RecyclerView acquisitions;

    @BindView(R.id.acquisition_title)
    TextView acquisitionsTitle;

    @OnClick(R.id.moto_detail_photo)
    public void onMotoPhotoClick()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_PHOTO);
    }

    private MaintenanceAdapter maintenanceAdapter;

    private RealmResults<Maintenance> maintenanceFinder;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.modify_moto:
                modifyNamesMoto();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

        this.initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.maintenanceFinder != null) this.maintenanceFinder.removeChangeListeners();
        this.maintenanceFinder = MaintenanceRepository.getInstance().findAllAsync(new RealmChangeListener<RealmResults<Maintenance>>() {
            @Override
            public void onChange(RealmResults<Maintenance> element) {
                onMaintenanceResponseReceived(element);
            }
        });
    }

    public void onMaintenanceResponseReceived(List<Maintenance> maintenances) {
        if (maintenances != null && !maintenances.isEmpty()) {
            this.maintenanceAdapter.setMaintenances(maintenances);
            this.acquisitionsTitle.setText(getString(R.string.last_acquisitions));
        }
        else
        {
            this.acquisitionsTitle.setText(getString(R.string.no_acquisitions));
        }
    }

    private void initRecyclerView()
    {
        this.acquisitions.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(TMApplication.getContext());
        this.acquisitions.setLayoutManager(layoutManager);
        this.acquisitions.setAdapter(this.maintenanceAdapter = new MaintenanceAdapter(new ArrayList<Maintenance>(0), this));
    }

    private void modifyNamesMoto()
    {
        new MaterialDialog.Builder(this)
                .title(R.string.modify_moto_title_dialog)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .positiveText(R.string.menu_modify)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (!input.toString().isEmpty())
                        {
                            try {
                                Moto updatedMoto = new Moto(moto);
                                updatedMoto.setName(input.toString());
                                moto = MotoRepository.getInstance().create(updatedMoto);
                                updateViewAfterMotoUpdate(false);
                            } catch (RepositoryException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
    }

    private void updateViewAfterMotoUpdate(boolean imageUpdate)
    {
        this.setTitle(this.moto.getName());

        if (imageUpdate)
        {
            Picasso.with(TMApplication.getContext())
                    .load(this.moto.getImage())
                    .transform(new CircleTransformation())
                    .placeholder(R.drawable.ic_motorcycle_black_24dp)
                    .into(this.photo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case REQUEST_SELECT_PHOTO:
                if (resultCode == RESULT_OK && data != null)
                {
                    Uri uri = data.getData();

                    try {
                        Moto updatedMoto = new Moto(moto);
                        updatedMoto.setImage(uri.toString());
                        moto = MotoRepository.getInstance().create(updatedMoto);
                        updateViewAfterMotoUpdate(true);
                    } catch (RepositoryException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onMotoMaintenanceClick(Maintenance maintenance) {
        // TODO fragment with graph etc.
    }
}
