package com.fanny.traxivity.history;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class StepsManager {

    private int stepTotal;
    private Map<Integer, Integer> stepsByHourOneDay;
    private Map<Integer, Integer> stepPerDayOneWeek;
    private Map<Integer, Integer> stepPerDayOneMonth;

    private static StepsManager INSTANCE = null;

    private StepsManager() {
        stepsByHourOneDay = new HashMap<Integer, Integer>();
        stepPerDayOneWeek = new HashMap<Integer, Integer>();
        stepPerDayOneMonth = new HashMap<Integer, Integer>();
    }

    public static synchronized StepsManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StepsManager();
        }
        return INSTANCE;
    }

    public void setStepTotal(int step) {
        stepTotal = step;
    }

    public int getTotalStepsDay() {
        return stepTotal;
    }

    public Map<Integer,Integer> getStepsByHourOneDay () {
        return stepsByHourOneDay;
    }

    public void setStepsByHourOneDay(Map<Integer,Integer> map) {
        stepsByHourOneDay = map;
    }

    public Map<Integer,Integer> getStepPerDayOneWeek () {
        return stepPerDayOneWeek;
    }

    public void setStepPerDayOneWeek(Map<Integer,Integer> map) {
        stepPerDayOneWeek = map;
    }

    public Map<Integer,Integer> getStepPerDayOneMonth () {
        return stepPerDayOneMonth;
    }

    public void setStepPerDayOneMonth(Map<Integer,Integer> map) {
        stepPerDayOneMonth = map;
    }

}
