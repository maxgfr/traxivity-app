package com.fanny.traxivity.history;

import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.Time;

/**
 * Created by maxime on 6/22/2017.
 */

public class ViewHourPerDay extends AsyncTask<GoogleApiClient, Void, Void> {

    @Override
    protected Void doInBackground(GoogleApiClient... params) {
        HistoryService hs = HistoryService.getInstance();
        hs.displayHourPerDay(params);
        return null;
    }
}