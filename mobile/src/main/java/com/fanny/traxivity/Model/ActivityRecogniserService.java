package com.fanny.traxivity.Model;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fanny.traxivity.Database.*;
import com.fanny.traxivity.Database.ActivityManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Date;

import static com.fanny.traxivity.Model.SaveLastActivity.lastActivity;

/**
 * Created by Sadiq on 01/03/2017.
 */

public class ActivityRecogniserService extends IntentService{

    private Handler handler;
    private ActivityManager manager = new ActivityManager();

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
            if(!strActivity.equals("Unknown")) {
                Date d = new Date();

                if(!strActivity.equals(lastActivity.getActivity().toString()) || strActivity.equals(ActivityType.Null.toString())) {
                    DbActivity myActivity = new DbActivity(d, d, (d.getTime() - lastActivity.getStartTime().getTime())/1000, ActivityType.valueOf(strActivity), 0);
                    manager.insertActivity(myActivity);
                    lastActivity = myActivity;
                }
                else {
                    DbActivity myActivity = new DbActivity(lastActivity.getStartTime(), d, (d.getTime() - lastActivity.getStartTime().getTime())/1000, ActivityType.valueOf(strActivity), 0);
                    manager.addDurationLast(myActivity);
                }
            }
            //--------------------------------------------------------------------------

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            DisplayToast toast = new DisplayToast(context, strActivity, duration);
            handler.post(toast);

            manager.getDay("04-05-2017");
        }

    }

    private String getActivityName(int activity){
        switch(activity){
            case DetectedActivity.IN_VEHICLE: return "InVehicle";
            case DetectedActivity.ON_BICYCLE: return "Cycling";
            case DetectedActivity.RUNNING: return "Running";
            case DetectedActivity.WALKING: return "Walking";
            case DetectedActivity.STILL: return "Inactive";
            case DetectedActivity.ON_FOOT: return "OnFoot";
            default: return "Unknown";
        }
    }


}
