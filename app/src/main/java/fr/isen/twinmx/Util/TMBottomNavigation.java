package fr.isen.twinmx.Util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import fr.isen.twinmx.Activities.MainActivity;
import fr.isen.twinmx.R;

/**
 * Created by pierredfc.
 */
public class TMBottomNavigation implements AHBottomNavigation.OnTabSelectedListener {

    private Activity callback;
    private Toolbar toolbar;
    private Bundle savedInstanceState;
    private AHBottomNavigation navigation;

    public TMBottomNavigation(AHBottomNavigation navigation, Bundle savedInstanceState, Activity activity, Toolbar toolbar) {
        this.navigation = navigation;
        this.savedInstanceState = savedInstanceState;
        this.callback = activity;
        this.toolbar = toolbar;

        this.initNavigation();
        this.navigation.setOnTabSelectedListener(this);
    }

    public void initNavigation()
    {
        // Create items
        final AHBottomNavigationItem acquisition = new AHBottomNavigationItem(R.string.bnav_acquisition, R.drawable.ic_assessment_black_24dp, R.color.colorAccent);
        final AHBottomNavigationItem history = new AHBottomNavigationItem(R.string.bnav_history, R.drawable.ic_history_black_24dp, R.color.colorAccent);
        final AHBottomNavigationItem instruction = new AHBottomNavigationItem(R.string.bnav_instruction, R.drawable.ic_help_black_24dp, R.color.colorAccent);
        final AHBottomNavigationItem settings = new AHBottomNavigationItem(R.string.bnav_settings, R.drawable.ic_settings_applications_black_24dp, R.color.colorAccent);

        // Add items
        this.navigation.addItem(acquisition);
        this.navigation.addItem(history);
        this.navigation.addItem(instruction);
        this.navigation.addItem(settings);

        this.navigation.setAccentColor(Color.parseColor("#FF5722"));

        this.navigation.setForceTitlesDisplay(true);
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected)
    {
        return !wasSelected && this.callback instanceof  MainActivity && ((MainActivity) this.callback).onTabSelected(position);
    }

    public interface THBottomNavigationCallback
    {
        boolean onTabSelected(int position);
    }
}
