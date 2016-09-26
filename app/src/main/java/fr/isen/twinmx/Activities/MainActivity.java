package fr.isen.twinmx.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import fr.isen.twinmx.Util.TMDrawer;

public class MainActivity extends AppCompatActivity {

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Create items
        AHBottomNavigationItem acquisition = new AHBottomNavigationItem(R.string.drawer_launch, R.drawable.ic_launch_black_24dp, R.color.colorAccent);
        AHBottomNavigationItem history = new AHBottomNavigationItem(R.string.drawer_history, R.drawable.ic_history_black_24dp, R.color.colorAccent);
        AHBottomNavigationItem instruction = new AHBottomNavigationItem(R.string.drawer_instruction, R.drawable.ic_help_black_24dp, R.color.colorAccent);
        AHBottomNavigationItem settings = new AHBottomNavigationItem(R.string.drawer_settings, R.drawable.ic_settings_applications_black_24dp, R.color.colorAccent);

        // Add items
        navigation.addItem(acquisition);
        navigation.addItem(history);
        navigation.addItem(instruction);
        navigation.addItem(settings);

        this.launchHome();
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
