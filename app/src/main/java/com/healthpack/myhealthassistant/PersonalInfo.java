package com.healthpack.myhealthassistant;

import java.util.Date;

public class PersonalInfo {

    private String name;
    private String birthday;
    private boolean isMale; /*, isMetric;*/
    private double height;

    public PersonalInfo(){
        //Default Constructor
    }

    public PersonalInfo(String name, String birthday, boolean isMale, /*boolean isMetric,*/ double height) {

        this.name = name;
        this.birthday = birthday;
        this.isMale = isMale;
        /*this.isMetric = isMetric;*/
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    /*public boolean isMetric() {
        return isMetric;
    }

    public void setMetric(boolean metric) {
        isMetric = metric;
    }*/

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
