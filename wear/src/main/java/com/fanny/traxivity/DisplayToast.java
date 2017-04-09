package com.fanny.traxivity;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Sadiq on 01/03/2017.
 */

public class DisplayToast implements Runnable {

    String text;
    Context context;
    int duration;

    public DisplayToast(Context context, String text, int duration){
        this.text = text;
        this.context = context;
        this.duration = duration;
    }

    public void run(){
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }
}
