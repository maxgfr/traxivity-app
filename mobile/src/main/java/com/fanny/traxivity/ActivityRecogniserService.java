package com.fanny.traxivity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fanny.traxivity.model.ActivityManager;
import com.fanny.traxivity.model.EActivity;
import com.fanny.traxivity.model.TActivity;
import com.fanny.traxivity.realm.RealmController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Sadiq on 01/03/2017.
 */

public class ActivityRecogniserService extends IntentService{

    private GoogleApiClient mApiClient;
    private Handler handler;
    private long lastDateTime = new Date().getTime();

    public ActivityRecogniserService(){
        super("ActivityRecogniserService");
    }

    public void onCreate(){
        super.onCreate();

        handler = new Handler(Looper.getMainLooper());

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Handling Intent");
        if (ActivityRecognitionResult.hasResult(intent)){
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity activity = result.getMostProbableActivity();
            String strActivity = getActivityName(activity.getType());

            System.out.println("Activity: "+strActivity);

            //--------------------------------------------------------------------------
            ActivityManager manager = new ActivityManager();
            Realm realm = Realm.getDefaultInstance();
            Date d = new Date();
            manager.insertActivity(realm,d,d.getTime()-lastDateTime,strActivity);
            lastDateTime = d.getTime();
            Log.d("Content",manager.selectAll(realm).toString());
            //--------------------------------------------------------------------------

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            DisplayToast toast = new DisplayToast(context, strActivity, duration);
            handler.post(toast);
        }

    }

    private String getActivityName(int activity){
        switch(activity){
            case DetectedActivity.IN_VEHICLE: return "In Vehicle";
            case DetectedActivity.ON_BICYCLE: return "Cycling";
            case DetectedActivity.RUNNING: return "Running";
            case DetectedActivity.WALKING: return "Walking";
            case DetectedActivity.STILL: return "Inactive";
            case DetectedActivity.ON_FOOT: return "On Foot";
            default: return "Unknown";
        }
    }


}
