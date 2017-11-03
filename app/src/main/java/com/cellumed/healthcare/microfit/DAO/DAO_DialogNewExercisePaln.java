package com.cellumed.healthcare.microfit.DAO;

/**
 * Created by ikoob on 2016. 8. 5..
 */
public class DAO_DialogNewExercisePaln {
    private  int exercisePlanNumber;
    private  String exercisePlanName;

    private  int repeatCount =0;

    public DAO_DialogNewExercisePaln(int repeatCount, int exercisePlanNumber) {
        this.repeatCount = repeatCount;
        this.exercisePlanNumber = exercisePlanNumber;
    }

    public int getExercisePlanNumber() {
        return exercisePlanNumber;
    }

    public void setExercisePlanNumber(int exercisePlanNumber) {
        this.exercisePlanNumber = exercisePlanNumber;
    }

    public String getExercisePlanName() {
        return exercisePlanName;
    }

    public void setExercisePlanName(String exercisePlanName) {
        this.exercisePlanName = exercisePlanName;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }
}
