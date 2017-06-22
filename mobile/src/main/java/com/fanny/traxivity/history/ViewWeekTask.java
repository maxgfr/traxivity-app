package com.fanny.traxivity.history;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by maxime on 6/22/2017.
 */

public class ViewWeekTask extends AsyncTask<GoogleApiClient, Void, Void> {

    @Override
    protected Void doInBackground(GoogleApiClient... params) {
        HistoryService hs = HistoryService.getInstance();
        hs.displayLastWeeksData(params);
        return null;
    }
}
