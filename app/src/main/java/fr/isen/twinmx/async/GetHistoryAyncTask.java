package fr.isen.twinmx.async;

import android.os.AsyncTask;
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
        this.requestListener.onResponseReceived(results);
    }
}
