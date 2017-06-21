package com.fanny.traxivity.historyAPI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fanny.traxivity.MainActivity;
import com.fanny.traxivity.historyAPI.steps.StepsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

/**
 * Created by maxime on 14-Jun-17.
 */

public class HistoryService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //History
    public GoogleApiClient mClient = null;

    /** Instance unique non préinitialisée */
    private static HistoryService INSTANCE = null;

    /** Constructeur privé */
    private HistoryService() {
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized HistoryService getInstance() {
        if (INSTANCE == null)
        { 	INSTANCE = new HistoryService();
        }
        return INSTANCE;
    }

    public boolean isClientNull () {
        if (mClient == null) {
            return true;
        }
        return false;
    }

    //Build a client
    public void buildFitnessClientHistory(MainActivity main) {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(main)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(main)
                .addOnConnectionFailedListener(main)
                .build();

        mClient.connect();
    }

    //View today's steps
    public void displayStepDataForToday() {
        if (mClient == null) {
            System.out.println("Client NULL");
        }
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( mClient, DataType.TYPE_STEP_COUNT_DELTA ).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());
    }

    private void showDataSet(DataSet dataSet) {
        System.out.println("SHOW DATA SET");
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            System.out.println("DataPoint");
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                //Log.e("History", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                System.out.println("UPDATE STEP");
                int stepcount = dp.getValue(field).asInt();
                //StepsManager managerSteps = new StepsManager();
                //managerSteps.setStepTotal(stepcount);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryService", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryService", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryService", "onConnectionFailed");
    }

}