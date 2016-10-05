package fr.isen.twinmx.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.Fragments.BluetoothFragment;
import fr.isen.twinmx.R;
import fr.isen.twinmx.Receivers.BluetoothIconReceiver;
import fr.isen.twinmx.Util.Bluetooth.TMBluetoothManager;
import fr.isen.twinmx.Util.TMBottomNavigation;
import fr.isen.twinmx.Util.TMDrawer;

public class MainActivity extends AppCompatActivity {

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

        this.launchHome();

        this.bluetoothManager = TMBluetoothManager.makeInstance(this);
        bluetoothIconReceiver = new BluetoothIconReceiver(bluetoothIcon, viewPager);

        this.bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TMBluetoothManager.getInstance().getBluetooth().tryConnection();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(bluetoothIconReceiver, new IntentFilter(BluetoothIconReceiver.ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetoothIconReceiver);
    }

    private void launchHome()
    {
        final BluetoothFragment bluetoothFragment = new BluetoothFragment();
        final FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainActivityContainer, bluetoothFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
