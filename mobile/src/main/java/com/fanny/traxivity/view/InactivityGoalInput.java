package com.fanny.traxivity.view;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.InformationDAO;
import com.fanny.traxivity.admin.model.Information;
import com.fanny.traxivity.database.activity.ActivityManager;
import com.fanny.traxivity.dialogs.textDialogInactivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by jbjourget.
 */


public class InactivityGoalInput extends AppCompatActivity {


    private static final int DEFAULT_MAX_MIN = 59;
    private static final int DEFAULT_MIN_MIN = 0;
    private static final int DEFAULT_MAX_HOURS = 8;
    private static final int DEFAULT_MIN_HOURS = 0;
    public static Date dateDeb;
    public static Integer nbHours;
    public static Integer nbMin;
    NumberPicker nbPickerMin;
    NumberPicker nbPickerHours;
    Button confirmButton;
    private TaskCompletionSource<ArrayList<Information>> getInformationTask;
    private Task getInformationTaskWaiter;
    protected ArrayList<Information> informationList;
    TextView tvTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inactivity_goal);
        dateDeb = new Date();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        confirmButton = (Button) findViewById(R.id.appCompatButtonInactivity);
        nbPickerMin = (NumberPicker) findViewById(R.id.numberPickerMin);
        nbPickerHours = (NumberPicker) findViewById(R.id.numberPickerHours);
        nbPickerMin.setMinValue(DEFAULT_MIN_MIN);
        nbPickerMin.setMaxValue(DEFAULT_MAX_MIN);
        nbPickerHours.setMinValue(DEFAULT_MIN_HOURS);
        nbPickerHours.setMaxValue(DEFAULT_MAX_HOURS);
        tvTitle = (TextView) findViewById(R.id.textView6);

        getInformationTask = new TaskCompletionSource<>();
        getInformationTaskWaiter = getInformationTask.getTask();
        informationList = new ArrayList<>();

        getInformationTaskWaiter.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {

                    Log.d("task", "success");
                    informationList.clear();
                    informationList.addAll((ArrayList<Information>) task.getResult());
                    Random rand = new Random();
                    tvTitle.setText(informationList.get(rand.nextInt(informationList.size())).getContent());
                }else{
                    Exception e = task.getException();
                    System.out.println(e.getMessage());
                }
            }
        });

        InformationDAO.getInstance().getInformation("Inactivity", getInformationTask);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nbHours = nbPickerHours.getValue();
                nbMin = nbPickerMin.getValue();
                android.app.FragmentManager fm = getFragmentManager();
                DialogFragment newFragment = new textDialogInactivity();
                newFragment.show(fm, "ActivityAccept");
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        ActivityManager managerActivity = new ActivityManager();
        managerActivity.removeLastActivity();
        finish();
        return true;

    }

    private void SetDividerColor(NumberPicker picker)
    {

    }
}