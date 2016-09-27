package fr.isen.twinmx.async;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.isen.twinmx.listeners.RequestListener;
import fr.isen.twinmx.model.History;

/**
 * Created by pierredfc.
 */
public class GetHistoryAyncTask extends AsyncTask<String, Integer, List<History>> {

    private final RequestListener requestListener;

    public GetHistoryAyncTask(RequestListener requestListener)
    {
        this.requestListener = requestListener;
    }

    @Override
    protected List<History> doInBackground(String... strings)
    {
        return null; //@TODO DatabaseHelper
    }

    @Override
    protected void onPostExecute(List<History> results)
    {
        // Test for now @TODO
        final List<History> test = new ArrayList<>();
        /*test.add(new History("Harley", new Date()));
        test.add(new History("Scooter de Robert", new Date()));
        test.add(new History("Scoot", new Date()));
        test.add(new History("Grosse moto de Jean Pierre le nouvel arrivant", new Date()));
        test.add(new History("Harley", new Date()));
        test.add(new History("Test", new Date()));
        test.add(new History("Harley2", new Date()));
        test.add(new History("dfg", new Date()));*/
        this.requestListener.onResponseReceived(test);
    }
}
