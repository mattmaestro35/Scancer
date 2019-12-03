package com.example.scancer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainMenu extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);;

        /* Make button to take a picture */
        ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        final ImageButton takePhotoButton = findViewById(R.id.imageButton);

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainMenu.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    System.out.println("Permission is granted.");
                    dispatchTakePictureIntent();
                } else {
                    System.out.println("Don't have permission.");
                }


            }
        });

        /* Code that makes camera button darker when you press it. */
        ImageView imageView = (ImageView)findViewById(R.id.imageButton);

        //set the ontouch listener
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
            System.out.println("Finished galleryAddPic");

            System.out.println("Current photo path = " + currentPhotoPath);
            File photoFile = new File(currentPhotoPath);
            String encodedString;
            System.out.println(photoFile + " = photoFile");
            try {
                InputStream inputStream = new FileInputStream(photoFile);
                System.out.println("input stream OK");
                byte[] bytes;
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                System.out.println("Output stream OK");
                try {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                    System.out.println("Byte reading OK");
                } catch (IOException e) {
                    System.out.println("Byte reading error = " + e);
                    e.printStackTrace();
                }
                bytes = output.toByteArray();
                encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                System.out.println("String encoding OK");
                //System.out.println("encoded String = " + encodedString);
                //makeJsonObjReq(encodedString);

                /* send the user to a new activity */

                Intent intent = new Intent(this, PictureTaken.class);
                intent.putExtra("imagePath", currentPhotoPath);
                System.out.println("Intent Made OK");
                startActivity(intent);
                System.out.println("Activity made ok...?");

            } catch (Exception e) {
                System.out.println("Error when setting up streams, " + e);
            }

        } else {
            System.out.println("No permission to add to gallery.");
        }

    }


    String currentPhotoPath;


    private void makeJsonObjReq(String encodedString) {
        System.out.println("Making object request.");
        RequestQueue queue = Volley.newRequestQueue(MainMenu.this);
        String URL = "PUT_SERVER_URL_HERE";
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("imageCode", encodedString);
        } catch (Exception e) {
            System.out.println("jsonObject put error = " + e);
        }
        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    URL, jsonObj,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //start a new activity or something
                            System.out.println("Server did the thing!");
                            System.out.println("Response = " + response);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Volley error response, error = " + error);
                }
            });
            queue.add(jsonObjReq);
            System.out.println("Request made.");
        } catch (Exception e) {
            System.out.println("exception error jsonobject error = " + e);
        }


        {

            /**
             * Passing some request headers
             * */
            /* I don't know what this is or what it does, but it was included in the code
            I found for JSONObject requests. */
            /*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
            */

        };

        // Adding request to request queue



    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        System.out.println("storageDir = " + storageDir);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                if (ContextCompat.checkSelfPermission(MainMenu.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission was also granted inside intent");
                    photoFile = createImageFile();
                    System.out.println("ImageFile created");
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
