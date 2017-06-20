package com.fanny.traxivity.model.historyAPI;

import android.os.AsyncTask;

import com.fanny.traxivity.database.stepsManagerBeta.DbSteps;
import com.fanny.traxivity.database.stepsManagerBeta.StepsManager;
import com.fanny.traxivity.model.ListenerService;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by maxime on 14-Jun-17.
 */

public class ViewStepDataForDay extends AsyncTask<Void, Void, Void> {

    private ListenerService hist; // singleton

    protected Void doInBackground(Void... params) {
        System.out.println("ViewStepDataForDay does in background an action");
        HistoryService hist = HistoryService.getInstance();
        hist.displayStepDataForToday();
        return null;
    }
}