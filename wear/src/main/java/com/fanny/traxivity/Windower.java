package com.fanny.traxivity;

import android.hardware.SensorEvent;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;

/**
 * Created by Fanny on 24/06/2016.
 */


/**
 * This class is partitioning the collected data into smaller portions of an amount of seconds
 */
public class Windower {

    private ArrayList<String> window;
    private Window completeWindow;
    private int counter = 0;

    /**
     * List of the windows

     */
    private ArrayList<Window> windowsList;
    /**
     * Current window index
     */
    private int currentWindow;

    /**
     * Starting time of the current window
     */
    private long startingTime;

    /**
     * The window size
     * 10000 = 10sec
     */
    private static final int WINDOW_SIZE = 5000;


    /**
     * Generates the windows list
     */
    public Windower(){
        OpenCVLoader.initDebug();
        windowsList = new ArrayList<>();
        window = new ArrayList();
        //completeWindow = new ArrayList();
    }


    /**
     * Add one element to the currentWindow of windowsList
     * If there is no currentWindow, creates one, and if the size of the currentWindow > 10 sec, creates a new window
     * @see SensorService#onSensorChanged(SensorEvent)
     * @param time event time (currentTimeMillis())
     * @param data sensor data
     * @return int if a new window is created return 1, otherwise return 0
     */
    public int addData(long time, String data){

        if (window.isEmpty()){
            startingTime = time;

        }
        //System.out.println("Data: "+(++counter)+" "+data);

        long timeDiff = time - startingTime;

        if (timeDiff > WINDOW_SIZE){ // if the size of the window > windowSize, creates a new window
            System.out.println("Time diff: " + timeDiff);
            System.out.println("new window");
            completeWindow = new Window(startingTime,time, window);
            //completeWindow.addAll(window);
            window = new ArrayList();
            window.add(data);
            startingTime = time;
            return 1;
        }else{
            window.add(data);
            return 0;
        }

    }

    /**
     * @return the last complete window
     */
    public Window getLastFullWindow() {
        //System.out.println("Current window: "+currentWindow);
        return completeWindow;
    }
}
