package com.healthpack.myhealthassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OptionsActivity extends AppCompatActivity {


    CardView cvPersonalInfo, cvGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        cvPersonalInfo = findViewById(R.id.cvPersonalInfo);
        cvGoals = findViewById(R.id.cvGoals);

        cvPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OptionsActivity.this, PersonalInfoActivity.class); //create an intent to move the the next activity
                startActivity(i);
            }
        });

        cvGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OptionsActivity.this, GoalsActivity.class); //create an intent to move the the next activity
                startActivity(i);
            }
        });
    }
}