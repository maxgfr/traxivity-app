package com.fanny.traxivity.historyAPI.steps;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by maxime on 6/21/2017.
 */

public class DbSteps extends RealmObject {

    private int id;
    private Date startTime;
    private Date endTime;
    private int hoursRange;
    private int nbSteps;
    private boolean isSpecial = false;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
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

    public DbSteps(Date startTime, int nbSteps) {
        this.id = startTime.getDate();
        this.hoursRange = startTime.getHours();
        this.startTime = startTime;
        this.nbSteps = nbSteps;
    }

    public DbSteps(Date startTime, Date endTime, int nbSteps) {
        this.id = startTime.getDate();
        this.hoursRange = startTime.getHours();
        this.startTime = startTime;
        this.endTime = endTime;
        this.nbSteps = nbSteps;
        this.isSpecial = true;
    }

    public DbSteps(){

    }
}