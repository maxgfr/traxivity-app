package com.fanny.traxivity;

import java.util.ArrayList;

/**
 * Created by Sadiq on 05/12/2016.
 */

public class Window {

    private long start_time;
    private long end_time;
    private ArrayList<String> data;

    public Window(long start_time, long end_time, ArrayList<String> data){
        this.start_time = start_time;
        this.end_time = end_time;
        this.data = new ArrayList();
        this.data.addAll(data);
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data.addAll(data);
    }

    public int size(){
        return data.size();
    }
}
