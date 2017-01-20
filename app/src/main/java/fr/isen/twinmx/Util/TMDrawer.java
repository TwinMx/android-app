package fr.isen.twinmx.util;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;

import fr.isen.twinmx.R;


public class TMDrawer implements Drawer.OnDrawerItemClickListener {
    private AccountHeader accountHeader;
    private Drawer drawer;

    private Activity callback;
    private Toolbar toolbar;
    private Bundle savedInstanceState;

    private int currentPosition;

    private ArrayList<IDrawerItem> items = new ArrayList<>();

    public TMDrawer(Bundle savedInstanceState, Activity activity, Toolbar toolbar) {
        this.savedInstanceState = savedInstanceState;
        this.callback = activity;
        this.toolbar = toolbar;

        this.initDrawer();
        this.drawer.setOnDrawerItemClickListener(this);
    }

    private void initDrawer()
    {
        this.initDrawerItems();
        this.setAccountHeader();

        //create the drawer and remember the `Drawer` result object
      /*  this.drawer = new DrawerBuilder()
                .withActivity(this.callback)
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
        this.currentPosition = 1;*/
    }

    private void setAccountHeader()
    {
        // Create the AccountHeader
        this.accountHeader = new AccountHeaderBuilder()
                .withActivity(this.callback)
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
        this.addDrawerItemWithIcon(R.string.bnav_history,  R.drawable.ic_history_black_24dp);

        this.items.add(new DividerDrawerItem());

        this.addDrawerItem(R.string.bnav_instruction);
        this.addDrawerItem(R.string.bnav_settings);
    }

    private void addDrawerItemWithIcon(int resId, int iconId)
    {
        this.items.add(new PrimaryDrawerItem().withName(resId).withIcon(iconId));
    }

    private void addDrawerItem(int resId)
    {
        this.items.add(new PrimaryDrawerItem().withName(resId));
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        drawer.closeDrawer();
        final OnMenuItemClickCallback callback = (OnMenuItemClickCallback) this.callback;
        if (callback != null)
        {
            callback.onMenuItemClick(position, this.currentPosition);
            this.currentPosition = position;
            return true;
        }
        return false;
    }

    public interface OnMenuItemClickCallback {
        /**
         * Called when a menu item has been clicked
         */
        void onMenuItemClick(int position, int actualPos);
    }
}