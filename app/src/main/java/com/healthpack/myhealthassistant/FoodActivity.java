package com.healthpack.myhealthassistant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FoodActivity extends AppCompatActivity {

    TextView tvCalorieIntakeValue, tvCalorieGoalValue, tvCalorieDeficitValue;
    ProgressBar pbCalorieIntakeProgress;
    ImageView ivFoodImage;
    Button btnImage, btnSaveFood;
    EditText etFoodDescription, etCalories, etSugar, etProtein, etCarbs;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(mAuth.getCurrentUser().getUid());

    Food food;
    List<Food> todayFoodList;
    List<Food> foodList;

    int pbCalorieStatus;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        tvCalorieIntakeValue = findViewById(R.id.tvCalorieIntakeValue);
        tvCalorieGoalValue = findViewById(R.id.tvCalorieGoalValue);
        tvCalorieDeficitValue = findViewById(R.id.tvCalorieDeficitValue);

        pbCalorieIntakeProgress = findViewById(R.id.pbCalorieIntakeProgress);

        etFoodDescription = findViewById(R.id.etFoodDescription);
        etCalories = findViewById(R.id.etCalories);
        etCarbs = findViewById(R.id.etCarbs);
        etSugar = findViewById(R.id.etSugar);
        etProtein = findViewById(R.id.etProtein);

        ivFoodImage = findViewById(R.id.ivFoodImage);
        btnImage = findViewById(R.id.btnImage);
        btnSaveFood = findViewById(R.id.btnSaveFood);

        /*
        * CODE ATTRIBUTION
        * SOURCE: https://www.youtube.com/watch?v=RaOyw84625w&t=55s&ab_channel=AndroidCoding
        * AUTHOR: Android Coding (YouTube)
        *
        */

        if(ContextCompat.checkSelfPermission(FoodActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(FoodActivity.this, new String[]{
                    Manifest.permission.CAMERA
            },
                    100);
        }
        //END CODE ATTRIBUTION

        btnImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Open Cameras
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        btnSaveFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String descrip, calorie, sugar, protein, carbs;
                descrip = etFoodDescription.getText().toString();
                calorie = etCalories.getText().toString();
                sugar = etSugar.getText().toString();
                protein = etProtein.getText().toString();
                carbs = etCarbs.getText().toString();

                //Check that the user has filled in all the text views
                if(!(descrip.isEmpty() || calorie.isEmpty() || sugar.isEmpty() || protein.isEmpty() || carbs.isEmpty()))
                {
                    Food newFood = new Food();
                    newFood.setDescription(etFoodDescription.getText().toString());
                    newFood.setDate(Singleton.getInstance().getDataManager().getToday());
                    newFood.setCalories(Integer.parseInt(etCalories.getText().toString()));
                    newFood.setSugar(Double.parseDouble(etSugar.getText().toString()));
                    newFood.setProtein(Double.parseDouble(etProtein.getText().toString()));
                    newFood.setCarbs(Double.parseDouble(etCarbs.getText().toString()));

                    Singleton.getInstance().getDataManager().pushFood(newFood);

                    Toast.makeText(FoodActivity.this, "Your meal has been saved", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(FoodActivity.this, "You need to fill in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myRef.child("food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                food = new Food();
                foodList = new ArrayList<Food>();
                todayFoodList = new ArrayList<Food>();

                for(DataSnapshot stepsSnapshot : snapshot.getChildren())
                {
                    food = stepsSnapshot.getValue(Food.class);
                    foodList.add(food);

                    //add the item to today's food list if the date is correct so that a total calorie intake for the day can be calculated
                    if(food.getDate().equals(Singleton.getInstance().getDataManager().getToday())){
                        todayFoodList.add(food);
                    }
                }

                SortByDate();

                if(todayFoodList.size() > 0) {

                    DisplayFoodData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FoodActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * CODE ATTRIBUTION
     * SOURCE: https://www.youtube.com/watch?v=RaOyw84625w&t=55s&ab_channel=AndroidCoding
     * AUTHOR: Android Coding (YouTube)
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if(data != null) {
                //Get Capture Image
                Bitmap captureImage = (Bitmap) data.getExtras().get("data");
                //Set Capture Image to Image View
                ivFoodImage.setImageBitmap(captureImage);
            }
        }
    }
    //END CODE ATTRIBUTION

    //display the current day's calorie intake
    public void DisplayFoodData(){

        Goals goals = Singleton.getInstance().getDataManager().pullGoals();

        //check that the user has set a goal - validation for the Food items was already performed in the onCreate method
        if(goals!= null) {

            int calories = getCalorieIntake();
            int calorieGoal = goals.getCalorieGoal();
            int calorieDeficit = calorieGoal - calories;
            updateProgress(calories, calorieGoal);

            tvCalorieIntakeValue.setText(""+calories);
            tvCalorieGoalValue.setText(""+calorieGoal);
            tvCalorieDeficitValue.setText(""+calorieDeficit + " left to go");
        }
    }

    //calculate the step progress and update the progress bar status
    public void updateProgress(int count, int goal){

        int c = count;
        int g = goal;
        double percent = ((c*100.0)/(g*100.0))*100.0;
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
                        pbCalorieIntakeProgress.setProgress(pbCalorieStatus);
                    }
                });
            }
        }).start();

        //END CODE ATTRIBUTION
    }

    //Bubble sort the Weight objects in the weightList by their date from newest to oldest
    public void SortByDate(){

        //List<Food> sorted = foodList;
        Food outerFood;
        Food innerFood;
        Food tempFood;

        for(int outer = 0; outer < foodList.size()-1; outer++){

            outerFood = foodList.get(outer);

            for(int inner = 1; inner< foodList.size(); inner++){

                innerFood = foodList.get(inner);

                if(FormatDate(innerFood.getDate()).compareTo(FormatDate(outerFood.getDate())) > 0){

                    tempFood = outerFood;
                    foodList.set(outer, innerFood);
                    foodList.set(inner, tempFood);
                }
            }
        }
    }

    //format the date so that it can be compared to (it is put in the format of yyyy-MM-dd)
    public String FormatDate(String date){

        String []split = date.split("-");

        return (split[2] + "-" + split[1] + "-" + split[0]);
    }

    public int getCalorieIntake(){

        int totalCalories = 0;

        for(int pos = 0; pos < todayFoodList.size(); pos++){
            totalCalories += todayFoodList.get(pos).getCalories();
        }

        return totalCalories;
    }

}