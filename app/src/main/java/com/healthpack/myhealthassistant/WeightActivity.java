package com.healthpack.myhealthassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WeightActivity extends AppCompatActivity {

    TextView tvStartingWeightValue, tvCurrentWeightValue, tvWeightGoalValue, tvWeightDeficitValue;
    EditText etUpdateWeight;
    Button btnUpdateWeight;
    ListView lvWeightHistory;
    ProgressBar pbWeightProgress;

    Weight weight;
    Weight startingWeight;
    Weight newWeight;

    ConversionManager conversionManager = new ConversionManager();
    double startingWeightValue, currentWeightValue, weightGoal, weightDeficit;
    boolean isMetric;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(mAuth.getCurrentUser().getUid());

    List<Weight> weightList;
    ArrayAdapter adapter;

    int pbWeightStatus;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        tvStartingWeightValue = findViewById(R.id.tvStartingWeightValue);
        tvCurrentWeightValue = findViewById(R.id.tvCurrentWeightValue);
        tvWeightGoalValue = findViewById(R.id.tvWeightGoalValue);
        tvWeightDeficitValue = findViewById(R.id.tvWeightDeficitValue);

        etUpdateWeight = findViewById(R.id.etUpdateWeight);

        btnUpdateWeight = findViewById(R.id.btnUpdateWeight);

        pbWeightProgress = findViewById(R.id.pbWeightProgress);

        lvWeightHistory = findViewById(R.id.lvWeightHistory);

        //Push data
        btnUpdateWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newWeight = new Weight();
                newWeight.setDate(Singleton.getInstance().getDataManager().getToday());
                double updatedWeight = Double.parseDouble(etUpdateWeight.getText().toString());
                newWeight.setWeight(updatedWeight);

                Singleton.getInstance().getDataManager().pushWeight(newWeight);
            }
        });

        myRef.child("weight info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                isMetric = Singleton.getInstance().getDataManager().pullSettings().isMetric();
                weight = new Weight();
                weightList = new ArrayList<Weight>();
                for(DataSnapshot stepsSnapshot : snapshot.getChildren())
                {
                    weight = stepsSnapshot.getValue(Weight.class);

                    //convert weight value to pounds, depending on the user's settings. All measurements are stored as metric in Firebase
                    if(isMetric == false)
                    {
                        weight.setWeight(conversionManager.KilogramsToPounds(weight.getWeight()));
                    }

                    weightList.add(weight);
                }

                if(weightList.size() > 0) {

                    SortByDate();
                    int currentPos = weightList.size();
                    DisplayWeightData(weightList.get(currentPos - 1));

                    adapter = new ArrayAdapter(WeightActivity.this, android.R.layout.simple_list_item_1, weightList);

                    lvWeightHistory.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeightActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Bubble sort the Weight objects in the weightList by their date from newest to oldest
    public void SortByDate(){

        List<Weight> sorted = weightList;
        Weight outerWeight;
        Weight innerWeight;
        Weight tempWeight;

        //Collections.sort(weightList, (Weight w1, Weight w2) -> FormatDate(w1.getDate()).compareTo(FormatDate(w2.getDate())));

        for(int outer = 0; outer < weightList.size(); outer++){

            //outerWeight = weightList.get(outer);

            for(int inner = outer + 1; inner< weightList.size(); inner++){

                //innerWeight = weightList.get(inner);

                //if(FormatDate(innerWeight.getDate()).compareTo(FormatDate(outerWeight.getDate())) > 0){
                if  (FormatDate(weightList.get(inner).getDate()).compareTo(FormatDate(weightList.get(outer).getDate())) > 0){

                    tempWeight = weightList.get(outer);
                    weightList.set(outer, weightList.get(inner));
                    weightList.set(inner, tempWeight);

                    /*tempWeight = outerWeight;
                    weightList.set(outer, innerWeight);
                    weightList.set(inner, tempWeight);*/
                }
            }
        }
    }

    //format the date so that it can be compared to (it is put in the format of yyyy-MM-dd)
    public String FormatDate(String date){

        String []split = date.split("-");

        return (split[2] + "-" + split[1] + "-" + split[0]);
    }

    public void DisplayWeightData(Weight foundWeight){

        weight = foundWeight;
        Goals goals = Singleton.getInstance().getDataManager().pullGoals();
        String measurement;
        int startingPos, currentPos;

        if(weight != null && goals!= null) {

            if (Singleton.getInstance().getDataManager().pullSettings().isMetric()) {

                startingPos = weightList.size() - 1;
                currentPos = 0;

                measurement = "kg";
                //startingWeightValue = getStartingWeight();
                /*startingWeightValue = foundWeight.getWeight();
                currentWeightValue = weight.getWeight();
                weightGoal = goals.getWeightGoal();
                weightDeficit = (weightGoal - currentWeightValue) * -1.0;*/

                startingWeightValue = weightList.get(startingPos).getWeight();
                currentWeightValue = weightList.get(currentPos).getWeight();
                weightGoal = goals.getWeightGoal();
                weightDeficit = (weightGoal - currentWeightValue) * -1.0;
            }
            else {

                measurement = "lb";
                //startingWeightValue = getStartingWeight();
                startingWeightValue = foundWeight.getWeight();
                currentWeightValue = weight.getWeight();
                weightGoal = conversionManager.KilogramsToPounds(goals.getWeightGoal());
                weightDeficit = Math.round(((weightGoal - currentWeightValue) * -1.0)*100.0) / 100.0;

                /*startingWeightValue = Math.round(conversionManager.KilogramsToPounds(getStartingWeight())*100.0)/100.0;
                currentWeightValue = Math.round(conversionManager.KilogramsToPounds(weight.getWeight())*100.0)/100.0;
                weightGoal = Math.round(conversionManager.KilogramsToPounds(goals.getWeightGoal())*100.0)/100.0;
                weightDeficit = Math.round((weightGoal - currentWeightValue)*100.0)/100.0 * -1.0;*/
            }

            /*startingWeightValue = getStartingWeight();
            currentWeightValue = weight.getWeight();
            weightGoal = goals.getWeightGoal();
            weightDeficit = (weightGoal - currentWeightValue) * -1.0;*/

            tvStartingWeightValue.setText("" + startingWeightValue + " " + measurement);
            tvCurrentWeightValue.setText("" + currentWeightValue + " " + measurement);
            tvWeightGoalValue.setText("" + weightGoal + " " + measurement);
            tvWeightDeficitValue.setText("" + weightDeficit + " " + measurement + " to go");

            if (startingWeightValue > weightGoal) //user wants to lose weight
            {

                if (startingWeightValue > currentWeightValue && currentWeightValue > weightGoal) { //user has made weight loss progress, but has not reached their goal yet
                    pbWeightStatus = (int) (((startingWeightValue - currentWeightValue) / (startingWeightValue - weightGoal)) * 100.0);
                }
                else{
                    //user has reached their goal
                    if(currentWeightValue == weightGoal){
                        pbWeightStatus = 100;
                    }
                }
            }
            else {
                if (startingWeightValue < weightGoal) { //user wants to gain weight

                    if (startingWeightValue < currentWeightValue && currentWeightValue < weightGoal) { //user has made weight gain progress, but has not reached their goal yet
                        pbWeightStatus = (int) (((startingWeightValue - currentWeightValue) / (startingWeightValue - weightGoal)) * 100.0);
                    }
                    else{
                        //user has reached their goal
                        if(currentWeightValue == weightGoal){
                            pbWeightStatus = 100;
                        }
                    }
                }
            }

            updateProgress();
        }

    }

    //calculate the step progress and update the progress bar status
    public void updateProgress(){

        /*
         * CODE ATTRIBUTION
         * AUTHOR: Adobe in a Minute (YouTube channel)
         * SOURCE: https://www.youtube.com/watch?v=SaTx-gLLxWQ&ab_channel=AdobeinaMinute
         */

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.SystemClock.sleep(50);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pbWeightProgress.setProgress(pbWeightStatus);
                    }
                });
            }
        }).start();

        //END CODE ATTRIBUTION
    }

    //compare the dates
    public double getStartingWeight(){

        double val = 0.0;
        startingWeight = weightList.get(0);

        for(int pos = 1; pos < weightList.size(); pos++) {

            //String []sp1 = weightList.get(pos).getDate().split("-");
            //String []sp2 = startingWeight.getDate().split("-");

            String []sp2 = weightList.get(pos).getDate().split("-");
            String []sp1 = startingWeight.getDate().split("-");

            //check if the new date is more recent than the first
            if (sp2[2].compareTo(sp1[2]) >= 0 && sp2[1].compareTo(sp1[1]) >= 0 && sp2[0].compareTo(sp1[0]) > 0) {
                startingWeight = weightList.get(pos);
            }
        }

        return startingWeight.getWeight();
    }
}