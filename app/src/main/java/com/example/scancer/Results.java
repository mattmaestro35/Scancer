package com.example.scancer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Results extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_results);

        Button returnButton = findViewById(R.id.returnButton);
        Intent intent = getIntent();

        ImageView picturePreview = findViewById(R.id.pictureView);
        Intent myIntent = getIntent();
        String type = myIntent.getStringExtra("type");
        double odds = myIntent.getDoubleExtra("odds", 0);
        double roundedOdds = Math.round(100.0 * odds) / 100.0;
        String output = type + " : " + roundedOdds + " %";

        TextView resultsText = findViewById(R.id.resultText);
        resultsText.setText(output);

        String currentPhotoPath = myIntent.getStringExtra("currentPhotoPath");
        System.out.println("currentPhotoPath = " + currentPhotoPath);
        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
        System.out.println("imageBitmap = " + imageBitmap);
        picturePreview.setImageBitmap(imageBitmap);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
