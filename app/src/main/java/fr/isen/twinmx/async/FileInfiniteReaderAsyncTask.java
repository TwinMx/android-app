package fr.isen.twinmx.async;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
            int wait = 0;
            try {
                input = this.bluetooth.getActivity().getResources().getAssets().open("measures/" + fileName);
                BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
                while (!hasToStop() && (line = bfr.readLine()) != null) {
                    this.bluetooth.onDataReceived(Integer.parseInt(line));
                    if ((++wait) % 10 == 0) { //500 µs
                        wait = 0;
                        Thread.sleep(1);
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
