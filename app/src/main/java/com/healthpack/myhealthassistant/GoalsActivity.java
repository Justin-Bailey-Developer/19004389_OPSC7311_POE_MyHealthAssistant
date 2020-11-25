package com.healthpack.myhealthassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GoalsActivity extends AppCompatActivity {

    Goals goals;

    TextView tvWeight;
    EditText etWeightGoal, etCaloriesGoal, etStepsGoal;
    Button btnSaveGoals;

    String measurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        tvWeight = findViewById(R.id.tvWeight);
        etWeightGoal = findViewById(R.id.etWeightGoal);
        etCaloriesGoal = findViewById(R.id.etCaloriesGoal);
        etStepsGoal = findViewById(R.id.etStepsGoal);

        btnSaveGoals = findViewById(R.id.btnSaveGoals);

        PrefillFields();
        final Settings settings = Singleton.getInstance().getDataManager().pullSettings();

        btnSaveGoals.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String weightGoal, calorieGoal, stepGoal;
                weightGoal = etWeightGoal.getText().toString().trim();
                calorieGoal = etCaloriesGoal.getText().toString().trim();
                stepGoal = etStepsGoal.getText().toString().trim();

                if(!(weightGoal.isEmpty() || calorieGoal.isEmpty() || stepGoal.isEmpty())) {

                    goals = new Goals();
                    goals.setCalorieGoal(Integer.parseInt(etCaloriesGoal.getText().toString().trim()));
                    goals.setStepGoal(Integer.parseInt(etStepsGoal.getText().toString().trim()));

                    //Check if a conversion is needed
                    if(settings.isMetric()) { //user is using metric system

                        goals.setWeightGoal(Double.parseDouble(etWeightGoal.getText().toString().trim()));
                    }
                    else
                    {
                        if(settings.isMetric() == false){ //user is using imperial, so it needs to be transferred into metric and then stored (all firebase data is metric)

                            //convert the user's input into metric kgs from lbs
                            goals.setWeightGoal(Singleton.getInstance().getConversionManager().PoundsToKilograms(Double.parseDouble(etWeightGoal.getText().toString().trim())));
                        }
                    }

                    Singleton.getInstance().getDataManager().pushGoalsInfo(goals);

                    Toast.makeText(GoalsActivity.this, "Your goals have been updated", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(GoalsActivity.this, "You need to fill in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
         });
    }

    public void PrefillFields(){

        goals = Singleton.getInstance().getDataManager().pullGoals();
        double weightGoal;

        if(goals != null){

            etCaloriesGoal.setText("" + goals.getCalorieGoal());
            etStepsGoal.setText("" + goals.getStepGoal());

            Settings settings = Singleton.getInstance().getDataManager().pullSettings();

            //Check that the user does have settings saved
            if(settings != null) {

                //Check for the user's preference
                if (settings.isMetric()) {
                    measurement = "(kg)";
                    weightGoal = goals.getWeightGoal();
                    etWeightGoal.setText("" + weightGoal);
                }
                else
                {
                    measurement = "(lb)";
                    weightGoal = Singleton.getInstance().getConversionManager().KilogramsToPounds(goals.getWeightGoal());
                    etWeightGoal.setText("" + weightGoal);
                }

                tvWeight.setText("Weight " + measurement);
            }

        }
    }
}