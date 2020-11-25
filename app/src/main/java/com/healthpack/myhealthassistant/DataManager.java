package com.healthpack.myhealthassistant;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataManager {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    PersonalInfo personalInfo;
    Goals goals;
    Settings settings;
    Steps steps;
    Steps newSteps;
    Journal journal;
    Food food;
    Weight weight;
    Weight recentWeight;

    List<Steps> stepsList;
    List<Steps> updatedList;

    ArrayAdapter adapter;

    SimpleDateFormat sdf;
    String today;
    String high = "1-1-2020";
    String compare = "";

    public DataManager() {
        //default constructor

        RefreshFirebaseReferences();

        sdf = new SimpleDateFormat("dd-MM-yyyy");
        today = sdf.format(new Date());

        personalInfo = pullPersonalInfo();
        goals = pullGoals();
        settings = pullSettings();
    }

    //Get Firebase authorization and database reference for current user
    public void RefreshFirebaseReferences(){

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(mAuth.getCurrentUser().getUid());
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public String getToday(){ return today; }

    //Push user's personal and physiological info to Firebase
    public void pushPersonalInfo(PersonalInfo persInfo){

        this.personalInfo = persInfo;

        myRef.child("personal info").removeValue(); //remove the user's old settings

        myRef.child("personal info").push().setValue(this.personalInfo); //push the user's new settings

    }

    public PersonalInfo pullPersonalInfo(){

        myRef.child("personal info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot infoValues : snapshot.getChildren()){
                    personalInfo = infoValues.getValue(PersonalInfo.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return personalInfo;
    }

    //Push user's goal data to Firebase
    public void pushGoalsInfo(Goals goals){

        this.goals = goals;

        myRef.child("goals").removeValue(); //remove old goals

        myRef.child("goals").push().setValue(this.goals); //push new goals

    }

    //Pull user's goals from Firebase
    public Goals pullGoals(){

        myRef.child("goals").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot infoValues : snapshot.getChildren()){
                    goals = infoValues.getValue(Goals.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return goals;
    }

    //Push user's settings data to Firebase
    public void pushSettings(Settings settings){

        this.settings = settings;

        myRef.child("settings").removeValue(); //remove the old settings

        myRef.child("settings").push().setValue(this.settings); //add the new settings

        //RefreshFirebaseReferences();
    }

    //Pull user's settings from Firebase
    public Settings pullSettings(){

        myRef.child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot infoValues : snapshot.getChildren()){
                    settings = infoValues.getValue(Settings.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return settings;
    }

    /*//Push user's steps data to Firebase
    public void pushSteps(Steps steps){

        this.steps = steps;

        myRef.child("steps").push().setValue(this.steps);

        RefreshFirebaseReferences();
    }*/

    //Push user's steps data to Firebase
    public void pushSteps(final Steps updateSteps) {

        newSteps = updateSteps;
        System.out.println("test 0");

        //pull the current step data from firebase into a list
        myRef.child("steps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                steps = new Steps();
                stepsList = new ArrayList<Steps>();
                System.out.println("test 1");

                for (DataSnapshot stepsSnapshot : snapshot.getChildren()) {
                    steps = stepsSnapshot.getValue(Steps.class);
                    stepsList.add(steps);
                }

                //UpdateSteps(stepsList, newSteps);
                /*System.out.println("test 2");

                //update the list by replacing the current day's step count with the updated version
                if (stepsList.size() > 0) {

                    SortByDate();
                    stepsList.set(0, newSteps);

                    System.out.println("test 3");

                    for(int pos = 0; pos < stepsList.size(); pos++){
                        System.out.println("test 4");
                        myRef.child("steps").push().setValue(stepsList.get(pos));
                    }
                }
                else
                {
                    System.out.println("test 5");
                    myRef.child("steps").push().setValue(newSteps);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        //System.out.println(stepsList.size() + " vs " + newSteps);
        UpdateSteps(stepsList, newSteps);

        /*System.out.println("test 2");

        //update the list by replacing the current day's step count with the updated version
        if (stepsList.size() > 0) {

            SortByDate();
            stepsList.set(0, newSteps);

            System.out.println("test 3");

            for(int pos = 0; pos < stepsList.size(); pos++){
                System.out.println("test 4");
                myRef.child("steps").push().setValue(stepsList.get(pos));
            }
        }
        else
        {
            System.out.println("test 5");
            myRef.child("steps").push().setValue(newSteps);
        }*/

        //updatedList = stepsList;

       /* //remove the steps node so that it can be replaced with an updated version
        myRef.child("steps").removeValue();

        if(stepsList.size() > 0){

            for(int pos = 0; pos < stepsList.size(); pos++){
                myRef.child("steps").push().setValue(stepsList.get(pos));
            }
        }*/

    }

    public void UpdateSteps(List<Steps> stepsList1, Steps newSteps1){
        updatedList = stepsList1;
        newSteps = newSteps1;

        System.out.println("test 2");

        //update the list by replacing the current day's step count with the updated version
        if (updatedList != null && updatedList.size() > 0) {

            System.out.println("test X");

            SortByDate();
            updatedList.set(0, newSteps);

            System.out.println("test 3");

            myRef.child("steps").removeValue();

            for(int pos = 0; pos < updatedList.size(); pos++){
                System.out.println("test 4");
                myRef.child("steps").push().setValue(updatedList.get(pos));
            }
        }
        else
        {
            myRef.child("steps").removeValue();
            System.out.println("test 5");
            myRef.child("steps").push().setValue(newSteps);
        }
    }

    //Bubble sort the Steps objects in the weightList by their date from newest to oldest
    public void SortByDate(){

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

    //Push user's activity entry to Firebase
    public void pushJournal(Journal journal){

        this.journal = journal;

        myRef.child("journal").push().setValue(this.journal);

        RefreshFirebaseReferences();

    }

    public Journal pullJournal(final String findDate){

        myRef.child("journal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot infoValues : snapshot.getChildren()){
                    if(findDate.equals(infoValues.getValue(Journal.class).getDate())) {
                        journal = infoValues.getValue(Journal.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return journal;
    }

    public void pushFood(Food food){

        this.food = food;
        String date = Singleton.getInstance().getDataManager().getToday();

        myRef.child("food").push().setValue(this.food);

        RefreshFirebaseReferences();
    }

    //Push user's updated weight to Firebase
    public void pushWeight(Weight weight){

        this.weight = weight;

        myRef.child("weight info").push().setValue(this.weight);

    }

   /* public Weight pullWeight(){

        myRef.child("weight info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot infoValues : snapshot.getChildren()){

                    weight = infoValues.getValue(Weight.class);
                    compare = weight.getDate();

                    if(FindMostRecent(high, compare)){ //check if the new weight value is more recent
                        high = weight.getDate();
                        recentWeight = weight;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return recentWeight;
    }

    //compare the dates
    public boolean FindMostRecent(String weight1, String weight2){

        boolean flag = false;

        String []sp1 = weight1.split("-");
        String []sp2 = weight2.split("-");

        //check if the new date is more recent than the first
        if(sp2[2].compareTo(sp1[2]) >= 0 && sp2[1].compareTo(sp1[1]) >= 0 && sp2[0].compareTo(sp1[0]) > 0){
            flag = true;
        }

        return flag;
    }

    //compare the dates
    public boolean FindOldest(String weight1, String weight2){

        boolean flag = false;

        String []sp1 = weight1.split("-");
        String []sp2 = weight2.split("-");

        //check if the new date is more recent than the first
        if(sp2[2].compareTo(sp1[2]) <= 0 && sp2[1].compareTo(sp1[1]) <= 0 && sp2[0].compareTo(sp1[0]) < 0){
            flag = true;
        }

        return flag;
    }*/

}
