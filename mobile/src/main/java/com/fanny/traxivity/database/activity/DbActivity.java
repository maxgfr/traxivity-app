package com.fanny.traxivity.database.activity;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class DbActivity extends RealmObject {

    private int id;
    private Date startTime;
    private int hoursRange;
    private Date endTime;
    private double duration;
    private String activity;
    private boolean specialActivity = false;
    private int nbSteps;

    public boolean isSpecialActivity() {
        return specialActivity;
    }

    public void setSpecialActivity(boolean specialActivity) {
        this.specialActivity = specialActivity;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getHoursRange() {
        return hoursRange;
    }
    public void setHoursRange(int nbSteps) {
        this.hoursRange = hoursRange;
    }

    public int getNbSteps() {
        return nbSteps;
    }
    public void setNbSteps(int nbSteps) {
        this.nbSteps = nbSteps;
    }

    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public double getDuration() {
        return duration;
    }
    public void setDuration(double duration){ this.duration = duration; }

    public String getActivity() {
        return activity;
    }
    public void setActivity(String activity) {
        this.activity = activity;
    }

    public DbActivity(Date startTime, String activity, int nbSteps) {
        this.id = startTime.getDate();
        this.hoursRange = startTime.getHours();
        this.endTime = startTime;
        this.startTime = startTime;
        this.duration = (endTime.getTime() - startTime.getTime())/1000;
        this.activity = activity;
        this.nbSteps = nbSteps;
    }

    public DbActivity(Date startTime, Date endTime, String activity, float nbSteps) {
        this.id = startTime.getDate();
        this.hoursRange = startTime.getHours();
        this.startTime = startTime;
        this.endTime = endTime;
        this.activity = activity;
        this.nbSteps = (int)nbSteps;
        this.specialActivity = true;
    }

    public DbActivity(){

    }
}