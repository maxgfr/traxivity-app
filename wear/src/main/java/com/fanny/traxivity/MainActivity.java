package com.fanny.traxivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends WearableActivity implements SensorEventListener{

    private static final String PREFERENCE_NAME = "PreferenceFile";


    /**
     * Used for debugging purpose (Log)
     */

    private final String TAG="MainActivity";
    /**
     * The app main folder
     */
    private static final String TRAXIVITY_FOLDER= "/Traxivity";

    /**
     * The app data folder
     */
    private static final String MODELS_FOLDER= "/Traxivity/models";


    private static final int RUNNING = 0;
    private static final int STAIRS = 2;
    private static final int WALKING = 4;
    private static final int LONG_INACTIVE = 11;

    private String displayText = "";

    /**
     * List of motivational messages
     */

    private String[] messages = {"Time to stretch your legs!", "Let's get some fresh air!", "Let's take a walk!", "Some fresh air now would be a good idea!", "How about a stroll?"};


    /**
     * The SharedPreferences used to save the user name
     */
    private SharedPreferences settings;
    private SensorManager mSensorManager;
    private Sensor mStepCounter;
    private TextView stepsDisplay;
    public int gStepCount = 0;

    private BroadcastReceiver mBatInfoReciever;
    private int battlevel;
    private TextView mBatteryView;
    private long lastUpdate;
    private int stepDiff;
    private long sendDate;

    private Date lastDate = new Date();


    /**
     * Called when the activity is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     * Set the sharedPreferences, register the BroadcastReceivers nameReceiver and buttonReceiver, set the contentView
     * If the name is not defined in the sharedPreferences: displays a "Welcome" message, otherwise displays the "welcome" message with the saved name
     * Launch OpenCV
     * Launch the creation of the folder needed by the app
     * Launch the copy of the files needed by the app from the raw folder to the external storage directory
     * If the copy was successful launch the SensorService, otherwise disable the "share" button
     * @see MainActivity#createFolder(String)
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAmbientEnabled();

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        startService(new Intent(this, ActivityRecognitionService.class));
        sendDate = Calendar.getInstance().getTimeInMillis();


        //Initializing values from the SharedPreferences


        final int stepCount = settings.getInt("stepCount", 0);
        stepDiff = settings.getInt("stepDiff", 0);
        lastUpdate = settings.getLong("lastUpdateTime", System.currentTimeMillis());

        final Intent batteryIntent = this.registerReceiver(mBatInfoReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //Initializing everything linked to the visual aspect (layout, Textview)

        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mBatteryView = (TextView) stub.findViewById(R.id.battView);
                battlevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                mBatteryView.setText(battlevel+"%");
                stepsDisplay = (TextView) stub.findViewById(R.id.activity);
                stepsDisplay.setText("Steps: "+stepCount);
              }
        });

        //Initializing the sensor used to get the StepCount

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (mStepCounter != null){
            mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_UI);
        }

        mBatInfoReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                battlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                mBatteryView.setText(battlevel+"%");
            }
        };

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);


        /*SharedPreferences.Editor editor = settings.edit();
        editor.putInt("stepCount", 1234);
        editor.commit();

        startService(new Intent(MainActivity.this,SendFileService.class));*/


    }

    /**
     * Called when the application is closed.
     * Writes the parameters into the SharedPreferences
     * @see MainActivity#onDestroy()
     * @param stepCount
     * @param stepDiff
     * @param updateTime
     */

    private void saveStepCount(int stepCount, int stepDiff, long updateTime){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("stepCount", stepCount);
        editor.putInt("stepDiff", stepDiff);
        editor.putLong("lastUpdateTime", updateTime);
        editor.commit();
    }

    /**
     * The final call received before the activity is destroyed.
     * Launch the SendFileService to send the data to the mobile
     * Stop the SendFileService and the SensorService so the app stop recording data from the sensor
     * @see SendFileService
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        saveStepCount(gStepCount, stepDiff, lastUpdate);

    }


    /**
     * Check if the folder exists, if not create it
     * @param nameFolder the name of the folder we want to create
     */
    public void createFolder(String nameFolder) {

        File myDir = new File(Environment.getExternalStorageDirectory() + nameFolder);
        if (!myDir.exists()) {
            myDir.mkdir();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int stepCount = 0;
        stepCount += sensorEvent.values[0];
        System.out.println("Step count before: "+stepCount);

        long currentTime = System.currentTimeMillis();

        Date currentDate = new Date();
        currentDate.setTime(currentTime);

        System.out.println("Last update: "+ lastDate.toString());
        System.out.println("Current Time: "+ currentDate.toString());

        //System.out.println("Last update: "+lastUpdate/(1000 * 60 * 60 * 24l));
        //System.out.println("Current Time: "+currentTime/(1000 * 60 * 60 * 24l));
        //if (lastUpdate/(1000 * 60 * 60 * 24l) < currentTime/(1000 * 60 * 60 * 24l)){
        if (lastDate.getDate() != currentDate.getDate()){
            stepDiff = stepCount;
            saveStepCount(0, stepDiff, currentTime);

            lastDate = currentDate;
            lastUpdate = currentTime;
        }


        gStepCount = stepCount-stepDiff;
        System.out.println("Step count after: "+gStepCount);
        if (stepsDisplay != null) {
            stepsDisplay.setText("Steps: " + gStepCount);
        }

        saveStepCount(gStepCount, stepDiff, currentTime);

        //Sending data each minute

        //if((Calendar.getInstance().getTimeInMillis()-sendDate)>PreferenceManager.getDefaultSharedPreferences(this).getLong("sendTime",900000)) {

        Log.d(TAG,"Calling SendFileService");
        startService(new Intent(MainActivity.this, SendFileService.class));

        sendDate=Calendar.getInstance().getTimeInMillis();

        //}


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
