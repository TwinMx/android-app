package fr.isen.twinmx.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.utils.CircleTransformation;

/**
 * Created by Clement on 06/01/2017.
 */

public class MotoFormActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_PHOTO = 2;

    @OnClick(R.id.form_back)
    public void back()
    {
          this.finish();
    }

    @OnClick(R.id.form_moto_addLibrary)
    public void addPhotoFromLibrary()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_PHOTO);
    }

    @OnClick(R.id.form_moto_launchCamera)
    public void launchCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile;
            try {
                photoFile = createImageFile();

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    this.photoURI = FileProvider.getUriForFile(TMApplication.getContext(),
                            "fr.isen.twinmx",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException ex) {
                Log.d("launchCamera", "Won't able to create a new file.");
            }
        }
    }

    @OnClick(R.id.form_moto_create)
    public void create()
    {
        if (this.isFormValidated())
        {
            MotoRepository rep = MotoRepository.getInstance();

            try {
                rep.create(new Moto(this.motosName.getText().toString(), this.photoURI != null ? this.photoURI.toString() : null));
                Log.d("MotoFormActivity", "Moto created.");
            } catch (RepositoryException e) {
                Log.d("MotoFormActivity", "Couldn't create moto into database.");
                e.printStackTrace();
            }
            this.finish();
        }
    }

    @BindView(R.id.form_moto_photo)
    ImageView photo;

    private Uri photoURI;

    @BindView(R.id.form_moto_text)
    TextView photoTextMessage;

    @BindView(R.id.form_moto_name)
    EditText motosName;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(requestCode == REQUEST_IMAGE_CAPTURE)
        {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moto_form);
        ButterKnife.bind(this);

        if (savedInstanceState != null)
        {
            String motosName = (String) savedInstanceState.get("motosName");
            if (!motosName.isEmpty()) this.motosName.setText(motosName);
            String motosURI = (String) savedInstanceState.get("motosURI");
            if(!motosURI.isEmpty()) this.loadMotoImage(Uri.parse(motosURI));
        }

        this.locationpermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    //this.photoURI = getImageUri(TMApplication.getContext(), (Bitmap)data.getExtras().get("data"));
                    this.loadMotoImage(this.photoURI);
                }
                break;
            case REQUEST_SELECT_PHOTO:
                if (resultCode == RESULT_OK && data != null)
                {
                    this.photoURI = data.getData();
                    this.loadMotoImage(this.photoURI);
                }
                break;
            default:
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void loadMotoImage(Uri uri)
    {
        Picasso.with(TMApplication.getContext())
                .load(uri)
                .resize(200,200)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(photo);

        photo.setVisibility(View.VISIBLE);
        photoTextMessage.setVisibility(View.GONE);
    }

    private boolean isFormValidated()
    {
        return !this.motosName.getText().toString().isEmpty();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("motosName", this.motosName.getText() != null ? this.motosName.getText().toString() : "");
        outState.putString("motosURI", this.photoURI != null ? this.photoURI.toString() : "");
    }

    private void locationpermission() {
        if (ContextCompat.checkSelfPermission(this
                ,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
