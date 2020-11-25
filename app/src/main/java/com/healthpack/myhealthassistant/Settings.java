package com.healthpack.myhealthassistant;

public class Settings {

    private boolean isMetric;

    public Settings(){
        //Default Constructor
    }

    public boolean isMetric() {
        return isMetric;
    }

    public void setMetric(boolean metric) {
        isMetric = metric;
    }
}
