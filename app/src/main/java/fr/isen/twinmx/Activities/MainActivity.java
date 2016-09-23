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

public class MainActivity extends AppCompatActivity implements TMDrawer.OnMenuItemClickCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private TMDrawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.setTitle(R.string.app_name);

        this.drawer = new TMDrawer(savedInstanceState, this, this.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        this.launchHome();
    }

    /**
     * 1 : Home
     * 2 : Acquisition
     * 3 : History
     * 4 : Instruction manual
     * 5 : Settings
     * @param position
     */
    @Override
    public void onMenuItemClick(int position, int actualPos) {
        if (position == actualPos)
        {
            return;
        }

        switch(position)
        {
            case 1:
                this.launchHome();
                break;
            case 2:
                Toast.makeText(this, "Acquisition", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, "History", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(this, "IM", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
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
