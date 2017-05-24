package com.fanny.traxivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.wearable.DataEventBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    /**
     * The app main folder
     */
    private static final String TRAXIVITY_FOLDER= "/Traxivity";

    /**
     * The app data folder
     */
    private static final String DATA_FOLDER= "/Traxivity/data";


    /**
     * The files older than AGE_FILE will be deleted
     */
    private static final int AGE_FILE = -7;


    /*
    Activity Integer Labels:
    RUNNING = 0
    INACTIVE = 1
    STAIRS = 2
    STANDING = 3
    WALKING = 4
     */


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

    /**
     * The date when the app was open
     */
    private static final Calendar TODAY =  Calendar.getInstance();


    /**
     * The currently visualized date
     */
    private Calendar visualizedDate = Calendar.getInstance();


    /**
     * The SharedPreferences used to save the user name
     */
    private SharedPreferences settings;


    private List<List> activityDay;

    /**
     * 2D Array containing the amount of each activity for each hour done during the currently visualized date
     * dailyActivity[hour][activity]
     * Not used anymore
     * @see MainActivity#dataExtraction(Calendar)
     * @see MainActivity#setChart()
     */
        int dailyActivity[][];

    /**
     * Amount of activity done during the currently visualized date
     * @see MainActivity#dataExtraction(Calendar)
     * @see MainActivity#setActivitySummary()
     *
     */
    int totalActivity;

    /**
     * Amount of running done during the currently visualized date
     * @see MainActivity#dataExtraction(Calendar)
     * @see MainActivity#setActivitySummary()
     */
    int totalRunning;

    /**
     * Amount of stairs done during the currently visualized date
     * @see MainActivity#dataExtraction(Calendar)
     * @see MainActivity#setActivitySummary()
     */
    int totalStairs;

    /**
     * Amount of walking done during the currently visualized date
     * @see MainActivity#dataExtraction(Calendar)
     * @see MainActivity#setActivitySummary()
     */
    int totalWalking;


    private int totalSteps;

    /**
     * Called when the activity is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     * Set the sharedPreferences, register the BroadcastReceiver dataReceiver, set the contentView, disable the button "after"(=forward)
     * If the name is not defined in the sharedPreferences: launch the showDialog, otherwise displays the "welcome" message with the saved name
     * Launch the creation of the folder needed by the app
     * Delete the old files
     * launch the visualization for TODAY
     * @see MainActivity#dataReceiver
     * @see MainActivity#showDialog()
     * @see MainActivity#createFolder(String)
     * @see MainActivity#deleteOldFiles()
     * @see MainActivity#visualization(Calendar)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,
                new IntentFilter("newData"));


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button = (Button)findViewById(R.id.after);
        button.setEnabled(false);

        if(settings.getString("name", null)==null){
            showDialog();
        }else{
            TextView t=(TextView)findViewById(R.id.welcome);
            String text = "Hello " + settings.getString("name", "") + " !";
            t.setText(text);
        }

        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.});

        createFolder(TRAXIVITY_FOLDER);
        createFolder(DATA_FOLDER);

        deleteOldFiles();

        visualization(TODAY);

        startService(new Intent(this, ActivityRecognitionService.class));

    }


    /**
     * The method behind the "settings" button
     * @see MainActivity#showDialog()
     * @param view current View
     */
    public void changeName(View view){
        showDialog();
    }



    /**
     * The method behind the "back" button.
     * Set the visualizedDate one day before and visualized it
     * Enable the "forward"/"after" button
     * @see MainActivity#visualization(Calendar)
     * @param view current View
     */
    public void before(View view){
        visualizedDate.roll(Calendar.DATE, false);
        visualization(visualizedDate);
        Button button = (Button)findViewById(R.id.after);
        button.setEnabled(true);
    }

    /**
     * The method behind the "forward" button.
     * If the visualizedDate is not today, set the visualizedDate one day after and visualized it
     * @see MainActivity#visualization(Calendar)
     * @param view current View
     */
    public void after(View view){
        if(!dateFormat(visualizedDate).equals(dateFormat(TODAY))){
            visualizedDate.roll(Calendar.DATE, true);
            visualization(visualizedDate);
        }

    }


    /**
     * Check if the folder exists, if not create it
     * @param nameFolder the name of the folder we want to create
     */
    public void createFolder(String nameFolder) {

        //File myDir = new File(Environment.getExternalStorageDirectory() + nameFolder);
        File myDir = new File(getFilesDir() + nameFolder);

        if (!myDir.exists()) {
            myDir.mkdir();
        }
    }

    /**
     * Check all the files in the dataFolder and delete those older than AGE_FILE
     * @see MainActivity#tooOld(Calendar)
     */

    public void deleteOldFiles(){
        File folder = new File(getFilesDir() + DATA_FOLDER);

        if (folder.isDirectory()) {
            for (File f : folder.listFiles()) {

                Calendar timeLastModification = Calendar.getInstance();
                timeLastModification.setTimeInMillis(f.lastModified());
                if (tooOld(timeLastModification)){

                    f.delete();
                }
            }
        }
    }

    /**
     * Check if the date is older than AGE_FILE days
     * @param day tested date
     * @return boolean true if the date is older than AGE_FILE days
     */
    public boolean tooOld(Calendar day){
        Calendar time = Calendar.getInstance();
        time.add(Calendar.DAY_OF_YEAR, AGE_FILE);
        return (day.before(time));
    }


    /**
     * Set up the visualization for the day:
     * "enable" or "disable" the back and forward button if needed
     * Launch the dateExtraction, setChart and setActivitySummary
     * Display the visualizedDate
     * @see MainActivity#dataExtraction(Calendar)
     * @see MainActivity#setChart()
     * @see MainActivity#setActivitySummary()
     * @param day visualized date
     */
    public void visualization(Calendar day) {

        System.out.println("Visualising date: "+day.getTime());

        visualizedDate.setTime(day.getTime());

        //"disable" forward button if date being visualised in today
        if(dateFormat(visualizedDate).equals(dateFormat(TODAY))){
            Button button = (Button)findViewById(R.id.after);
            button.setEnabled(false);
        }

        //"disable" back button if date being visualised is older than 7 days ago
        if(tooOld(day)) {
            Button button = (Button)findViewById(R.id.before);
            button.setEnabled(false);
        }else{
            Button button = (Button)findViewById(R.id.before);
            button.setEnabled(true);
        }

        //Launch the dateExtraction, setChart and setActivitySummary
        dataExtraction(day);
        setChart();
        System.out.println("Total activity: "+totalActivity);
        System.out.println("Total walking: "+totalWalking);
        setActivitySummary();



        //Displays the visualizedDate in a TextView
        SimpleDateFormat df = new SimpleDateFormat("EEEE dd MMMM ");
        String date = df.format(visualizedDate.getTime());
        TextView t=(TextView)findViewById(R.id.day);
        t.setText(date);

    }


    /**
     * Check if there is a file for the day, if so read the file to extract the data in dailyActivity, totalActivity, totalRunning, totalStairs and totalWalking
     * @param day extracted date
    */
    public void dataExtraction(Calendar day){

        int hour;
        int activity;
        int steps;
        long timestamp;

        dailyActivity = new int[24][5];
        totalActivity = 0;
        totalRunning = 0;
        totalStairs = 0;
        totalWalking = 0;
        totalSteps = 0;

        String date = dateFormat(day);

        File file = new File(getFilesDir() + DATA_FOLDER + "/" + date +".csv");
        System.out.println("Reading file: "+file.getPath());

        if (file.exists()) {
            BufferedReader br = null;
            String line;
            String cvsSplitBy = ",";

            Long last_timestamp = -1l;
            try {
                br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] splitLine = line.split(cvsSplitBy);

                    if (splitLine.length != 4){
                        System.out.println("wrong format");
                    }else {
                        timestamp = Long.parseLong(splitLine[0]);
                        Calendar time = Calendar.getInstance();
                        time.setTimeInMillis(timestamp);
                        //System.out.println("Date: "+date);
                        //System.out.println("Timestamp: "+dateFormat(time));


                        if (date.equals(dateFormat(time))){

                            hour=getHour(timestamp);
                            activity = Integer.parseInt(splitLine[2]);

                            if (isActive(activity)) {

                                long increment = 0;
                                if (last_timestamp >= 0) {
                                    increment = (timestamp - last_timestamp) / 1000;
                                }

                                System.out.println("increment: "+increment);

                                dailyActivity[hour][activity] += increment;
                                totalActivity += increment;

                                if (activity == RUNNING) {
                                    totalRunning += increment;
                                }
                                if (activity == STAIRS) {
                                    totalStairs += increment;
                                }
                                if (activity == WALKING) {
                                    totalWalking += increment;
                                }

                                steps = Integer.parseInt(splitLine[3]);
                                totalSteps += steps;
                            }

                            last_timestamp = timestamp;

                        }else{
                            System.out.println("wrong day: " + date);
                        }
                    }


                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }else{
            System.out.println("no data for today " + date);
        }
    }


    public boolean isActive(int activity){
        if (activity == RUNNING || activity == WALKING || activity == STAIRS){
            return true;
        }else{
            return false;
        }

    }



