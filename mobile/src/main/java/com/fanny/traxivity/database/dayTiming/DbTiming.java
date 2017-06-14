package com.fanny.traxivity.database.dayTiming;

import io.realm.RealmObject;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class DbTiming extends RealmObject {
    private String name;
    private int hour;
    private int minute;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public DbTiming(String name, int hour, int minute){
        this.name = name;
        this.hour = hour;
        this.minute = minute;
    }

    public DbTiming(){}
}
