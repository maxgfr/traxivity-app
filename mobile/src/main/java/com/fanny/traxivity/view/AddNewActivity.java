package com.fanny.traxivity.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.app.DialogFragment;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.fanny.traxivity.MainActivity;
import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.ActivityConversionDAO;
import com.fanny.traxivity.database.stepsManagerBeta.DbSteps;
import com.fanny.traxivity.database.stepsManagerBeta.StepsManager;
import com.fanny.traxivity.model.ActivityToSteps;
import com.fanny.traxivity.dialogs.datePickerDialog;
import com.fanny.traxivity.dialogs.timePickerDialog;
import com.fanny.traxivity.database.activity.ActivityManager;
import com.fanny.traxivity.database.activity.DbActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class AddNewActivity extends AppCompatActivity {

    private TaskCompletionSource<ArrayList<ActivityToSteps>> getConversionTask;
    private TaskCompletionSource<Float> getConversionActivityToSteps;
    private Task getConversionActivityToStepsWaiter;
    private Task getConversionTaskWaiter;

    public static Date date;
    public static int hourStartTime;
    public static int minutesStartTime;

    private int nbHours;
    private int nbMinutes;

    private ArrayList<ActivityToSteps> activityConversionList;
    private ArrayList<String> activityList;

    private Date activityDate;
    private int activityHour = 0;
    private int activityMinute = 0;

    private float activityToNbSteps = 0;

    //public TextView tv

    private ArrayAdapter<String> activities_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        activityDate = new Date();

        activityConversionList = new ArrayList<>();
        activityList = new ArrayList<>();
        activityList.add("Loading list...");

        final TextView tv_date = (TextView) findViewById(R.id.tv_date);
        final TextView tv_time = (TextView) findViewById(R.id.tv_time);

        final Spinner sp_activityList = (Spinner)findViewById(R.id.sp_newActivity);
        final NumberPicker np_hours = (NumberPicker)findViewById(R.id.np_hours);
        final NumberPicker np_minutes = (NumberPicker)findViewById(R.id.np_minutes);
        final Button bt_pickDate = (Button)findViewById(R.id.bt_datePicker);
        final Button bt_pickTime = (Button)findViewById(R.id.bt_timePicker);
        Button bt_add = (Button)findViewById(R.id.bt_adNewActivity);


        getConversionActivityToSteps = new TaskCompletionSource<>();
        getConversionActivityToStepsWaiter = getConversionActivityToSteps.getTask();

        getConversionTask = new TaskCompletionSource<>();
        getConversionTaskWaiter = getConversionTask.getTask();

        activities_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityList);
        activities_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_activityList.setAdapter(activities_adapter);

        getConversionTaskWaiter.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    activityList.clear();

                    activityConversionList.addAll((ArrayList<ActivityToSteps>)task.getResult());

                    for(ActivityToSteps ats : activityConversionList){
                        activityList.add(ats.getActivityName());
                    }

                    activities_adapter.notifyDataSetChanged();
                }
                else{
                    Exception e = task.getException();
                    System.out.println(e.getMessage());
                }
            }
        });

        getConversionActivityToStepsWaiter.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    activityToNbSteps = (float)task.getResult();


                    StepsManager managerSteps = new StepsManager();
                    //------------------------------------------------
                    Calendar start = Calendar.getInstance(Locale.getDefault());
                    start.setTime(date);
                    start.set(Calendar.HOUR_OF_DAY, hourStartTime);
                    start.set(Calendar.MINUTE, minutesStartTime);
                    start.set(Calendar.SECOND, 0);
                    //------------------------------------------------
                    Calendar end = Calendar.getInstance(Locale.getDefault());
                    end.setTime(date);
                    end.set(Calendar.HOUR_OF_DAY, hourStartTime+nbHours);
                    end.set(Calendar.MINUTE, minutesStartTime+nbMinutes);
                    end.set(Calendar.SECOND, 0);

                    Log.d("test",activityToNbSteps+"");

                    DbSteps newSteps = new DbSteps(start.getTime(),end.getTime(),(int)activityToNbSteps*((nbHours*60)+(nbMinutes)));
                    managerSteps.insertNew(newSteps);
                }
                else{
                    Exception e = task.getException();
                    System.out.println(e.getMessage());
                }
            }
        });

        ActivityConversionDAO.getInstance().getConversionList(getConversionTask);

        np_hours.setMinValue(0);
        np_hours.setMaxValue(24);

        np_minutes.setMinValue(0);
        np_minutes.setMaxValue(60);

        np_hours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                nbHours = newVal;
            }
        });

        np_minutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                nbMinutes = newVal;
            }
        });

        bt_pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new datePickerDialog();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        bt_pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new timePickerDialog();
                Bundle args = new Bundle();
                args.putString("key", "newActivity");
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityConversionDAO.getInstance().getConversion(getConversionActivityToSteps, sp_activityList.getSelectedItem().toString());

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
