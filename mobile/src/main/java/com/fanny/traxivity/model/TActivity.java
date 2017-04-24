package com.fanny.traxivity.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by huextrat on 18/04/2017.
 */

public class TActivity extends RealmObject {

    private Date startTime;

    private double duration;

    private String activity;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity.toString();
    }

    public TActivity(Date startTime, double duration, String activity) {
        this.startTime = startTime;
        this.duration = duration;
        this.activity = activity;
    }

    public TActivity(){

    }
}
