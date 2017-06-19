package com.fanny.traxivity.model;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fanny.traxivity.MainActivity;
import com.fanny.traxivity.database.stepsManagerBeta.DbSteps;
import com.fanny.traxivity.database.stepsManagerBeta.StepsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import io.realm.Realm;

/**
 * Listen to the changes in the Data Layer Event, used to send the collected data from the wear to the mobile
 */
public class ListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private final String TAG="ListenerService";

    /**
     * String to define the path for retrieving the DataMapRequest.
     *
     * @see ListenerService#onDataChanged(DataEventBuffer)
     */
    private static final String WEARABLE_DATA_PATH = "/wearable_data";

    /**
     * The app data folder
     */
    private static final String DATA_FOLDER= "/Traxivity/data";

    private GoogleApiClient googleClient;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();
    }

    /**
     * When there is a change in the Data Layer Event, writes the new data in a file and call sendBroadcast to update the visualization in the main activity
     * @param dataEvents
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(WEARABLE_DATA_PATH) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Long timestamp = dataMap.getLong("timestamp");
                    int stepcount = dataMap.getInt("stepcount");

                    Log.d(TAG,"Received data from the wearable");
                    Log.d(TAG,"Timestamp : "+ timestamp.toString());
                    Log.d(TAG,"Stepcount : "+Integer.toString(stepcount));

                    StepsManager managerSteps = new StepsManager();
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(timestamp);
                    Date d = cal.getTime();
                    DbSteps newActivity = new DbSteps(d, stepcount);
                    managerSteps.insertNew(newActivity);
                    this.sendBroadcast(new Intent().setAction("bcNewSteps"));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    /**
     * Check if the external storage is writable
     * @return boolean true if the external storage is writable.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return(Environment.MEDIA_MOUNTED.equals(state));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(googleClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
