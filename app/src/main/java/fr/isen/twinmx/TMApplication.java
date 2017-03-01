package fr.isen.twinmx;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * TwinMax application
 */
public class TMApplication extends Application {

    public static final int APP_VERSION = 1;

    /**
     * Context use during the app lifecycle.
     */
    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this.getApplicationContext();

        // Database default configuration
        final RealmConfiguration configuration = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(configuration);
    }

    public static Context getContext()
    {
        return context;
    }

    /**
     * Load a string from its resource Id.
     * @param resourceId
     * @return
     */
    public static String loadString(int resourceId) {
        return getContext().getResources().getString(resourceId);
    }

    /**
     * Load a string from its resource Id and replace string format.
     * @param resourceId
     * @param strings
     * @return
     */
    public static String loadString(int resourceId, String... strings) {
        return String.format(loadString(resourceId), (Object[]) strings);
    }
}
