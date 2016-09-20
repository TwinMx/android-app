package fr.isen.twinmx.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;

import fr.isen.twinmx.R;


public class TMDrawer implements Drawer.OnDrawerItemClickListener {

    AccountHeader accountHeader;
    Drawer drawer;

    Activity activity;
    Toolbar toolbar;
    Bundle savedInstanceState;

    private ArrayList<IDrawerItem> items = new ArrayList<>();

    public TMDrawer(Bundle savedInstanceState, Activity activity, Toolbar toolbar) {
        this.savedInstanceState = savedInstanceState;
        this.activity = activity;
        this.toolbar = toolbar;

        this.initDrawer();
        this.drawer.setOnDrawerItemClickListener(this);
    }

    private void initDrawer()
    {
        this.initDrawerItems();
        this.setAccountHeader();

        //create the drawer and remember the `Drawer` result object
        this.drawer = new DrawerBuilder()
                .withActivity(this.activity)
                .withRootView(R.id.drawer_container)
                .withToolbar(this.toolbar)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(this.accountHeader)
                .withCloseOnClick(true)
                .addDrawerItems(items.toArray(new IDrawerItem[items.size()]))
                .withSelectedItemByPosition(1)
                .build();

        this.drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }

    private void setAccountHeader()
    {
        // Create the AccountHeader
        this.accountHeader = new AccountHeaderBuilder()
                .withActivity(this.activity)
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        this.accountHeader.setBackgroundRes(R.color.colorPrimary);
    }

    private void initDrawerItems()
    {
        addDrawerItem(R.string.drawer_home);
        addDrawerItem(R.string.drawer_launch);
        addDrawerItem(R.string.drawer_history);

        this.items.add(new DividerDrawerItem());

        addDrawerItem(R.string.drawer_instruction);
        addDrawerItem(R.string.drawer_settings);
    }

    private void addDrawerItem(int resId)
    {
        this.items.add(new PrimaryDrawerItem().withName(resId));
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        drawer.closeDrawer();
       //TODO
        return true;
    }
}