package com.fanny.traxivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.wearable.DataEventBuffer;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(nameReceiver,
                new IntentFilter("newName"));

        LocalBroadcastManager.getInstance(this).registerReceiver(buttonReceiver,
                new IntentFilter("button"));

        LocalBroadcastManager.getInstance(this).registerReceiver(activityReceiver,
                new IntentFilter("activity"));


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAmbientEnabled();


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                TextView mTextView = (TextView) stub.findViewById(R.id.welcome);
                String text;

                if(settings.getString("name", null)==null) {
                    text = "Welcome !";

                }else{
                    text = "Welcome " + settings.getString("name", "") + " !";
                }
                mTextView.setText(text);
            }
        });


        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");

        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

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
            //button.setEnabled(false);
        }




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


            final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
            TextView welcome = (TextView)stub.findViewById(R.id.welcome);
            welcome.setText("Welcome " + settings.getString("name", "") + " !");

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

            final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
            TextView activity = (TextView)stub.findViewById(R.id.activity);

            if (intent.getIntExtra("activity", -1) == RUNNING){
                displayText = "Jogging";
            }else if(intent.getIntExtra("activity", -1) == STAIRS) {
                displayText = "Stairs";
            }else if(intent.getIntExtra("activity", -1) == WALKING) {
                displayText = "Walking";
            } else if(intent.getIntExtra("activity", -1) >= 10){
                if (intent.getIntExtra("activity", -1)== LONG_INACTIVE) {
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

}
