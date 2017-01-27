package fr.isen.twinmx;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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

        final RealmConfiguration configuration = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(configuration);
    }

    public static Context getContext()
    {
        return context;
    }

    public static String loadString(int resourceId) {
        return getContext().getResources().getString(resourceId);
    }

    public static String loadString(int resourceId, String... strings) {
        return String.format(loadString(resourceId), (Object[]) strings);
    }
}
