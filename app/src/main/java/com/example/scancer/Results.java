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

        Intent myIntent = getIntent();

        /* Button to return to main menu. */
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* Set up image display. */
        ImageView picturePreview = findViewById(R.id.pictureView);
        String currentPhotoPath = myIntent.getStringExtra("currentPhotoPath");
        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
        picturePreview.setImageBitmap(imageBitmap);

        /* Set up list of cancer types. */
        List<String> types = new ArrayList<>();
        types.add("Melanocytic nevi");
        types.add("Melanoma");
        types.add("Benign Keratosis");
        types.add("Actinic Keratoses");
        types.add("Dermatofibroma");
        types.add("Vascular Skin Lesion");
        types.add("Basal Cell Carcinoma");

        /* Set up string to display results. */
        String output = "";
        TextView resultView = findViewById(R.id.resultView0);
        for (int i = 0; i < 7; i++) {
            double odds = 100 * myIntent.getDoubleExtra("id" + i, 0);
            double roundedOdds = (double) Math.round(odds * 10) / 10;
            String type = types.get(i);
            String outputAdd = type + ": " + roundedOdds + "%" + "\n";
            /* Add to output string. */
            output = output + outputAdd;
        }

        /* Display results. */
        resultView.setText(output);
    }

}
