package fr.isen.twinmx.async;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.isen.twinmx.model.TMFile;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

/**
 * Created by Clement on 08/02/2017.
 */

public class FileInfiniteReaderAsyncTask extends AsyncTask<Void, Void, Void> {

    private final TMFile file;
    private boolean stop;
    private final TMBluetooth bluetooth;
    private boolean stopped;

    public FileInfiniteReaderAsyncTask(TMBluetooth bluetooth, TMFile file) {
        this.bluetooth = bluetooth;
        this.file = file;
        this.stop = false;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String fileName = file.getFileName();
        while (!stop) {
            String line;
            InputStream input;
            boolean wait = true;
            try {
                input = this.bluetooth.getContext().getResources().getAssets().open("measures/"+fileName);
                BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
                while (!stop && (line=bfr.readLine())!=null){
                    this.bluetooth.onDataReceived(Integer.parseInt(line));
                    if (wait) { //500 µs
                        wait = false;
                        Thread.sleep(1);
                    } else {
                        wait = true;
                    }
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

    public boolean isStopped() {
        return stopped;
    }
}
