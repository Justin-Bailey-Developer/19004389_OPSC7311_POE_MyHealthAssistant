package com.healthpack.myhealthassistant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    CardView cvSteps, cvCalories, cvActivity, cvFood, cvWater, cvWeight;
    ImageView ivHamburger, ivCog;
    TextView tvStepCount, tvStepGoal, tvStepDeficit, tvCalorieIntake, tvCalorieGoal, tvCalorieDeficit, tvStartingWeightValue, tvCurrentWeightValue, tvWeightGoal, tvWeightDeficit;
    ProgressBar pbSteps, pbCalorieIntake, pbWeight;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    PersonalInfo userinfo;
    Goals goals;
    Settings settings;
    Steps steps, currentSteps;
    String today;

    DataManager dataManager = new DataManager();
    ConversionManager conversionManager = new ConversionManager();

    Weight weight;
    Weight startingWeight;
    double startingWeightValue, currentWeightValue, weightGoal, weightDeficit;
    List<Weight> weightList;
    boolean isMetric;

    Food food;
    List<Food> todayFoodList;
    List<Food> foodList;
    int calorieIntake;

    String measurement;

    int pbStepsStatus;
    int pbWeightStatus;
    int pbCalorieStatus;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Card views
        cvSteps = findViewById(R.id.cvSteps);
        cvCalories = findViewById(R.id.cvCalories);
        cvActivity = findViewById(R.id.cvActivity);
        cvFood = findViewById(R.id.cvFood);
        cvWeight = findViewById(R.id.cvWeight);

        //Image views
        ivHamburger = findViewById(R.id.ivHamburger);
        ivCog = findViewById(R.id.ivCog);

        //Text views
        tvStepCount = findViewById(R.id.tvStepCount);
        tvStepGoal = findViewById(R.id.tvStepGoal);
        tvStepDeficit = findViewById(R.id.tvStepDeficit);
        tvCalorieIntake = findViewById(R.id.tvCalorieIntake);
        tvCalorieGoal = findViewById(R.id.tvCalorieGoal);
        tvCalorieDeficit = findViewById(R.id.tvCalorieDeficit);
        tvStartingWeightValue = findViewById(R.id.tvStartingWeightValue);
        tvCurrentWeightValue = findViewById(R.id.tvCurrentWeightValue);
        tvWeightGoal = findViewById(R.id.tvWeightGoal);
        tvWeightDeficit = findViewById(R.id.tvWeightDeficit);

        //Progress bars
        pbSteps = findViewById(R.id.pbSteps);
        pbWeight = findViewById(R.id.pbWeight);
        pbCalorieIntake = findViewById(R.id.pbCalorieIntake);

        //Database reference
        mAuth = FirebaseAuth.getInstance(); //get access to current personalInfo
        myRef = database.getReference(mAuth.getCurrentUser().getUid());

        myRef.child("goals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot goalsSnapshot : snapshot.getChildren()) {
                    goals = goalsSnapshot.getValue(Goals.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        myRef.child("settings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot settingsSnapshot : snapshot.getChildren()) {
                    settings = settingsSnapshot.getValue(Settings.class);
                    DisplayData(); //prefill the text views with the current days data
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        today = Singleton.getInstance().getDataManager().getToday();

        DisplayData(); //prefill the text views with the current days data

        //ON CLICK LISTENERS
        cvSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, StepsActivity.class); //create an intent to move the the next activity
                startActivity(i);
            }
        });

        cvActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, JournalActivity.class); //create an intent to move the the next activity
                startActivity(i);
            }
        });

        cvFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, FoodActivity.class); //create an intent to move the the next activity
                startActivity(i);
            }
        });

        cvWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, WeightActivity.class); //create an intent to move the the next activity
                startActivity(i);
            }
        });

        ivHamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, OptionsActivity.class); //create an intent to move the the next activity
                startActivity(i);
            }
        });

        ivCog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, SettingsActivity.class); //create an intent to move the the next activity
                startActivity(i);
            }
        });

    }

    //Prefill all the fields with the current day's data
    public void DisplayData() {

        today = Singleton.getInstance().getDataManager().getToday();

        //Check that the user has saved their physiological info, goals, and settings
        if (goals != null && settings != null) {

            pullCurrentSteps();
            pullWeight();
            pullFood();
        }
        else
        {
            //Toast.makeText(HomeActivity.this, "You need to enter your personal info, goals, and settings first", Toast.LENGTH_LONG).show();
        }
    }

    //Pull user's settings from Firebase
    public Settings pullSettings() {

        myRef.child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot infoValues : snapshot.getChildren()) {
                    settings = infoValues.getValue(Settings.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return settings;
    }

    public void pullCurrentSteps() {

        myRef.child("steps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                steps = new Steps();

                for (DataSnapshot stepsSnapshot : snapshot.getChildren()) {
                    steps = stepsSnapshot.getValue(Steps.class);

                    //find the current day's steps
                    if (steps.getDate().equals(today)) {
                        DisplaySteps(steps);
                    }
                    else{
                        steps.setCount(0);
                        DisplaySteps(steps);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //display the steps data for today
    public void DisplaySteps(Steps displaySteps) {

        Steps localSteps = displaySteps;
        int count = 0;
        int stepGoal = 0;
        int stepDeficit = 0;
        //Goals goals = Singleton.getInstance().getDataManager().pullGoals();

        if (localSteps != null && goals != null) {

            count = localSteps.getCount();
            stepGoal = goals.getStepGoal();
            stepDeficit = stepGoal - count;
            updateStepsProgress(count, stepGoal);

            tvStepCount.setText("" + count);
            tvStepGoal.setText("" + stepGoal);
            tvStepDeficit.setText("" + stepDeficit + " left to go");
        }
        else
        {
            if (localSteps == null && goals != null)
            {
                count = 0;
                stepGoal = goals.getStepGoal();
                stepDeficit = stepGoal - count;

                tvStepCount.setText("" + count);
                tvStepGoal.setText("" + stepGoal);
                tvStepDeficit.setText("" + stepDeficit + " left to go");
            }
        }

    }

    //calculate the step progress and update the progress bar status
    public void updateStepsProgress(int count, int goal) {

        int c = count;
        int g = goal;
        double test = ((c * 100.0) / (g * 100.0)) * 100.0;
        int newStatus = (int) test;
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
                        pbSteps.setProgress(pbStepsStatus);
                    }
                });
            }
        }).start();

        //END CODE ATTRIBUTION
    }

    //get the user's current weight and update the progress bar status
    public void pullWeight() {

        if (settings.isMetric()) {
            measurement = "kg";
        } else {
            measurement = "lb";
        }

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

                    SortWeightByDate();
                    int currentPos = weightList.size();
                    DisplayWeightData(weightList.get(currentPos - 1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void DisplayWeightData(Weight foundWeight){

        weight = foundWeight;
        String measurement = "";
        int startingPos, currentPos;

        if(weight != null && goals!= null) {

            if (Singleton.getInstance().getDataManager().pullSettings().isMetric()) {

                startingPos = weightList.size() - 1;
                currentPos = 0;

                measurement = "kg";
                startingWeightValue = weightList.get(startingPos).getWeight();
                currentWeightValue = weightList.get(currentPos).getWeight();
                weightGoal = goals.getWeightGoal();
                weightDeficit = (weightGoal - currentWeightValue) * -1.0;
            }
            else {

                measurement = "lb";
                startingWeightValue = foundWeight.getWeight();
                currentWeightValue = weight.getWeight();
                weightGoal = conversionManager.KilogramsToPounds(goals.getWeightGoal());
                weightDeficit = Math.round(((weightGoal - currentWeightValue) * -1.0)*100.0) / 100.0;

            }

            tvStartingWeightValue.setText("" + startingWeightValue + " " + measurement);
            tvCurrentWeightValue.setText("" + currentWeightValue + " " + measurement);
            tvWeightGoal.setText("" + weightGoal + " " + measurement);
            tvWeightDeficit.setText("" + weightDeficit + " " + measurement + " to go");

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

            updateWeightProgress();
        }

    }

    //calculate the step progress and update the progress bar status
    public void updateWeightProgress() {

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
                        pbWeight.setProgress(pbWeightStatus);
                    }
                });
            }
        }).start();

        //END CODE ATTRIBUTION
    }

    //compare the dates
    public double getStartingWeight() {

        double val = 0.0;
        startingWeight = weightList.get(0);

        for (int pos = 1; pos < weightList.size(); pos++) {

            String[] sp1 = weightList.get(pos).getDate().split("-");
            String[] sp2 = startingWeight.getDate().split("-");

            //check if the new date is more recent than the first
            if (sp2[2].compareTo(sp1[2]) >= 0 && sp2[1].compareTo(sp1[1]) >= 0 && sp2[0].compareTo(sp1[0]) > 0) {
                startingWeight = weightList.get(pos);
            }
        }

        return startingWeight.getWeight();
    }

    //Bubble sort the Weight objects in the weightList by their date from newest to oldest
    public void SortWeightByDate(){

        List<Weight> sorted = weightList;
        Weight outerWeight;
        Weight innerWeight;
        Weight tempWeight;


        for(int outer = 0; outer < weightList.size(); outer++){

            for(int inner = outer + 1; inner< weightList.size(); inner++){

                if  (FormatDate(weightList.get(inner).getDate()).compareTo(FormatDate(weightList.get(outer).getDate())) > 0){

                    tempWeight = weightList.get(outer);
                    weightList.set(outer, weightList.get(inner));
                    weightList.set(inner, tempWeight);

                }
            }
        }
    }

    public String FormatDate(String date) {

        String[] split = date.split("-");

        return (split[2] + "-" + split[1] + "-" + split[0]);
    }

    public void pullFood() {

        myRef.child("food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                food = new Food();
                foodList = new ArrayList<Food>();
                todayFoodList = new ArrayList<Food>();

                for (DataSnapshot stepsSnapshot : snapshot.getChildren()) {
                    food = stepsSnapshot.getValue(Food.class);
                    foodList.add(food);

                    //add the item to today's food list if the date is correct so that a total calorie intake for the day can be calculated
                    if (food.getDate().equals(Singleton.getInstance().getDataManager().getToday())) {
                        todayFoodList.add(food);
                    }
                }

                if (todayFoodList.size() > 0) {

                    DisplayFood();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void DisplayFood() {

        int calories = getCalorieIntake();
        int calorieGoal = goals.getCalorieGoal();
        int calorieDeficit = calorieGoal - calories;

        //check that the user has set a goal - validation for the Food items was already performed in the onCreate method
        if (goals != null) {

            if(foodList.size() > 0) {
                calories = getCalorieIntake();
                calorieGoal = goals.getCalorieGoal();
                calorieDeficit = calorieGoal - calories;
                updateCalorieIntake(calories, calorieGoal);

                tvCalorieIntake.setText("" + calories);
                tvCalorieGoal.setText("" + calorieGoal);
                tvCalorieDeficit.setText("" + calorieDeficit + " left to go");
            }
            else
            {
                calories = 0;
                calorieGoal = goals.getCalorieGoal();
                calorieDeficit = calorieGoal - calories;
                //updateCalorieIntake(calories, calorieGoal);

                tvCalorieIntake.setText("" + calories);
                tvCalorieGoal.setText("" + calorieGoal);
                tvCalorieDeficit.setText("" + calorieDeficit + " left to go");
            }
        }
    }

    public int getCalorieIntake() {

        int totalCalories = 0;

        for (int pos = 0; pos < todayFoodList.size(); pos++) {
            totalCalories += todayFoodList.get(pos).getCalories();
        }

        return totalCalories;
    }

    //calculate the step progress and update the progress bar status
    public void updateCalorieIntake(int count, int goal) {

        int c = count;
        int g = goal;
        double percent = ((c * 100.0) / (g * 100.0)) * 100.0;
        int newStatus = (int) percent;
        pbCalorieStatus = newStatus;

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
                        pbCalorieIntake.setProgress(pbCalorieStatus);
                    }
                });
            }
        }).start();

        //END CODE ATTRIBUTION
    }

}