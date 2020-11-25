package com.healthpack.myhealthassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;

public class StepsActivity extends AppCompatActivity {

    TextView tvStepCountValue, tvStepGoalValue, tvStepDeficitValue;
    ListView lvStepsHistory;
    EditText etUpdateSteps;
    Button btnUpdateSteps;
    ProgressBar pbSteps2;

    Steps steps;
    Steps updatedSteps;
    DataManager dataManager = new DataManager();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(mAuth.getCurrentUser().getUid());

    List<Steps> stepsList;
    ArrayAdapter adapter;

    int pbStepsStatus;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        //Text view
        tvStepCountValue = findViewById(R.id.tvStepCountValue);
        tvStepGoalValue = findViewById(R.id.tvStepGoalValue);
        tvStepDeficitValue = findViewById(R.id.tvStepDeficitValue);

        //Progress bar
        pbSteps2 = (ProgressBar) findViewById(R.id.pbSteps2);

        //List view
        lvStepsHistory = findViewById(R.id.lvStepsHistory);

        //Edit text
        etUpdateSteps = findViewById(R.id.etUpdateSteps);

        //Buttons
        btnUpdateSteps = findViewById(R.id.btnUpdateSteps);

        //Push data
        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Steps newSteps = new Steps();
                newSteps.setDate(Singleton.getInstance().getDataManager().getToday());
                int newStepCount = Integer.parseInt(etUpdateSteps.getText().toString());

                if(steps != null){
                    newStepCount += steps.getCount();
                }

                newSteps.setCount(newStepCount);

                dataManager.pushSteps(newSteps);
            }
        });

        myRef.child("steps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                steps = new Steps();
                stepsList = new ArrayList<Steps>();
                for(DataSnapshot stepsSnapshot : snapshot.getChildren())
                {
                    steps = stepsSnapshot.getValue(Steps.class);
                    stepsList.add(steps);

                    //find the current day's steps
                    if(steps.getDate().equals(Singleton.getInstance().getDataManager().getToday())){
                        DisplayStepData(steps);
                    }
                }

                if(stepsList.size() > 0) {
                    SortByDate();
                    adapter = new ArrayAdapter(StepsActivity.this, android.R.layout.simple_list_item_1, stepsList);

                    lvStepsHistory.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StepsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Bubble sort the Steps objects in the weightList by their date from newest to oldest
    public void SortByDate(){

        //List<Weight> sorted = stepsList;
        Steps outerSteps;
        Steps innerSteps;
        Steps tempSteps;

        for(int outer = 0; outer < stepsList.size()-1; outer++){

            outerSteps = stepsList.get(outer);

            for(int inner = 1; inner< stepsList.size(); inner++){

                innerSteps = stepsList.get(inner);

                if(FormatDate(innerSteps.getDate()).compareTo(FormatDate(outerSteps.getDate())) > 0){

                    tempSteps = outerSteps;
                    stepsList.set(outer, innerSteps);
                    stepsList.set(inner, tempSteps);
                }
            }
        }
    }

    public String FormatDate(String date){

        String []split = date.split("-");

        return (split[2] + "-" + split[1] + "-" + split[0]);
    }

    public void DisplayStepData(Steps steps){

        Goals goals = Singleton.getInstance().getDataManager().pullGoals();

        if(steps != null && goals!= null) {

            int count = steps.getCount();
            int stepGoal = goals.getStepGoal();
            int stepDeficit = stepGoal - count;
            updateProgress(count, stepGoal);

            tvStepCountValue.setText(""+steps.getCount());
            tvStepGoalValue.setText(""+stepGoal);
            tvStepDeficitValue.setText(""+stepDeficit + " left to go");
        }
    }

    //calculate the step progress and update the progress bar status
    public void updateProgress(int count, int goal){

        int c = count;
        int g = goal;
        double percent = ((c*100.0)/(g*100.0))*100.0;
        int newStatus = (int) percent;
        pbStepsStatus = newStatus;

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
                        pbSteps2.setProgress(pbStepsStatus);
                    }
                });
            }
        }).start();

        //END CODE ATTRIBUTION
    }

}