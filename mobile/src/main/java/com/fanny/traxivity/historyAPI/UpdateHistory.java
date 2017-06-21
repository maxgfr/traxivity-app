package com.fanny.traxivity.historyAPI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by maxime on 6/19/2017.
 */

public class UpdateHistory extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("UpdateHistory is active");
        new ViewStepDataForDay().execute();
    }

}