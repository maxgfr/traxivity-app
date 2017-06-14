package com.fanny.traxivity.database.inactivity;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class DbInactivity extends RealmObject {

    private int id;
    private Date startTime;
    private int nbHours;
    private int nbMinutes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getNbHours() {
        return nbHours;
    }
    public void setNbHours(Integer nbHours) {
        this.nbHours = nbHours;
    }

    public int getNbMinutes() {
        return nbMinutes;
    }
    public void setNbMinutes(Integer nbMinutes) {
        this.nbMinutes = nbMinutes;
    }


    public DbInactivity(Date startTime, int nbHours, int nbMinutes) {
        this.id = startTime.getDate();
        this.startTime = startTime;
        this.nbHours = nbHours;
        this.nbMinutes = nbMinutes;
    }

    public DbInactivity(){

    }
}