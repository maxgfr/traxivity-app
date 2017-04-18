package com.fanny.traxivity.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by huextrat on 18/04/2017.
 */

public class Day extends RealmObject {

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

    public Activity getActivity() {
        return Activity.valueOf(activity);
    }

    public void setActivity(Activity activity) {
        this.activity = activity.toString();
    }

    public Day(Date startTime, double duration, String activity) {
        this.startTime = startTime;
        this.duration = duration;
        this.activity = activity;
    }

    public Day(){

    }
}
