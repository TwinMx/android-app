package fr.isen.twinmx.async;

import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clement on 10/02/2017.
 */

public abstract class StoppableAsyncTask<T,U,W> extends AsyncTask<T,U,W> {

    private boolean stopped = true;
    private boolean stop = false;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.stopped = false;
        this.stop = false;
    }

    public boolean hasToStop() {
        return stop;
    }

    public boolean isStopped() {
        return stopped;
    }

    @Override
    protected void onPostExecute(W o) {
        super.onPostExecute(o);
        this.stopped = true;
    }

    public void stop() {
        this.stop = true;
    }

    public void stopAndWait(){
        stop();
        try {
            this.get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            if (!isStopped()) {
                stopAndWait();
            }
        }
    }


}
