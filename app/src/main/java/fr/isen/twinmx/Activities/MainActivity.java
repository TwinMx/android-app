package fr.isen.twinmx.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import fr.isen.twinmx.Util.TMBottomNavigation;
import fr.isen.twinmx.Util.TMDrawer;

public class MainActivity extends AppCompatActivity implements TMBottomNavigation.THBottomNavigationCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.setTitle(R.string.app_name);

        final TMBottomNavigation nav = new TMBottomNavigation(this.navigation, savedInstanceState, this, this.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        this.launchAcquisition();
    }

    @Override
    public boolean onTabSelected(int position) {
        switch (position)
        {
            case 0:
                this.launchAcquisition();
                break;
            case 1:
                this.launchHistory();
                break;
            case 2:
                this.launchHelp();
                break;
            case 3:
                this.launchSettings();
                break;
            default:
                return false;
        }
        return true;
    }

    private void launchAcquisition()
    {
        final BluetoothFragment bluetoothFragment = new BluetoothFragment();
        final FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainActivityContainer, bluetoothFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void launchHistory()
    {

    }

    private void launchHelp()
    {

    }

    private void launchSettings()
    {

    }
}
