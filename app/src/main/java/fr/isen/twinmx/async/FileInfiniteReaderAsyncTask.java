package fr.isen.twinmx.async;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

/**
 * Created by Clement on 08/02/2017.
 */

public class FileInfiniteReaderAsyncTask extends AsyncTask<Void, Void, Void> {

    private boolean stop;
    private final TMBluetooth bluetooth;

    public FileInfiniteReaderAsyncTask(TMBluetooth bluetooth) {
        this.bluetooth = bluetooth;
        this.stop = false;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (!stop) {
            String filename = "twinmax-moto2.txt";
            String line;
            InputStream input;
            try {
                input = this.bluetooth.getContext().getResources().getAssets().open(filename);
                BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
                while (!stop && (line=bfr.readLine())!=null){
                    Log.d("log", line);
                    this.bluetooth.onDataReceived(Integer.parseInt(line));
                    Thread.sleep(0, 600000);
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public void stop() {
        this.stop = true;
    }
}
