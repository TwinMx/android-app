package fr.isen.twinmx.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import fr.isen.twinmx.database.RealmHelper;
import fr.isen.twinmx.database.TMRealmModule;


import fr.isen.twinmx.database.model.Moto;

import fr.isen.twinmx.fragments.ChartFragment;

import fr.isen.twinmx.fragments.ManualFragment;
import fr.isen.twinmx.fragments.HistoryFragment;
import fr.isen.twinmx.R;

import fr.isen.twinmx.listeners.OnMotoHistoryClickListener;

import fr.isen.twinmx.Receivers.BluetoothIconReceiver;
import fr.isen.twinmx.util.Bluetooth.TMBluetooth;
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

    private static RealmConfiguration realmConfiguration;

    @BindView(R.id.bluetoothIcon)
    ImageView bluetoothIcon;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.bluetoothProgressIcon)
    ProgressBar bluetoothProgressBar;

    private BluetoothIconReceiver bluetoothIconReceiver;

    private TMBluetooth mBluetooth; //Keep a pointer to avoid GC


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);

        this.setTitle(R.string.bnav_acquisition);

        final TMBottomNavigation nav = new TMBottomNavigation(this.navigation, savedInstanceState, this, this.toolbar);
        this.navigation.manageFloatingActionButtonBehavior(this.floatingActionButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        this.mBluetooth = new TMBluetooth(this);
        this.bluetoothIconReceiver = new BluetoothIconReceiver(bluetoothIcon, bluetoothProgressBar, viewPager, mBluetooth);
        this.mBluetooth.setBluetoothIconReceiver(this.bluetoothIconReceiver);

        if (this.realmConfiguration == null) {
            this.realmConfiguration = new RealmConfiguration.Builder(this)
                    .name("TwinMax")
                    .schemaVersion(7)
                    .deleteRealmIfMigrationNeeded()
                    .modules(new TMRealmModule())
                    .build();
        }

        RealmHelper.setRealm(Realm.getInstance(this.realmConfiguration));

        if (savedInstanceState == null) {
            final ChartFragment chartFragment = ChartFragment.newInstance(this, mBluetooth);
            this.launchFragment(chartFragment, false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothIconReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(bluetoothIconReceiver);
    }

    @Override
    public boolean onTabSelected(int position) {
        this.floatingActionButton.setOnClickListener(null);
        switch (position) {
            case 0:
                final ChartFragment chartFragment = ChartFragment.newInstance(this, mBluetooth);
                this.launchFragment(chartFragment, false);
                break;
            case 1:
                this.launchFragment(HistoryFragment.newInstance(this, this.floatingActionButton), true);
                break;
            case 2:
                this.launchFragment(new ManualFragment(), false);
                break;
            default:
                return false;
        }
        return true;
    }

    private void launchFragment(Fragment fragment, boolean isFab) {
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

    protected void onStop() {
        super.onStop();
        mBluetooth.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TMBluetooth.REQUEST_ENABLE_BT) {
            if (resultCode == TMBluetooth.RESULT_ENABLE_BT_ALLOWED) {
                bluetoothIconReceiver.enabled();
            }
        }

    }

    @OnClick(R.id.bluetoothIcon)
    public void onBluetoothIconClick(View view) {
        if (mBluetooth.isBluetoothEnabled()) { //scan
            mBluetooth.scanDevices();
        } else {
            mBluetooth.enableBluetooth(); //prompt to enable bluetooth
        }
    }

}
