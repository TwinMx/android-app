package fr.isen.twinmx.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.fragments.BluetoothFragment;
import fr.isen.twinmx.fragments.HelpFragment;
import fr.isen.twinmx.fragments.HistoryFragment;
import fr.isen.twinmx.fragments.SettingsFragment;
import fr.isen.twinmx.R;
import fr.isen.twinmx.Receivers.BluetoothIconReceiver;
import fr.isen.twinmx.util.Bluetooth.TMBluetoothManager;
import fr.isen.twinmx.util.TMBottomNavigation;
import fr.isen.twinmx.model.History;
import fr.isen.twinmx.ui.listeners.ClickListener;


public class MainActivity extends AppCompatActivity implements TMBottomNavigation.THBottomNavigationCallback, ClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation navigation;

    @BindView(R.id.bluetoothIcon)
    ImageView bluetoothIcon;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private BroadcastReceiver bluetoothIconReceiver;

    private TMBluetoothManager bluetoothManager; //Keep a pointer to avoid GC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.setTitle(R.string.app_name);

        final TMBottomNavigation nav = new TMBottomNavigation(this.navigation, savedInstanceState, this, this.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        this.bluetoothManager = TMBluetoothManager.makeInstance(this);
        this.bluetoothIconReceiver = new BluetoothIconReceiver(bluetoothIcon, viewPager);

        this.bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TMBluetoothManager.getInstance().getBluetooth().tryConnection();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.launchFragment(new BluetoothFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(bluetoothIconReceiver, new IntentFilter(BluetoothIconReceiver.ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(bluetoothIconReceiver);
    }

    @Override
    public boolean onTabSelected(int position) {
        switch (position)
        {
            case 0:
                this.launchFragment(new BluetoothFragment());
                break;
            case 1:
                this.launchFragment(new HistoryFragment());
                break;
            case 2:
                this.launchFragment(new HelpFragment());
                break;
            case 3:
                this.launchFragment(new SettingsFragment());
                break;
            default:
                return false;
        }
        return true;
    }

    private void launchFragment(Fragment fragment)
    {
        final FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainActivityContainer, fragment);
        transaction.commit();
    }

    @Override
    public void onItemClick(History history) {
        Toast.makeText(this, history.getName(), Toast.LENGTH_SHORT).show();
    }
}
