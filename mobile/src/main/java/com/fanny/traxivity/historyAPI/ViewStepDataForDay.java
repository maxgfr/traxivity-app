package com.fanny.traxivity.historyAPI;

import android.os.AsyncTask;

/**
 * Created by maxime on 14-Jun-17.
 */

public class ViewStepDataForDay extends AsyncTask<Void, Void, Void> {

    protected Void doInBackground(Void... params) {
        System.out.println("ViewStepDataForDay does in background an action");
        HistoryService hist = HistoryService.getInstance();
        hist.displayStepDataForToday();
        return null;
    }
}