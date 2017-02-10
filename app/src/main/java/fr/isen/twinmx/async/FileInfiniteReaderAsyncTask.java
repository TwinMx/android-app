package fr.isen.twinmx.async;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import fr.isen.twinmx.model.TMFile;
import fr.isen.twinmx.utils.bluetooth.TMBluetooth;

/**
 * Created by Clement on 08/02/2017.
 */

public class FileInfiniteReaderAsyncTask extends StoppableAsyncTask<Void, Void, Void> {

    private final TMFile file;
    private final TMBluetooth bluetooth;

    public FileInfiniteReaderAsyncTask(TMBluetooth bluetooth, TMFile file) {
        this.bluetooth = bluetooth;
        this.file = file;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String fileName = file.getFileName();
        InputStream input;
        while (!hasToStop()) {
            String line;
            boolean wait = true;
            try {
                input = this.bluetooth.getContext().getResources().getAssets().open("measures/" + fileName);
                BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
                while (!hasToStop() && (line = bfr.readLine()) != null) {
                    this.bluetooth.onDataReceived(Integer.parseInt(line));
                    if (wait) { //500 µs
                        wait = false;
                        Thread.sleep(1);
                    } else {
                        wait = true;
                    }
                }
                input.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }




}
