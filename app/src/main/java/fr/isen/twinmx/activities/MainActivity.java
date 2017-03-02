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

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import butterknife.OnLongClick;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.RealmConfiguration;
import fr.isen.twinmx.database.TMRealmModule;


import fr.isen.twinmx.database.model.Moto;

import fr.isen.twinmx.fragments.ChartFragment;

import fr.isen.twinmx.fragments.ManualFragment;
import fr.isen.twinmx.fragments.HistoryFragment;
import fr.isen.twinmx.R;

import fr.isen.twinmx.listeners.OnMotoHistoryClickListener;

import fr.isen.twinmx.receivers.BluetoothIconReceiver;
import fr.isen.twinmx.utils.TMUtils;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;
import fr.isen.twinmx.utils.TMBottomNavigation;

import io.realm.Realm;

/**
 * Main activity class
 */
public class MainActivity extends AppCompatActivity implements TMBottomNavigation.THBottomNavigationCallback, OnMotoHistoryClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation navigation;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.bluetoothIcon)
    ImageView bluetoothIcon;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.bluetoothProgressIcon)
    ProgressBar bluetoothProgressBar;

    /**
     * Realm configuration
     */
    private static io.realm.RealmConfiguration realmConfiguration;

    /**
     * Bluetooth icon handler
     */
    private BluetoothIconReceiver bluetoothIconReceiver;

    /**
     * Twinmax bluetooth handler
     */
    private static TMBluetooth mBluetooth; //Keep a pointer to avoid GC

    /**
     * Static variables for fragment and shortcuts
     */
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    private static final String ACTION_SHORTCUT_ACQUISITION = "shortcut_acquisition";
    private static final String ACTION_SHORTCUT_HISTORY = "shortcut_history";
    private static final String ACTION_SHORTCUT_INSTRUCTION = "shortcut_instruction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);

        this.setTitle(R.string.bnav_acquisition);

        final TMBottomNavigation nav = new TMBottomNavigation(this.navigation, savedInstanceState, this, this.toolbar);
        this.navigation.manageFloatingActionButtonBehavior(this.floatingActionButton);
        if (this.getSupportActionBar() != null) this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (realmConfiguration == null) {
            realmConfiguration = new io.realm.RealmConfiguration.Builder(this)
                    .name("TwinMax")
                    .schemaVersion(TMApplication.APP_VERSION)
                    .deleteRealmIfMigrationNeeded()
                    .modules(new TMRealmModule()) //Register database collections
                    .build();
        }

        RealmConfiguration.setRealm(Realm.getInstance(realmConfiguration));

        // Restore an instance. For example, when changing orientation
        if (savedInstanceState == null) {
            MainActivity.mBluetooth = new TMBluetooth(this);
            this.bluetoothIconReceiver = new BluetoothIconReceiver(bluetoothIcon, bluetoothProgressBar, viewPager, mBluetooth);
            MainActivity.mBluetooth.setBluetoothIconReceiver(this.bluetoothIconReceiver);

            final Intent intent = getIntent();
            if (intent != null && intent.getAction() != null) {
                int tabPosition = findTabPosition(intent.getAction());
                if (tabPosition >= 0) {
                    this.navigation.setCurrentItem(tabPosition, false);
                    this.onTabSelected(tabPosition);
                }
                else {
                    final ChartFragment chartFragment = ChartFragment.newInstance(this, mBluetooth);
                    this.launchFragment(chartFragment, false);
                }
            }
            else {
                final ChartFragment chartFragment = ChartFragment.newInstance(this, mBluetooth);
                this.launchFragment(chartFragment, false);
            }
        }
        else
        {
            MainActivity.mBluetooth.setActivity(this);
            this.bluetoothIconReceiver = new BluetoothIconReceiver(bluetoothIcon, bluetoothProgressBar, viewPager, mBluetooth);
            MainActivity.mBluetooth.setBluetoothIconReceiver(this.bluetoothIconReceiver);
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof ChartFragment) {
                ChartFragment chartFragment = (ChartFragment) fragment;
                chartFragment.setBluetooth(mBluetooth);
                chartFragment.setActivity(this);
            }
            else if (fragment instanceof HistoryFragment)
            {
                this.floatingActionButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * @return the current fragment
     */
    private Fragment getCurrentFragment() {
        return this.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.bluetoothIconReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.bluetoothIconReceiver);
    }

    /**
     * Handling shortcut for SDK > 22
     * @param action
     * @return
     */
    private int findTabPosition(String action) {
        switch(action) {
            case ACTION_SHORTCUT_ACQUISITION:
                return 0;
            case ACTION_SHORTCUT_HISTORY:
                return 1;
            case ACTION_SHORTCUT_INSTRUCTION:
                return 2;
            default:
                return -1;
        }
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
        transaction.replace(R.id.mainActivityContainer, fragment, FRAGMENT_TAG);
        transaction.commit();
        this.floatingActionButton.setVisibility(!isFab ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onMotoHistoryClick(Moto moto) {
        final Intent intent = new Intent(this, MotoDetailActivity.class);
        intent.putExtra(TMUtils.MOTO_ID_INTENT, moto.getId());
        this.startActivity(intent);
    }

    protected void onStop() {
        super.onStop();

        mBluetooth.removeListeners();

        if(this.isFinishing())
        {
            mBluetooth.stopReadingFromFile();
            mBluetooth.stop();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TMBluetooth.REQUEST_ENABLE_BT) {
            if (resultCode == TMBluetooth.RESULT_ENABLE_BT_ALLOWED) {
                this.bluetoothIconReceiver.enabled();
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

    @OnLongClick(R.id.bluetoothIcon)
    public boolean onBluetoothIconLongClick(View view) {
        mBluetooth.showFilesDialog();
        return true;
    }
}
