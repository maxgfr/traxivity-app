package com.fanny.traxivity.view;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fanny.traxivity.MainActivity;
import com.fanny.traxivity.R;
import com.fanny.traxivity.database.dayTiming.DayTimingManager;
import com.fanny.traxivity.database.dayTiming.DbTiming;
import com.fanny.traxivity.dialogs.timePickerDialog;
import com.fanny.traxivity.model.SetAlarm;

import java.util.List;

/**
 * Created by huextrat <www.hugoextrat.com>
 */

public class SettingsActivity extends AppCompatActivity {

    public static int hourStartTime;
    public static int minutesStartTime;

    public static int hourMidTime;
    public static int minutesMidTime;

    public static int hourEndTime;
    public static int minutesEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView tv_start = (TextView)findViewById(R.id.tv_start);
        TextView tv_mid = (TextView)findViewById(R.id.tv_mid);
        TextView tv_end = (TextView)findViewById(R.id.tv_end);

        Button bt_pickTimeStart = (Button)findViewById(R.id.bt_timePickerStart);
        Button bt_pickTimeMid = (Button)findViewById(R.id.bt_timePickerMid);
        Button bt_pickTimeEnd = (Button)findViewById(R.id.bt_timePickerEnd);

        DayTimingManager managerTiming = new DayTimingManager();
        List<DbTiming> listStart = managerTiming.getTimingStart();
        List<DbTiming> listMid = managerTiming.getTimingMid();
        List<DbTiming> listEnd = managerTiming.getTimingEnd();

        if(listStart.size() != 0){
            tv_start.setText(listStart.get(0).getHour()+":"+listStart.get(0).getMinute());
        }

        if(listMid.size() != 0){
            tv_mid.setText(listMid.get(0).getHour()+":"+listMid.get(0).getMinute());
        }

        if(listEnd.size() != 0){
            tv_end.setText(listEnd.get(0).getHour()+":"+listEnd.get(0).getMinute());
        }

        bt_pickTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragmentStart = new timePickerDialog();
                Bundle args = new Bundle();
                args.putString("key", "start");
                newFragmentStart.setArguments(args);
                newFragmentStart.show(getFragmentManager(), "timePickerStart");
            }
        });

        bt_pickTimeMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragmentMid = new timePickerDialog();
                Bundle args = new Bundle();
                args.putString("key", "mid");
                newFragmentMid.setArguments(args);
                newFragmentMid.show(getFragmentManager(), "timePickerShow");
            }
        });

        bt_pickTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragmentEnd = new timePickerDialog();
                Bundle args = new Bundle();
                args.putString("key", "end");
                newFragmentEnd.setArguments(args);
                newFragmentEnd.show(getFragmentManager(), "timePickerEnd");
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
