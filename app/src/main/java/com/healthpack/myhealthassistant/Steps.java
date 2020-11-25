package com.healthpack.myhealthassistant;

import java.util.Date;

public class Steps {

    private String date;
    private int count;

    public Steps(){
        //default constructor
    }

    public Steps(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String toString(){

        return date + ": " + count;
    }
}
