package com.fanny.traxivity.database.goal;

import com.google.android.gms.fitness.data.Goal;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class DbGoal extends RealmObject {

    private int id;
    private String type;
    private Date beginningDate;
    private Date endingDate;
    private int stepsNumber;
    private double duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(Date beginningDate) {
        this.beginningDate = beginningDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public int getStepsNumber() {
        return stepsNumber;
    }

    public void setStepsNumber(int stepsNumber) {
        this.stepsNumber = stepsNumber;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public DbGoal(GoalType type, Date beginningDate, Date endingDate, int stepsNumber, double duration) {
        this.id = beginningDate.getDate();
        this.type = type.toString();
        this.beginningDate = beginningDate;
        this.endingDate = endingDate;
        this.stepsNumber = stepsNumber;
        this.duration = duration;
    }


    public DbGoal() {

    }

    public static DbGoal withSteps(Date beginningDate, Date endingDate, int stepsNumber){
        return new DbGoal(GoalType.Steps,beginningDate,endingDate,stepsNumber,0);
    }

    public static DbGoal withDuration(Date beginningDate, Date endingDate, double duration){
        return new DbGoal(GoalType.Duration,beginningDate,endingDate,0,duration);
    }

}