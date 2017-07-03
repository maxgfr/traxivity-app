package com.fanny.traxivity.history;

import android.icu.util.Calendar;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by maxime on 7/3/2017.
 */

public class UpdateTask extends AsyncTask<Object, Object, Object> {

    @Override
    protected Calendar doInBackground(Object... params) {
        HistoryService hs = HistoryService.getInstance();
        hs.updateHistory((int)params[0],(java.util.Calendar) params[1], (java.util.Calendar) params[2]);
        return null;
    }
}