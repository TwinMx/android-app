package fr.isen.twinmx;

import android.app.Application;
import android.content.Context;

/**
 * Created by pierredfc.
 */
public class TMApplication extends Application {

    public static final int APP_VERSION = 1;

    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this.getApplicationContext();
    }

    public static Context getContext()
    {
        return context;
    }
}
