package com.fanny.traxivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.wearable.DataEventBuffer;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends WearableActivity {
    /**
     * The app main folder
     */
    private static final String TRAXIVITY_FOLDER= "/Traxivity";

    /**
     * The app data folder
     */
    private static final String MODELS_FOLDER= "/Traxivity/models";

    /**
     * The running label
     */
    private static final int RUNNING = 0;

    private static final int SEDENTARY = 1;

    /**
     * The stairs label
     */
    private static final int STAIRS = 2;
    /**
     * The walking label
     */
    private static final int WALKING = 4;

    private static final int LONG_INACTIVE = 11;

    private String displayText = "";

    private String[] messages = {"Time to stretch your legs!", "Let's get some fresh air!", "Let's take a walk!", "Some fresh air now would be a good idea!", "How about a stroll?"};


    private TextView catchtv;
    private TextView windowtv;
    private TextView welcomeTv;
    private TextView activityTv;
    private TextView mClockView;
    private RelativeLayout rLayout;
    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);


    /**
     * The SharedPreferences used to save the user name
     */
    private SharedPreferences settings;

    private GoogleApiClient mApiClient;




    /**
     * Called when the activity is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     * Set the sharedPreferences, register the BroadcastReceivers nameReceiver and buttonReceiver, set the contentView
     * If the name is not defined in the sharedPreferences: displays a "Welcome" message, otherwise displays the "welcome" message with the saved name
     * Launch OpenCV
     * Launch the creation of the folder needed by the app
     * Launch the copy of the files needed by the app from the raw folder to the external storage directory
     * If the copy was successful launch the SensorService, otherwise disable the "share" button
     * @see MainActivity#nameReceiver
     * @see MainActivity#buttonReceiver
     * @see MainActivity#createFolder(String)
     * @see MainActivity#rawToFile(String, InputStream)
     * @see SensorService
     * @param savedInstanceState
     */

    /*** @see onUpdateAmbient and @onCreate ***/

    private AlarmManager mAmbientStateAlarmManager;
    private PendingIntent mAmbientStatePendingIntent;

    private static final long AMBIENT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tests using PowerManager

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = powerManager.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "MyWakelockTag");
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        Handler mWakeLockHandler = new Handler();

        mWakeLockHandler.removeCallbacksAndMessages(null);


        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setAmbientEnabled();

        mAmbientStateAlarmManager =
                (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent ambientStateIntent =
                new Intent(getApplicationContext(), MainActivity.class);

        mAmbientStatePendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                ambientStateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        


        settings = PreferenceManager.getDefaultSharedPreferences(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(nameReceiver,
                new IntentFilter("newName"));

        LocalBroadcastManager.getInstance(this).registerReceiver(buttonReceiver,
                new IntentFilter("button"));

        LocalBroadcastManager.getInstance(this).registerReceiver(activityReceiver,
                new IntentFilter("activity"));
        LocalBroadcastManager.getInstance(this).registerReceiver(infoReceiver,
                new IntentFilter("info"));




        setContentView(R.layout.activity_main);





        catchtv = (TextView) findViewById(R.id.catchtv);
        windowtv = (TextView) findViewById(R.id.window);
        rLayout = (RelativeLayout) findViewById(R.id.container);
        welcomeTv = (TextView) findViewById(R.id.welcome);
        activityTv = (TextView) findViewById(R.id.activity);
        mClockView = (TextView) findViewById(R.id.clock);
        String text;

        if(settings.getString("name", null)==null) {
            text = "Welcome !";

        }else{
            text = "Welcome " + settings.getString("name", "") + " !";
        }
        welcomeTv.setText(text);



        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");

        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        //Mat im = new Mat(5,5, CvType.CV_32FC1);

        //createFolder(TRAXIVITY_FOLDER);
        //createFolder(MODELS_FOLDER);

        //copy model files from 'raw' application directory to device file system
        if (rawToFile("model", getResources().openRawResource(R.raw.model)) && rawToFile("meansigma", getResources().openRawResource(R.raw.meansigma))){
            System.out.println("Raw files found....");
            startService(new Intent(MainActivity.this, SensorService.class));
            //System.out.println("Starting ActivityRecogniserService");
            //startService(new Intent(MainActivity.this, ActivityRecogniserService.class));
        }else{
            Button button = (Button)findViewById(R.id.share);
            //startService(new Intent(MainActivity.this, SensorService.class));
            //button.setEnabled(false);
        }

        startService(new Intent(MainActivity.this, SensorService.class));


    }

    /**
     * The final call received before the activity is destroyed.
     * Launch the SendFileService to send the data to the mobile
     * Stop the SendFileService and the SensorService so the app stop recording data from the sensor
     * @see SendFileService
     * @see SensorService
     */
    @Override
    protected void onDestroy() {
        mAmbientStateAlarmManager.cancel(mAmbientStatePendingIntent);
        super.onDestroy();

        startService(new Intent(MainActivity.this, SendFileService.class));
        stopService(new Intent(MainActivity.this, SendFileService.class));
        //stopService(new Intent(MainActivity.this, ActivityRecogniserService.class));

    }

    /**
     * The method behind the "share" button, launch the SendFileService to send data from the wear to the mobile
     * @see SendFileService
     * @param view
     */
    public void share(View view){

        startService(new Intent(MainActivity.this, SendFileService.class));
        stopService(new Intent(MainActivity.this, SendFileService.class));

    }

    /*** RECEIVER/INTENT RELATIVE ***/

    /**
     * Receive intents sent by sendBroadcast() in the ListenerService
     * When an intent is received, it means that a new name has been received from the mobile and added to the shared preferences
     * Change the "welcome" message to display the new name
     * @see ListenerService
     * @see ListenerService#onDataChanged(DataEventBuffer)
     * @see ListenerService#sendBroadcast()
     */
    private BroadcastReceiver nameReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            /*final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
            TextView welcome = (TextView)stub.findViewById(R.id.welcome);*/
            TextView welcome = (TextView)findViewById(R.id.welcome);
            welcome.setText("Welcome " + settings.getString("name", "") + " !");

            }
    };

    private BroadcastReceiver infoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            windowtv.setText(Integer.toString(intent.getIntExtra("windownb", -1)));
            catchtv.setText(Integer.toString(intent.getIntExtra("catchtv", -1)));

        }
    };

    /**
     * Receive intents sent by sendBroadcastActivity() in the ListenerService
     * When an intent is received, it means that an activity has been predicted
     * Change the "activity" message to display the current activity
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#sendBroadcastActivity(int)
     */
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            /*final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
            TextView activity = (TextView)stub.findViewById(R.id.activity);*/
            TextView activity = (TextView)findViewById(R.id.activity);

            if (intent.getIntExtra("activity", -1) == RUNNING){
                displayText = "Jogging";
            }else if(intent.getIntExtra("activity", -1) == STAIRS) {
                displayText = "Stairs";
            }else if(intent.getIntExtra("activity", -1) == WALKING) {
                displayText = "Walking";
            } else if(intent.getIntExtra("activity", -1) >= 10){
                if (intent.getIntExtra("activity", -1)== SEDENTARY) {
                    Random gen = new Random();
                    int i = gen.nextInt(messages.length);
                    displayText = messages[i];
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    long[] pattern = {0, 500, 50, 300};
                    vibrator.vibrate(pattern, -1);
                }
            }else{
                displayText = "";
            }

            activity.setText(displayText);
        }
    };

    /**
     * Receive intents sent by sendBroadcastButton() in the SensorService
     * When an intent is received, it means that there is some data to share (or not)
     * Enable (or disable) the button "share"
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#sendBroadcastButton(boolean)
     */
    private BroadcastReceiver buttonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Button button = (Button)findViewById(R.id.share);

            if (intent.getBooleanExtra("bool", true)){
                button.setEnabled(true);
            }else{
                button.setEnabled(false);
            }
        }
    };

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

    /**
     * Copy the content of the raw file in the external storage, in order to be able to load the model
     * @param nameFile name of the file to create
     * @param raw name of the file to copy
     */
    public boolean rawToFile(String nameFile, InputStream raw) {
        //File dir = new File(Environment.getExternalStorageDirectory(),MODELS_FOLDER);
        File dir = new File(getFilesDir(), MODELS_FOLDER);
        File file = new File(dir, nameFile + ".csv");
        try {
            if (!dir.exists()) {
                System.out.println("Creating dir: " + dir.getPath());
                if (dir.mkdirs()) {
                    System.out.println(dir.getPath() + " created successfully");
                } else {
                    System.out.println("Error creating dir: " + dir.getPath());
                }
            }
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("Created file " + file.getPath() + " successfully!");
                } else {
                    System.out.println("Error while creating file " + file.getPath());
                }
                if (isExternalStorageWritable()) {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        byte[] buffer = new byte[4 * 1024];
                        int read;

                        while ((read = raw.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }
                        output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally{
                        output.close();
                        raw.close();
                    }
                } else {
                    CharSequence text = "The external storage is not writable";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(this, text, duration);
                    toast.show();
                    return false;
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Check if the external storage is writable
     * @return boolean true if the external storage is writable.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void onPause() {

        super.onPause();
    }

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
        }
    }

    /*** AMBIENT RELATIVE ***/

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            rLayout.setBackgroundColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.VISIBLE);
            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            rLayout.setBackgroundColor(Color.argb(255,0,146,166));
            mClockView.setVisibility(View.GONE);
        }
    }



    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        setIntent(intent);

        //refreshDisplayAndSetNextUpdate();

    }



}
