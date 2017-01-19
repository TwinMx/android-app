package fr.isen.twinmx.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

import butterknife.BindView;
import butterknife.ButterKnife;

import fr.isen.twinmx.database.RealmHelper;
import fr.isen.twinmx.database.TMRealmModule;

import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.fragments.BluetoothFragment;
import fr.isen.twinmx.fragments.HelpFragment;
import fr.isen.twinmx.fragments.HistoryFragment;
import fr.isen.twinmx.fragments.SettingsFragment;
import fr.isen.twinmx.R;
import fr.isen.twinmx.listeners.OnMotoHistoryClickListener;
import fr.isen.twinmx.util.TMBottomNavigation;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import fr.isen.twinmx.model.History;
import fr.isen.twinmx.ui.listeners.ClickListener;

public class MainActivity extends AppCompatActivity implements TMBottomNavigation.THBottomNavigationCallback, ClickListener, OnMotoHistoryClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation navigation;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.setTitle(R.string.app_name);
        final TMBottomNavigation nav = new TMBottomNavigation(this.navigation, savedInstanceState, this, this.toolbar);
        this.navigation.manageFloatingActionButtonBehavior(this.floatingActionButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //instantiate the realm and do migration (compulsory)
        final RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("TwinMax")
                .schemaVersion(3)
                //.migration(new TMMigration())
                .deleteRealmIfMigrationNeeded()
                .modules(new TMRealmModule())
                .build();
        RealmHelper.setRealm(Realm.getInstance(configuration));

        if (savedInstanceState == null)
        {
            this.launchFragment(new BluetoothFragment(), false);
        }
    }

    @Override
    public boolean onTabSelected(int position) {
        this.floatingActionButton.setOnClickListener(null);
        switch (position)
        {
            case 0:
                this.launchFragment(new BluetoothFragment(), false);
                break;
            case 1:
                this.launchFragment(HistoryFragment.newInstance(this, this.floatingActionButton), true);
                break;
            case 2:
                this.launchFragment(new HelpFragment(), false);
                break;
            case 3:
                this.launchFragment(new SettingsFragment(), false);
                break;
            default:
                return false;
        }
        return true;
    }

    private void launchFragment(Fragment fragment, boolean isFab)
    {
        final FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainActivityContainer, fragment);
        transaction.commit();
        this.floatingActionButton.setVisibility(!isFab ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onItemClick(History history) {
        Toast.makeText(this, history.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMotoHistoryClick(Moto moto) {
        Intent intent = new Intent(this, MotoDetailActivity.class);
        intent.putExtra("motoID", moto.getId());
        startActivity(intent);
    }
}
