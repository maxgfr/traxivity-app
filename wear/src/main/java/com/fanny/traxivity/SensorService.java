package com.fanny.traxivity;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.widget.Toast;

import org.opencv.core.Mat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class SensorService extends Service implements SensorEventListener {

    private final static int INTERVAL = 1000 * 60 * 1; //1 minute

    private int sedCount = 0;

    /**
     * The app data folder
     */
    private static final String DATA_FOLDER= "/Traxivity/data";

    /**
     * The running label
     */
    private static final int RUNNING = 0;

    /**
     * The sedentary label
     */
    private static final int SEDENTARY = 1;

    private int lastPrediction = SEDENTARY;

    /**
     * The stairs label
     */
    private static final int STAIRS = 2;

    /**
     * The standing label
     */
    private static final int STANDING = 3;

    /**
     * The walking label
     */
    private static final int WALKING = 4;

    private static final int LONG_INACTIVE = 11;
    private int cpt = 0;


    private Windower windowing = new Windower();

    private FeatureExtractor featureExtraction;
    private Classifier classification;



    /**
     * The ArrayList to use the Csensor.
     *
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()
     * @see SensorService#onCreate()
     * @see MainActivity

     */
    private ArrayList<Csensor> SensorsAL;

    private Sensor sensor;

    private Csensor accelerometer;

    /**
     * The Csensor gyroscope.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()
     * @see Csensor
     */
    private Csensor gyroscope;

    /**
     * The Csensor gyroscope_uncalibrate.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()
     * @see Csensor
     */
    private Csensor gyroscope_uncalibrate;

    /**
     * The Csensor magneticfield.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()
     * @see SensorService#%Magneticfield(SensorEvent)
     * @see Csensor
     * */
    private Csensor magneticfield;

    /**
     * The Csensor magneticfield_uncalibrate.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()
     * @see Csensor
     */
    private Csensor magneticfield_uncalibrate;

    /**
     * The Csensor orientation.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()

     * @see Csensor
     */
    private Csensor orientation;

    /**
     * The Csensor pressure.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()

     * @see Csensor
     */
    private Csensor pressure;

    /**
     * The Csensor geomagnetic.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()

     * @see Csensor
     */
    private Csensor geomagnetic;

    /**
     * The Csensor linear_acceleration.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()

     * @see Csensor
     */
    private Csensor linear_acceleration;

    /**
     * The Csensor rotationvector.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()

     * @see Csensor
     */
    private Csensor rotationvector;

    /**
     * The Csensor stepdetector.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()

     * @see Csensor
     */
    private Csensor stepdetector;

    /**
     * The ArrayList to use the Csensor.
     *
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensors()
     * @see SensorService#unregisterCSensors()
     * @see SensorService#onCreate()
     * @see MainActivity
     * @see SelectSensors
     * @see Csensor
     */





    /**
     * The vector to save the data from the accelerometer.
     *
     * Read the data from the event of the Accelerometer and save these data.
     * The data of the Accelerometer are used to provide the orientation.
     *
     * @see SensorService#getAccelerometerData(SensorEvent)
     */
    float[] accelerometer_vector = new float[3];




    /**
     * The SensorManager to be able to use the sensors.
     *
     * @see SensorService#initSensors()
     */
    private SensorManager sensorManager;



    /**
     * HandlerThread to record the data in another thread.
     *
     * @see SensorService#onCreate()
     * @see SensorService#onDestroy()
     */
    private volatile HandlerThread mHandlerThread;

    /**
     * ServiceHandler to record the data in another thread.
     */
    private ServiceHandler mServiceHandler;

    /**
     * Boolean to determine if the data file has reach te limit of memory.
     */
    private boolean full = false;

    private File recordFile;

    private Handler handler;

    private long sendDate;



    public SensorService() {
        //super("SensorService");
    }


    /**
     * Define how the handler will process messages
     */
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*@Override
    protected void onHandleIntent(Intent intent) {

    }*/


    @Override
    public void onCreate() {
        super.onCreate();



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("MyCustomService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

        handler = new Handler(Looper.getMainLooper());

        String filesDir = getFilesDir().getPath();
        System.out.println("Files Dir: "+filesDir);

        featureExtraction = new FeatureExtractor(filesDir);
        classification = new Classifier(filesDir);

        //classification.trainNewModel();

        SensorsAL = new ArrayList<>();

        sendDate = Calendar.getInstance().getTimeInMillis();

        initRecordFile();

        startMeasurement();

    }


    private void initRecordFile(){
        if (isExternalStorageWritable()) {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String filename = df.format(c.getTime());

            //File myDir = new File(Environment.getExternalStorageDirectory() + DATA_FOLDER);
            File myDir = new File(getFilesDir() + DATA_FOLDER);

            Boolean success = true;

            if (!myDir.exists()) {
                success = myDir.mkdir();
            }

            if (success) {
                //File file = new File(Environment.getExternalStorageDirectory().getPath() + DATA_FOLDER, filename);
                recordFile = new File(myDir, filename);
            } else {
                String text = "Error";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(SensorService.this, text, duration);
                toast.show();
            }

        } else {
            String text = "The external storage is not writable";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(SensorService.this, text, duration);
            toast.show();

        }

    }



    /**
     * The final call received before the service is destroyed.
     * Stop the recording of the data from the sensors by unregistering them.
     * Warn the user if the limit of the memory have been reached.
     * Quit the HandlerThread
     * @see SensorService#stopMeasurement()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        stopMeasurement();

        if (full) {

            Context context = getApplicationContext();
            CharSequence text = "Storage is Full";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }


        mHandlerThread.quit();
    }

    /**
     * Start the recording of the data from the sensors: initialize and register them.
     *
     * @see SensorService#initSensors()
     * @see SensorService#registerCSensor(Sensor sensor)
     */
    private void startMeasurement() {

        if (sensorManager != null) {

            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {

                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                registerCSensor(sensor);
            }
        }

    }

    /**
     * Stop the recording of the data from the sensors: unregister them.
     *
     * @see SensorService#unregisterCSensors()
     */
    private void stopMeasurement() {
        unregisterCSensor(sensor);

    }



    /**
     * Record the accelerometer data for each event with the sensors.
     * Add the data to a window
     * If the window is full, launch the FeatureExtractor and the Classifier, and the save the prediction with a timestamp in the external storage
     * Call the sendBroadcastActivity to send the predicted activity to the mainActivity
     *
     * @param event
     *          The event from a sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {


        int inactiveThreshold = 660;
        String data = "";
        //event.
        long time = System.currentTimeMillis();
        data += Long.toString(time);
        data += getAccelerometerData(event);
        //data += "\n";

        if (windowing.addData(time, data)==1){

            Window window = windowing.getLastFullWindow();
            System.out.println("Window length: "+window.size());
            Mat rep = featureExtraction.extract(window.getData());
            sendBroadcastInfo(featureExtraction.windownb, featureExtraction.catchnb);

            int prediction = classification.predict(rep);
            int activityClass = prediction;

            //Toast
            Context context = getApplicationContext();
            String activity="";
            switch (prediction){
                case RUNNING: activity = "Jogging";
                    break;
                case SEDENTARY: activity = "Inactive";
                    break;
                case STAIRS: activity = "Stairs";
                    break;
                case STANDING: activity = "Standing";
                    break;
                case WALKING: activity = "Walking";
                    break;
                default: activity = ""+prediction;
            }

            //CharSequence text = (activity);
            int duration = Toast.LENGTH_SHORT;
            DisplayToast toast = new DisplayToast(context, activity, duration);

            //if(prediction != SEDENTARY){

            /*** DISPLAYING TOAST ***/

                //handler.post(toast);

            //}

            if (activityClass == SEDENTARY){
                sedCount++;
            }else{
                sedCount = 0;
            }

            if (sedCount >= inactiveThreshold){
                activityClass = 10;
                if ((sedCount % inactiveThreshold)==0){
                    activityClass = 11;
                }
            }

            int steps = 0;
            if (activity == "Walking"){
                System.out.println("Counting steps...");
                steps = StepCounter.countSteps1(window.getData());
                System.out.println("Steps: "+steps);
            }



            //sendBroadcastActivity(RUNNING);
            sendBroadcastActivity(activityClass);

            /*if(cpt<4){
                cpt++;
            }else{
                cpt=0;
            }*/


            String output = Long.toString(window.getStart_time()) + ",";
            output += Long.toString(window.getEnd_time()) + ",";
            output += prediction + ",";
            output += steps + "\n";


            if (recordFile != null) {

                FileWriter filewriter = null;
                try {
                    recordFile.createNewFile();
                    filewriter = new FileWriter(recordFile, true);
                    filewriter.write(output);

                    //sendBroadcastButton(true);



                    if((Calendar.getInstance().getTimeInMillis()-sendDate)>60000) {


                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        long[] vibrationPattern = {0, 500, 50, 300};
                        //-1 - don't repeat
                        final int indexInPatternToRepeat = -1;
                        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

                        startService(new Intent(SensorService.this, SendFileService.class));
                        stopService(new Intent(SensorService.this, SendFileService.class));

                        sendDate=Calendar.getInstance().getTimeInMillis();

                    }




                    /*final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            try{
                                startService(new Intent(SensorService.this, SendFileService.class));
                                stopService(new Intent(SensorService.this, SendFileService.class));
                            }
                            catch (Exception e) {
                                // TODO: handle exception
                            }
                            finally{
                                //also call the same runnable to call it at regular interval
                                handler.postDelayed(this, 1000);
                            }
                        }
                    };

                    runnable.run();*/

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (filewriter != null) {
                        try {
                            filewriter.close();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Initialize the Csensors available
     * Add the Csensors to the list.
     *
     * @see Csensor
     */
    public void initSensors() {

        if (sensorManager != null) {

            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {

                accelerometer = new Csensor("Accelerometer", true, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                SensorsAL.add(accelerometer);
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {

                gyroscope = new Csensor("Gyroscope", false, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
                SensorsAL.add(gyroscope);
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED) != null) {

                gyroscope_uncalibrate = new Csensor("GyroscopeUncalibrated", false, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED));
                SensorsAL.add(gyroscope_uncalibrate);
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {

                magneticfield = new Csensor("Magnetic Field", false, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
                SensorsAL.add(magneticfield);
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) != null) {

                magneticfield_uncalibrate = new Csensor("Magnetic Field Uncalibrate", false, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED));
                SensorsAL.add(magneticfield_uncalibrate);
            }


            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null && sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {

                orientation = new Csensor("Orientation", false, null);
                SensorsAL.add(orientation);
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {

                pressure = new Csensor("Pressure", false, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));
                SensorsAL.add(pressure);
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) != null) {

                geomagnetic = new Csensor("Geomagnetic", false, sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR));
                SensorsAL.add(geomagnetic);
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {

                linear_acceleration = new Csensor("Linear Acceleration", false, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
                SensorsAL.add(linear_acceleration);
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {

                rotationvector = new Csensor("Rotation Vector", false, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
                SensorsAL.add(rotationvector);
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {

                stepdetector = new Csensor("Step Detector", false, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR));
                SensorsAL.add(stepdetector);
            }
        }

    }

    /**
     * Register the Csensors for the recording.
     *
     * Register the sensors to use the onSensorChanged.
     * Enable the sensors to record the data.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     * @see SensorService#startMeasurement()
     */
    public void registerCSensors() {

    }


    public void registerCSensor(Sensor sensor) {

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        //sensorManager.registerListener(this, sensor, 10000, mServiceHandler);

    }

    /**
     * Unregister the Csensors to stop the recording.
     *
     * @see SensorService#stopMeasurement()
     */
    public void unregisterCSensors() {

        for (int i = 0; i < SensorsAL.size(); i++) {
            sensorManager.unregisterListener(this, SensorsAL.get(i).getSensorused());
        }
    }


    public void unregisterCSensor(Sensor sensor) {

        sensorManager.unregisterListener(this, sensor);

    }


    /**
     * Get the data from the Accelerometer and return it as a String
     * @param event
     *          The event that occur when the data from the sensors change.
     * @return  The data saved from the Accelerometer.
     *
     * @see SensorService#onSensorChanged(SensorEvent)
     */
    public String getAccelerometerData(SensorEvent event) {

        String data = "";

        //Getting the data for the accelerometer vector
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometer_vector[0] = event.values[0];
            accelerometer_vector[1] = event.values[1];
            accelerometer_vector[2] = event.values[2];
        }

        data += "," + String.valueOf(accelerometer_vector[0]);
        data += "," + String.valueOf(accelerometer_vector[1]);
        data += "," + String.valueOf(accelerometer_vector[2]);

        return data;
    }

    /**
     * Check if the external storage is writable
     * @return boolean true if the external storage is writable.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Send the intent to the BroadcastReceiver buttonReceiver in the main activity
     * @see MainActivity#buttonReceiver
     */
    public void sendBroadcastButton(boolean bool) {
        Intent intent = new Intent("button");
        if (bool) {
            intent.putExtra("bool", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }else{
            intent.putExtra("bool", false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    /**
     * Send the intent to the BroadcastReceiver activityReceiver in the main activity
     * @see MainActivity#buttonReceiver
     */
    public void sendBroadcastActivity(int activity) {
        Intent intent = new Intent("activity");
        intent.putExtra("activity", activity);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    public void sendBroadcastInfo(int windownb, int catchnb){
        Intent intent = new Intent("info");
        intent.putExtra("windownb", windownb);
        intent.putExtra("catchnb", catchnb);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /*protected void onPause(){
        sensorManager.unregisterListener(this, sensor);
    }


    protected  void onResume(){
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST, mServiceHandler);
    }*/


}