/*
    public void dataExtraction(Calendar day){
        int hour;
        int activity;
        long timestamp;

        totalActivity = 0;
        totalRunning = 0;
        totalStairs = 0;
        totalWalking = 0;

        activityDay = new ArrayList<>();
        List<List> labelDay = new ArrayList<>();

        for (int i = 0; i < 24 ; i++){
            activityDay.add(new ArrayList<>());
            labelDay.add(new ArrayList<>());
        }

        String date = dateFormat(day);

        File file = new File(Environment.getExternalStorageDirectory() + DATA_FOLDER + "/" + date +".csv");

        if (file.exists()) {
            BufferedReader br = null;
            String line;
            String cvsSplitBy = ",";

            try {
                br = new BufferedReader(new FileReader(file));

                int lastActivity = -1; // 0 = inactive, 1 = active
                int val = 0;
                int currentHour = -1;

                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] splitLine = line.split(cvsSplitBy);

                    if (splitLine.length != 2){
                        System.out.println("wrong format");
                    }else {
                        timestamp = Long.parseLong(splitLine[0]);
                        Calendar time = Calendar.getInstance();
                        time.setTimeInMillis(timestamp);
                        if (date.equals(dateFormat(time))) {

                            hour = getHour(timestamp);
                            activity = Integer.parseInt(splitLine[1]);
                            int activeOrInactive = getActiveOrInactive(activity);

                            if (currentHour == -1) {
                                currentHour = hour;
                            }

                            if (hour == currentHour) {
                                if (lastActivity == -1) {
                                    lastActivity = activeOrInactive;
                                    val++;
                                } else {
                                    if (activeOrInactive == lastActivity) {
                                        val++;
                                    } else {
                                        activityDay.get(hour).add((float)val);
                                        labelDay.get(hour).add(lastActivity);
                                        lastActivity = activeOrInactive;
                                        val = 1;
                                    }
                                }
                            } else {
                                activityDay.get(currentHour).add((float) val);
                                labelDay.get(currentHour).add(lastActivity);

                                if((activityDay.get(currentHour).size()%2) != 0){
                                    activityDay.get(currentHour).add((float) 1);
                                    labelDay.get(currentHour).add(lastActivity);
                                }

                                currentHour = hour;
                               //if the first activity of the hour is active, adds 10 sec of inactive at the beginning of the hour
                               //in order to have the colours in the right orders and the labels right

                                if (activeOrInactive != 0){
                                    activityDay.get(currentHour).add((float) 1);
                                    labelDay.get(currentHour).add(0);
                                }

                                lastActivity = activeOrInactive;
                                val = 1;
                            }

                            if (activity == RUNNING){
                                totalRunning++;
                                totalActivity++;
                            }
                            if (activity == STAIRS){
                                totalStairs++;
                                totalActivity++;
                            }
                            if (activity == WALKING){
                                totalWalking++;
                                totalActivity++;
                            }

                        }else{
                            System.out.println("wrong day" + date);
                        }
                    }


                }
                activityDay.get(currentHour).add((float) val);
                labelDay.get(currentHour).add(lastActivity);

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }else{
            System.out.println("no data for today " + date);
        }

    }
*/
  /*
    public void setChart(){

        BarChart chart = (BarChart) findViewById(R.id.chart);
        List<BarEntry> entries = new ArrayList<>();
        System.out.println("new");
        for(int i = 0 ; i < 24 ; i++) {

            float[] activityList = new float[activityDay.get(i).size()];
            //String[] labelList = new String[activityDay.get(i).size()];
            System.out.println("taille: " + activityDay.get(i).size());
            for (int j = 0 ; j < activityDay.get(i).size() ; j++){
                activityList[j] = (float) activityDay.get(i).get(j) /6;
                System.out.println(activityList[j]);
            }
            entries.add(new BarEntry(i, activityList));
        }
        System.out.println("test");
        //Set the data
        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setDrawValues(false); //hide the value above bars
        dataSet.setColors(getColors());
        dataSet.setStackLabels(new String[]{"Inactive", "Active"});

        BarData barData = new BarData(dataSet);



        //Legend
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

        //Left YAxis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinValue(0f);
        leftAxis.setAxisMaxValue(60f);
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false);

        //Right YAxis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        //XAxis
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);

        //MarkerView: display a customized (popup) View whenever a value is highlighted in the chart
        //CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);

        //Chart
        //chart.setMarkerView(mv);
        chart.setDescription("");
        chart.setData(barData);
        chart.animateXY(2000, 2000);
        chart.setTouchEnabled(false);
        //chart.setPinchZoom(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.invalidate(); // refresh
    }
*/
    /**
     *
     * @param activity activity number
     * @return 0 if inactive, 1 if active
     */
    private int getActiveOrInactive(int activity){

        if (activity == 1){
            return 0;
        }else{
            return 1;
        }
    }


    /**
     * Get colors for the dataSet
     * @return colors: an array with the colors
     */
    private int[] getColors() {

        int stacksize = 3;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = ColorTemplate.MATERIAL_COLORS[i];
        }

        return colors;
    }

    /**
     * Create the chart with the dailyActivity data and display it
     * @see MainActivity#getColors()
     */

    public void setChart(){
        BarChart chart = (BarChart) findViewById(R.id.chart);
        List<BarEntry> entries = new ArrayList<>();

        for(int i = 0 ; i < 24 ; i++) {
            float minutesWalking = (float)dailyActivity[i][WALKING] / 60;
            float minutesStairs = (float)dailyActivity[i][STAIRS] / 60;
            float minutesRunning = (float)dailyActivity[i][RUNNING] / 60;

            entries.add(new BarEntry(i, new float[]{minutesWalking, minutesStairs, minutesRunning}));
        }

        //Set the data
        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setDrawValues(false); //hide the value above bars
        dataSet.setColors(getColors());
        dataSet.setStackLabels(new String[]{"Walking", "Stairs", "Running"});

        BarData barData = new BarData(dataSet);




        //Legend
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

        //Left YAxis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinValue(0f);
        leftAxis.setAxisMaxValue(60f);
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false);

        //Right YAxis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        //XAxis
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);

        //MarkerView: display a customized (popup) View whenever a value is highlighted in the chart
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);

        //Chart
        chart.setMarkerView(mv);
        chart.setDescription("");
        chart.setData(barData);
        chart.animateXY(2000, 2000);
        chart.setPinchZoom(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.invalidate(); // refresh
    }


    /**
     * Format the timestamp to keep only the hour
     * @param timestamp the timestamp to format
     * @return the hour of the timestamp
     */
    public int getHour(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            return Integer.parseInt(sdf.format(calendar.getTime()));
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }


    /**
     * Display the totalActivity, totalRunning, totalStairs and totalWalking
     * @see MainActivity#timeToText(int)
     */
    public void setActivitySummary(){

        TextView nbActivity = (TextView) findViewById(R.id.nbActivity);
        nbActivity.setText(timeToText(totalActivity));

        TextView nbWalking = (TextView) findViewById(R.id.nbWalking);
        nbWalking.setText(timeToText(totalWalking));

        TextView nbStairs = (TextView) findViewById(R.id.nbStairs);
        nbStairs.setText(timeToText(totalStairs));

        TextView nbRunning = (TextView) findViewById(R.id.nbRunning);
        nbRunning.setText(timeToText(totalRunning));

        TextView nbSteps = (TextView) findViewById(R.id.nbSteps);
        nbSteps.setText(String.valueOf(totalSteps));


    }

    /**
     * transform the tens seconds in a more visualizable String with the hour, min and sec
     * @param time the time to visualize
     * @return String text the transformed time
     */
    public String timeToText(int time){

        //int h = (time / 360);
        //int min = (time % 360) / 6;
        //int sec = (time % 360) % 6;
        int h = time/3600;
        int r = time - (h * 3600);
        int min = r/60;
        r = r - (min * 60);
        int sec = r;

        String text;

        if (h != 0){
            text = Integer.toString(min) +" h " + Integer.toString(min) +" min " +  Integer.toString(sec) + " sec";
        }else if (min != 0){
            text = Integer.toString(min) +" min " +  Integer.toString(sec) + " sec";
        }else if (sec != 0){
            text = Integer.toString(sec) + " sec";
        }else{
            text = "0 sec";
        }
        return text;

    }

    /**
     * format a Calendar in a String with the format "dd-MMM-yyyy"
     * @param c calendar to format
     * @return String
     */

    public String dateFormat(Calendar c){

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c.getTime());
    }

    /**
     * Display an alertDialog that asks the name of the user and saves it in the sharedPreferences
     * Send the new name to the wear through the sendFileService
     * When the name is changed, change the textView "welcome" withe the new name
     * @see SendFileService
     */
    public void showDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Hello");
        alert.setMessage("What's your name ?");
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input);

        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                editor.putString("name", input.getText().toString());
                editor.apply();

                dialog.dismiss();

                startService(new Intent(MainActivity.this, SendFileService.class));

                stopService(new Intent(MainActivity.this, SendFileService.class));

                TextView t = (TextView) findViewById(R.id.welcome);
                String text = "Hello " + settings.getString("name", "") + " !";
                t.setText(text);
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();

    }

    /**
     * Receive intents sent by sendBroadcast() in the ListenerService
     * When an intent is received, it means that some data has been received from the wear.
     * Launch the visualization for TODAY to display the new data
     * @see MainActivity#visualization(Calendar)
     * @see ListenerService
     * @see ListenerService#onDataChanged(DataEventBuffer)
     * @see ListenerService#sendBroadcast()
     */
    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            visualization(TODAY);
        }
    };

}
