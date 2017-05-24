package com.fanny.traxivity;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * Send the name from the mobile to the wear
 */
public class SendFileService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleClient;

    /**
     * The SharedPreferences used to save the user name
     */
    private SharedPreferences settings;

    public SendFileService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Build a new GoogleApiClient and connect
     */
    public void onCreate() {
        super.onCreate();

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();
    }




    /**
     * Send a data object when the data layer connection is successful.
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        System.out.println("on connected SendFileService");


        String name = settings.getString("name", "");


        new SendToDataLayerThread("/message_path", name, googleClient).start();


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
}

/**
 * Create a new thread to send the file, so the UI thread is not blocked
 */
class SendToDataLayerThread extends Thread {
    String path;
    String message;
    GoogleApiClient googleClient;

    /**
     * Constructor to send a message to the data layer
     * @param p path
     * @param msg message
     * @param googleClient google api client
     */
    SendToDataLayerThread(String p, String msg, GoogleApiClient googleClient) {
        path = p;
        message = msg;
        this.googleClient = googleClient;
    }

    /**
     * Send the message to the data layer
     */
    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
            if (result.getStatus().isSuccess()) {
                Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
            }
            else {
                Log.e("myTag", "ERROR: failed to send Message");
            }
        }
    }
}