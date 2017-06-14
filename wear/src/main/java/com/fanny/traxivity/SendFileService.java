package com.fanny.traxivity;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * Need to modify it so that we can send only the stepcount stored in the sharedpreferences with a timestamp added just before sending the data.
 * It means really simplifying this code so that we only send one value and one timestamp.
 */

public class SendFileService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String PREFERENCE_NAME = "PreferenceFile";

    private final String TAG="SendFileService";

    /**
     * String to define the path for sending the DataMapRequest.
     */
    private static final String WEARABLE_DATA_PATH = "/wearable_data";

    /**
     * The app data folder
     */
    private static final String DATA_FOLDER= "/Traxivity/data";
    private GoogleApiClient googleClient;


    public SendFileService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Build a new GoogleApiClient and connects
     */
    public void onCreate() {
        super.onCreate();

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();
    }


    /**
     * Read the file in memory to construct the dataMap
     * Send the dataMap when the data layer connection is successful.
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Node> connectedNodes = Wearable.NodeApi.getConnectedNodes(googleClient).await().getNodes();

                Log.d(TAG,"Listing nodes...");

                for (Node node:connectedNodes) {

                    if(node.isNearby()){

                        Log.d(TAG, "Sending data to " + node.getDisplayName() + "...");

                        sendStepcount();
                    }else{
                        Log.d(TAG, node.getDisplayName() + "isn't nearby, can't send data...");
                    }

                }


            }
        }).start();

    }


    public void sendStepcount(){

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WEARABLE_DATA_PATH);
        putDataMapRequest.getDataMap().putInt("stepcount",PreferenceManager.getDefaultSharedPreferences(this).getInt("stepCount",0));
        putDataMapRequest.getDataMap().putLong("timestamp",System.currentTimeMillis());
        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        request.setUrgent();
        Wearable.DataApi.putDataItem(googleClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if (!dataItemResult.getStatus().isSuccess()) {
                            Log.e(TAG, "buildWatchOnlyNotification(): Failed to set the data, "
                                    + "status: " + dataItemResult.getStatus().getStatusCode());
                        }
                    }
                });
        Log.d(TAG,"Data sent from Wearable");


        stopSelf();

    }


    /**
     * Disconnect from the data layer when the Activity stops
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
    }


    /**
     * Placeholder for required connection callbacks
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) { }

    /**
     * Placeholder for required connection callbacks
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    public void sendBroadcast(boolean bool) {
        Intent intent = new Intent("button");
        if (bool) {
            intent.putExtra("bool", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }else{
            intent.putExtra("bool", false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
}




}