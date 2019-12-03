package com.example.scancer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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
                Intent newIntent = new Intent(PictureTaken.this, Results.class);
                newIntent.putExtra("currentPhotoPath", currentPhotoPath);
                startActivity(newIntent);
                finish();
            }
        });
    }

    private void setPicDisplay() {
        ImageView picturePreview = findViewById(R.id.picturePreview);

        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
        picturePreview.setImageBitmap(imageBitmap);
    }
}
