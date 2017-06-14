package com.fanny.traxivity.admin.view.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.ActivityConversionDAO;
import com.fanny.traxivity.model.ActivityToSteps;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;

/**
 * Created by extra on 30/05/2017.
 */

public class ConversionManager extends AppCompatActivity {

    private Context context;

    private static final String TAG = MessagesManager.class.getSimpleName();

    private TaskCompletionSource<ArrayList<ActivityToSteps>> getConversionTask;
    private Task getConversionTaskWaiter;

    private ArrayAdapter<String> categories_adapter;

    private ArrayList<ActivityToSteps> activityConversionList;
    private ArrayList<String> activityList;

    private String selectedActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion_manager);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = this;

        activityConversionList = new ArrayList<>();
        activityList = new ArrayList<>();
        activityList.add("Loading list...");

        final Spinner sp_activityList = (Spinner)findViewById(R.id.sp_activityList);
        final EditText et_conversion = (EditText)findViewById(R.id.et_conversion);
        Button bt_conversionEdit = (Button)findViewById(R.id.bt_conversionEdit);
        Button bt_addConversion = (Button)findViewById(R.id.bt_startNewConversion);


        //Tasks to get the list of conversions in the database
        getConversionTask = new TaskCompletionSource<>();
        getConversionTaskWaiter = getConversionTask.getTask();

        categories_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityList);
        categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_activityList.setAdapter(categories_adapter);

        //Listener executed once all the categories are pulled
        getConversionTaskWaiter.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    activityList.clear();
                    activityConversionList.addAll((ArrayList<ActivityToSteps>)task.getResult());

                    for(ActivityToSteps ats : activityConversionList){
                        activityList.add(ats.getActivityName());
                    }

                    et_conversion.setText(Float.toString(activityConversionList.get(0).getNumberStepsPerMinute()));

                    categories_adapter.notifyDataSetChanged();
                }
                else{
                    Exception e = task.getException();
                    System.out.println(e.getMessage());
                }
            }
        });

        //Start the async task
        ActivityConversionDAO.getInstance().getConversionList(getConversionTask);

        sp_activityList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(activityConversionList.size() != 0){
                    et_conversion.setText(Float.toString(activityConversionList.get(position).getNumberStepsPerMinute()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bt_addConversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityConversionActivity = new Intent(ConversionManager.this, NewConversion.class);
                startActivityForResult(activityConversionActivity,0);
            }
        });

        bt_conversionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sp_activityList.getSelectedItem().toString().equals("Loading list...")){
                    selectedActivity = sp_activityList.getSelectedItem().toString();
                    ActivityConversionDAO.getInstance().updateConversion(selectedActivity,Float.parseFloat(et_conversion.getText().toString()));
                    activityConversionList.get(sp_activityList.getSelectedItemPosition()).setNumberStepsPerMinute(Float.parseFloat(et_conversion.getText().toString()));
                    Toast.makeText(context, "Conversion edited", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == RESULT_OK){
            getConversionTask = new TaskCompletionSource<>();
            getConversionTaskWaiter = getConversionTask.getTask();

            getConversionTaskWaiter.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        activityList.clear();
                        activityConversionList.clear();
                        activityConversionList.addAll((ArrayList<ActivityToSteps>)task.getResult());

                        for(ActivityToSteps ats : activityConversionList){
                            activityList.add(ats.getActivityName());
                        }

                        categories_adapter.notifyDataSetChanged();
                        Toast.makeText(context, "Conversion added", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Exception e = task.getException();
                        System.out.println(e.getMessage());
                    }
                }
            });

            ActivityConversionDAO.getInstance().getConversionList(getConversionTask);

        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
