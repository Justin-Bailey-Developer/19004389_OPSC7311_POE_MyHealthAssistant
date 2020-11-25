package com.healthpack.myhealthassistant;

public class Weight {

    private String date;
    private double weight;
    private ConversionManager conversionManager = new ConversionManager();

    public Weight(){

    }

    public Weight( String date, double weight) {

        this.date = date;
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) { this.weight = weight; }

    public String toString(){
        return date + " - " + weight + "kg";
    }

    public double getWeightInPounds(){
        return conversionManager.KilogramsToPounds(weight);
    }
}
