package com.example.scancer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
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

        /* Invisible until yesUpload is clicked. */
        final TextView waiting = findViewById(R.id.waiting);
        waiting.setVisibility(View.GONE);

        /* Get photo path from intent. */
        Intent myIntent = getIntent();
        currentPhotoPath = myIntent.getStringExtra("imagePath");
        setPicDisplay();

        /* Button to not upload and return to main menu. */
        Button noUpload = findViewById(R.id.noUpload);
        noUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //return to main menu
            }
        });

        /* Button to upload and proceed to results. */
        Button yesUpload = findViewById(R.id.yesUpload);
        yesUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                File photoFile = new File(currentPhotoPath);
                /* Set up byte input/output streams to convert image to base64 String. */
                String encodedString;
                try {
                    InputStream inputStream = new FileInputStream(photoFile);
                    byte[] bytes;
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    try {
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        System.out.println("Byte reading error = " + e);
                        e.printStackTrace();
                    }

                    bytes = output.toByteArray();
                    encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);

                    /* Send server request. */
                    makeJsonObjReq(encodedString);
                    waiting.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    System.out.println("Error when setting up streams, " + e);
                }


            }
        });
    }

    /* Sends encoded string to server. */
    private void makeJsonObjReq(String encodedString) {
        RequestQueue queue = Volley.newRequestQueue(PictureTaken.this);

        /*Put server URL here. */
        String URL = "http://10.0.2.2/post";
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("img", encodedString);
        } catch (Exception e) {
            System.out.println("jsonObject put error = " + e);
        }

        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    URL, jsonObj,
                    new Response.Listener<JSONObject>() {
                        /* Runs when a response is received from server. */
                        @Override
                        public void onResponse(JSONObject response) {

                            Intent newIntent = new Intent(PictureTaken.this, Results.class);
                            newIntent.putExtra("currentPhotoPath", currentPhotoPath);
                            try {
                                JSONArray results = response.getJSONArray("result");
                                /* Unpack the JSONArray response into a set of doubles.
                                Put the doubles as extras. */
                                for (int i = 0; i < results.length(); i++) {
                                    double odds = results.getDouble(i);
                                    newIntent.putExtra("id" + i, odds);
                                }
                            } catch (Exception e) {
                                System.out.println("JSON response error = " + e);
                            }

                            startActivity(newIntent);
                            finish();
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
    }

    /* Sets the image display in the middle of the screen. */
    private void setPicDisplay() {
        ImageView picturePreview = findViewById(R.id.picturePreview);

        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
        picturePreview.setImageBitmap(imageBitmap);
    }
}
