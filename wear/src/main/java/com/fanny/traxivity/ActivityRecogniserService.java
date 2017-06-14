package com.fanny.traxivity;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;


/**
 * Created by Sadiq on 01/03/2017.
 */

public class ActivityRecogniserService extends IntentService {

    private final String TAG="RecogniserService";


    private Handler handler;

    public ActivityRecogniserService(){
        super("ActivityRecogniserService");
    }

    public void onCreate(){
        super.onCreate();

        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        if (ActivityRecognitionResult.hasResult(intent)){
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity activity = result.getMostProbableActivity();
            String strActivity = getActivityName(activity.getType());
            Log.d(TAG,"The activity is : "+ strActivity);
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
            case DetectedActivity.ON_FOOT: return "Walking";
            default: return "Unknown";
        }
    }



}
