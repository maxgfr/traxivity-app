package com.fanny.traxivity.Database;

import java.util.Date;

/**
 * Created by huextrat on 27/04/2017.
 */

public class DbGoal {

    private GoalType type;
    private Date beginningDate;

    public GoalType getType() {
        return type;
    }

    public Date getBeginningDate() {
        return beginningDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public int getStepsNumber() {
        return stepsNumber;
    }

    public double getDuration() {
        return duration;
    }

    private Date endingDate;
    private int stepsNumber;
    private double duration;

    public DbGoal(GoalType type, Date beginningDate, Date endingDate, int stepsNumber, double duration){
        this.type = type;
        this.beginningDate = beginningDate;
        this.endingDate = endingDate;
        this.stepsNumber = stepsNumber;
        this.duration = duration;
    }

    public DbGoal(){

    }
}
