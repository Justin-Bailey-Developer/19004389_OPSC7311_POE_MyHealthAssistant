package com.healthpack.myhealthassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    Settings settings;
    Button btnSaveSettings;
    RadioGroup rgMeasurement;
    RadioButton rbtnMetric, rbtnImperial;

    DataManager dataManager = new DataManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        rgMeasurement = findViewById(R.id.rgMeasurement);
        rbtnMetric = findViewById(R.id.rbtnMetric);
        rbtnImperial = findViewById(R.id.rbtnImperial);

        settings = Singleton.getInstance().getDataManager().pullSettings();

        //display the user's current settings if they have previously save settings
        if(settings != null) {
            if (settings.isMetric()) { //set the user's current settings
                rbtnMetric.setChecked(true);
            } else {
                rbtnImperial.setChecked(true);
            }
        }

        //Save the user's selected setting
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                settings = new Settings();

                if(rbtnMetric.isChecked()){
                    settings.setMetric(true);
                }
                else{
                    if(rbtnImperial.isChecked()){
                        settings.setMetric(false);
                    }
                }

                dataManager.pushSettings(settings); //update the settings
                Toast.makeText(SettingsActivity.this, "Your new settings have been saved", Toast.LENGTH_SHORT).show(); //inform the user the save was successful
            }
         });

    }
}