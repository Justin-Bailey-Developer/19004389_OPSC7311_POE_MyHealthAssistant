package com.healthpack.myhealthassistant;

public class Goals {

    private double weightGoal;
    private int calorieGoal, stepGoal;

    public Goals() {
        //Default constructor
    }

    public Goals(double weightGoal, int calorieGoal, int stepGoal) {
        this.weightGoal = weightGoal;
        this.calorieGoal = calorieGoal;
        this.stepGoal = stepGoal;
    }

    public double getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(double weightGoal) {
        this.weightGoal = weightGoal;
    }

    public int getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(int calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }
}
