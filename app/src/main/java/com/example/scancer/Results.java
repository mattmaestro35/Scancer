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

import java.util.ArrayList;
import java.util.List;

public class Results extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_results);

        Button returnButton = findViewById(R.id.returnButton);
        Intent intent = getIntent();

        ImageView picturePreview = findViewById(R.id.pictureView);
        Intent myIntent = getIntent();
        List<String> types = new ArrayList<>();
        types.add("Melanocytic nevi");
        types.add("Melanoma");
        types.add("Benign Keratosis");
        types.add("Actinic Keratoses");
        types.add("Dermatofibroma");
        types.add("Vascular Skin Lesion");
        types.add("Basal Cell Carcinoma");

        //id0 - id6 for doubles

        String output = "";
        TextView resultView = findViewById(R.id.resultView0);
        for (int i = 0; i < 7; i++) {
            double odds = myIntent.getDoubleExtra("id" + i, 0);
            double roundedOdds = Math.round(odds * 10) / 10;
            String type = types.get(i);
            String outputAdd = type + ": " + roundedOdds + "%" + "\n";
            output = output + outputAdd;
        }

        resultView.setText(output);
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
