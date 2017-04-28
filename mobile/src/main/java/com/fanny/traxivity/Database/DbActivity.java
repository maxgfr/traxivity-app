package com.fanny.traxivity.Database;

import java.util.Date;

/**
 * Created by huextrat on 18/04/2017.
 */

public class DbActivity {

    private Date startTime;
    private double duration;
    private String activity;

    public Date getStartTime() {
        return startTime;
    }

    public double getDuration() {
        return duration;
    }

    public String getActivity() {
        return activity;
    }

    public DbActivity(Date startTime, double duration, String activity) {
        this.startTime = startTime;
        this.duration = duration;
        this.activity = activity;
    }

    public DbActivity(){

    }
}