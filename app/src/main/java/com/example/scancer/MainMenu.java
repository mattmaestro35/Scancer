package com.example.scancer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static java.text.DateFormat.getDateTimeInstance;

public class MainMenu extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        /* Make button to take a picture */
        ActivityCompat.requestPermissions(this,new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        final Button tryExampleButton = findViewById(R.id.tryExample);
        tryExampleButton.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
            /* Convert example image to bitmap. */
            Bitmap testImg = BitmapFactory.decodeResource(MainMenu.this.getResources(),
                    R.drawable.examplepic1);
            /* Save bitmap image. */
            File photoFile;
            try {
                photoFile = createImageFile();
                String file_path = photoFile.getAbsolutePath();
                File file = new File(file_path);
                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                testImg.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                System.out.println("Failed while making example picture," + e);
            }

            putImageAndGotoConfirm();
        }});

        final ImageButton takePhotoButton = findViewById(R.id.imageButton);

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainMenu.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    System.out.println("Don't have permission to write external storage.");
                }
            }
        });
        setUpButtonDarkener();
    }

    /* Runs after image is saved from saved.
    Adds it to gallery, then runs function to put as extra and start next activity. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
            putImageAndGotoConfirm();
        } else {
            System.out.println("No permission to add to gallery.");
        }
    }



    /* Puts image saved as an extra and starts PictureTaken activity. */
    private void putImageAndGotoConfirm() {
        Intent intent = new Intent(this, PictureTaken.class);
        intent.putExtra("imagePath", currentPhotoPath);
        startActivity(intent);
    }

    /* Creates a file for an image with a unique file path. */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = getDateTimeInstance().format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    /* Dispatch the intent to take a picture with the camera.
    Returns an activity result after the user confirms their picture. */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;

            try {
                if (ContextCompat.checkSelfPermission(MainMenu.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    photoFile = createImageFile();
                } else {
                    System.out.println("Permission wasn't granted inside dispatchTakePictureIntent");
                }
            } catch (IOException ex) {
                System.out.println("Failed to create image file, error = " + ex);
                // Error occurred while creating the File
            }


            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.scancer.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /* After an image is saved, this adds it to the user's photo gallery. */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /* Code that makes camera button darker when pressed by user. */
    private void setUpButtonDarkener() {

        ImageView imageView = findViewById(R.id.imageButton);
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}
