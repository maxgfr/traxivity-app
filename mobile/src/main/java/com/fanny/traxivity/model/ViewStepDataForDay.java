package com.fanny.traxivity.model;

import android.os.AsyncTask;

/**
 * Created by maxime on 14-Jun-17.
 */

public class ViewStepDataForDay extends AsyncTask<Void, Void, Void> {

    private ListenerService hist; // singleton

    protected Void doInBackground(Void... params) {
        HistoryService hist = HistoryService.getInstance();
        hist.displayStepDataForToday();
        return null;
    }
}