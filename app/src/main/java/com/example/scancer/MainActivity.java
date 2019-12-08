package com.example.scancer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    /* Declaring strings for disclaimer. */
    static final String disclaimerText2 = "Scancer is an app that, given an image of a skin lesion,\n" +
            "uses machine learning to calculate the chance of a malignant skin cancer.";
    static final String disclaimerText3 = "Scancer does not give official medical advice.";
    static final String disclaimerText4 = "It is intended only as a preliminary tool. \n" +
            "Accuracy is not guaranteed.";
    static final String disclaimerText5 = "Serious concerns should be brought to a medical " +
            "professional.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* Set up disclaimer. */
        TextView disclaimer1 = findViewById(R.id.disclaimer2);
        disclaimer1.setText(disclaimerText2);
        TextView disclaimer3 = findViewById(R.id.disclaimer3);
        disclaimer3.setText(disclaimerText3);
        TextView disclaimer4 = findViewById(R.id.disclaimer4);
        disclaimer4.setText(disclaimerText4);
        TextView disclaimer5 = findViewById(R.id.disclaimer5);
        disclaimer5.setText(disclaimerText5);

        final Button understandButton = findViewById(R.id.understandButton);
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
