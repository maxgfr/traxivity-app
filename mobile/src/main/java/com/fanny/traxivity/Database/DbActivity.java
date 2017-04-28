package com.fanny.traxivity.Database;

import java.util.Date;

/**
 * Created by huextrat on 18/04/2017.
 */

public class DbActivity {

    private Date startTime;
    private Date endTime;
    private double duration;
    private ActivityType activity;
    private int nbSteps;

    public int getNbSteps() {
        return nbSteps;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setDuration(double duration){ this.duration = duration; }

    public double getDuration() {
        return duration;
    }

    public ActivityType getActivity() {
        return activity;
    }

    public DbActivity(Date startTime, Date endTime, double duration, ActivityType activity, int nbSteps) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.duration = duration;
        this.activity = activity;
        this.nbSteps = nbSteps;
    }

    public DbActivity(){

    }
}