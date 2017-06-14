package com.fanny.traxivity.model;

/**
 * Created by almabire.
 */

public class ActivityToSteps {
    private String activityName;

    private float numberStepsPerMinute;

    public ActivityToSteps(String activityName, float numberStepsPerMinute) {
        this.activityName = activityName;
        this.numberStepsPerMinute = numberStepsPerMinute;
    }

    public ActivityToSteps() {
    }

    public float getSteps(int minuteNumber){
        return numberStepsPerMinute * minuteNumber;
    }

    public String getActivityName() {
        return activityName;
    }

    public float getNumberStepsPerMinute() {
        return numberStepsPerMinute;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public void setNumberStepsPerMinute(float numberStepsPerMinute) {
        this.numberStepsPerMinute = numberStepsPerMinute;
    }
}

