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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.setTitle(R.string.app_name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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
