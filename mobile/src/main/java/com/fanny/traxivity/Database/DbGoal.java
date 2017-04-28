package com.fanny.traxivity.Database;

import java.util.Date;

/**
 * Created by huextrat on 27/04/2017.
 */

public class DbGoal {

    private Date beginningDate;
    private Date endingDate;
    private int stepsNumber;
    private double duration;

    public DbGoal(Date beginningDate, Date endingDate, int stepsNumber, double duration){
        this.beginningDate = beginningDate;
        this.endingDate = endingDate;
        this.stepsNumber = stepsNumber;
        this.duration = duration;
    }

    public DbGoal(){

    }
}
