package com.example.scancer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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

public class PictureTaken extends AppCompatActivity {

    public String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_taken);
        System.out.println("AfterPicture activity started");
        Intent myIntent = getIntent();
        currentPhotoPath = myIntent.getStringExtra("imagePath");
        setPicDisplay();
        Button noUpload = findViewById(R.id.noUpload);
        noUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //return to main menu
            }
        });

        Button yesUpload = findViewById(R.id.yesUpload);
        yesUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

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
                    makeJsonObjReq(encodedString);

                    /* send the user to a new activity */


                } catch (Exception e) {
                    System.out.println("Error when setting up streams, " + e);
                }

                Intent newIntent = new Intent(PictureTaken.this, Results.class);
                newIntent.putExtra("currentPhotoPath", currentPhotoPath);
                startActivity(newIntent);
                finish();
            }
        });
    }

    private void makeJsonObjReq(String encodedString) {
        System.out.println("Making object request.");
        RequestQueue queue = Volley.newRequestQueue(PictureTaken.this);
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

    private void setPicDisplay() {
        ImageView picturePreview = findViewById(R.id.picturePreview);

        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
        picturePreview.setImageBitmap(imageBitmap);
    }
}
