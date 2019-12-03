package com.example.scancer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String disclaimerText1 = "Scancer is not official medical advice.";
    static final String disclaimerText2 = "It is intended only as a preliminary tool. and and and and and and and and and and beter";
    static final String disclaimerText3 = "Accuracy is not guaranteed.";
    static final String disclaimerText4 = "Serious concerns should be brought to a medical " +
            "professional.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);;
        /* Make button to take a picture */
        TextView disclaimer2 = findViewById(R.id.disclaimer2);
        disclaimer2.setText(disclaimerText1);
        TextView disclaimer3 = findViewById(R.id.disclaimer3);
        disclaimer3.setText(disclaimerText2);
        TextView disclaimer4 = findViewById(R.id.disclaimer4);
        disclaimer4.setText(disclaimerText3);
        TextView disclaimer5 = findViewById(R.id.disclaimer5);
        disclaimer5.setText(disclaimerText4);
        final Button understandButton = findViewById(R.id.understandButton);
        System.out.println("Envi = " + Environment.getExternalStorageState());
        understandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainMenu();

            }
        });


    }

    private void goToMainMenu() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        finish();
    }

}
