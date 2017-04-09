package com.fanny.traxivity;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Sadiq on 02/03/2017.
 */

public class ActivityRecognitionService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mApiClient;

    public void onCreate(){
        super.onCreate();

        mApiClient = new GoogleApiClient.Builder(this).
                addApi(com.google.android.gms.location.ActivityRecognition.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                build();
        mApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("ActivityRecogniserService connected");
        Intent intent = new Intent(this, ActivityRecogniserService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        com.google.android.gms.location.ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 1000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
