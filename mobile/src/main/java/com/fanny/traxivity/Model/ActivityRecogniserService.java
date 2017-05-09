package com.fanny.traxivity.Model;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.fanny.traxivity.Database.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Date;

/**
 * Created by Sadiq on 01/03/2017.
 */

public class ActivityRecogniserService extends IntentService{

    private GoogleApiClient mApiClient;
    private Handler handler;
    private long lastDateTime = new Date().getTime();
    private com.fanny.traxivity.Database.ActivityManager manager = new com.fanny.traxivity.Database.ActivityManager();

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
            Date d = new Date();
            DbActivity myActivity = new DbActivity(d,d.getTime()-lastDateTime,strActivity);
            manager.insertActivity(myActivity);
            lastDateTime = d.getTime();
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
