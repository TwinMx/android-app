package fr.isen.twinmx.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

import fr.isen.twinmx.TMApplication;

/**
 * Created by Clement on 02/03/2017.
 */

public class ImageConverter {

    private static boolean checkPermission(Activity context, String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (context.shouldShowRequestPermissionRationale(permission)) {
                    // Explain to the user why we need to read the contacts
                }

                context.requestPermissions(new String[]{permission}, requestCode);
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique
            }
        }
        return true;
    }


    public static Bitmap toBitmap(Activity activity, Uri uri) throws IOException {

        if (!checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, 1) || !checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, 2)) {
            return null;
        }

        ParcelFileDescriptor parcelFileDescriptor =
                TMApplication.getContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public static Uri toNewUri(Activity activity, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(TMApplication.getContext().getContentResolver(), inImage, "Moto", null);
        return Uri.parse(path);
    }

    public static Uri toNewUri(Activity activity, Uri uri) throws IOException {
        return toNewUri(activity, toBitmap(activity, uri));
    }

}
