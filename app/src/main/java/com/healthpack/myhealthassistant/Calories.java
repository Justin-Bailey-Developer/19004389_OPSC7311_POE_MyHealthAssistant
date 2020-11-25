package com.healthpack.myhealthassistant;

import java.util.Date;

public class Calories {

    private String date;
    private int calorieCount;

    public Calories(String date, int count) {
        this.date = date;
        this.calorieCount = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return calorieCount;
    }

    public void setCount(int count) {
        this.calorieCount = count;
    }
}
