package com.fanny.traxivity.View;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanny.traxivity.R;

/**
 * Created by jbjourget on 02/05/2017.
 */



public class GoalInputActivity extends AppCompatActivity {

    RelativeLayout relative1;
    EditText stepsEtext;
    EditText hoursEtext;
    EditText minEtext;
    RadioButton rdbSteps;
    RadioButton rdbTime;
    Button confirm;
    Editable stepsEditable;
    Editable minEditable;
    Editable hoursEditable;
    Button dayButton;
    Button weekButton;
    TextView hintsText;
    public static Boolean isWeek = false;
    public static Integer nbSteps = null;
    public static Integer nbMin = null;
    public static Integer nbHours = null;
    public static final int NB_STEPS_FOR_DAY= 10000;
    public static final int NB_HOUR_FOR_DAY = 1;
    public static final int NB_MIN_FOR_DAY = 20;
    public static final int NB_STEPS_FOR_WEEK= NB_STEPS_FOR_DAY*7;
    public static final int NB_HOUR_FOR_WEEK = NB_HOUR_FOR_DAY*7+NB_MIN_FOR_DAY*7/60;
    public static final int NB_MIN_FOR_WEEK = NB_MIN_FOR_DAY*7%60;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_input);



        relative1 = (RelativeLayout) findViewById(R.id.relativeLayout);
        stepsEtext = (EditText) findViewById(R.id.editText);
        hoursEtext = (EditText) findViewById(R.id.editText5);
        minEtext = (EditText) findViewById(R.id.editText4);
        rdbSteps = (RadioButton) findViewById(R.id.radioButton5);
        rdbTime = (RadioButton) findViewById(R.id.radioButton4);
        confirm = (Button) findViewById(R.id.appCompatButton3);
        dayButton = (Button) findViewById(R.id.appCompatButton);
        weekButton = (Button) findViewById(R.id.appCompatButton2);
        hintsText = (TextView) findViewById(R.id.textView3);
        hintsText.setText("The average number of steps for a day is "+NB_STEPS_FOR_DAY);

        rdbSteps.setChecked(true);
        relative1.setVisibility(View.INVISIBLE);

        dayButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                isWeek = false;
                dayButton.setEnabled(false);
                dayButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
                weekButton.setEnabled(true);
                weekButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                if(rdbSteps.isChecked()){
                    hintsText.setText("The average number of steps for a day is "+NB_STEPS_FOR_DAY);
                }else{
                    hintsText.setText("The average duration of training for a day is "+NB_HOUR_FOR_DAY +" h "+NB_MIN_FOR_DAY+" min");
                }
            }
        });

        weekButton.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                isWeek = true;
                weekButton.setEnabled(false);
                weekButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
                dayButton.setEnabled(true);
                dayButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                if(rdbSteps.isChecked()){
                    hintsText.setText("The average number of steps for a week is "+NB_STEPS_FOR_WEEK);
                }else{
                    hintsText.setText("The average duration of training for a week is "+NB_HOUR_FOR_WEEK +" h "+NB_MIN_FOR_WEEK+" min");
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonConfirmClick(v);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onStepsTicked(View view){
        stepsEtext.setVisibility(View.VISIBLE);
        rdbTime.setChecked(false);
        relative1.setVisibility(View.INVISIBLE);
        if(isWeek){
            hintsText.setText("The average number of steps for a week is "+NB_STEPS_FOR_WEEK);
        }else{
            hintsText.setText("The average number of steps for a day is "+NB_STEPS_FOR_DAY);
        }

    }

    public void onTimeTicked(View view){
        relative1.setVisibility(View.VISIBLE);
        rdbSteps.setChecked(false);
        stepsEtext.setVisibility(View.INVISIBLE);
        if(isWeek){
            hintsText.setText("The average  duration of training for a week is "+NB_HOUR_FOR_WEEK+" h "+NB_MIN_FOR_WEEK+" min");
        }else{
            hintsText.setText("The average duration of training for a day is "+NB_HOUR_FOR_DAY +" h "+NB_MIN_FOR_DAY+" min");
        }
    }



    public void buttonConfirmClick(View view){
        if(rdbSteps.isChecked()){
            stepsEditable = stepsEtext.getText();

            if(!(String.valueOf(stepsEditable).equals(""))) {
                nbSteps = Integer.parseInt(String.valueOf(stepsEditable));
                android.app.FragmentManager fm = getFragmentManager();
                DialogFragment newFragment = new textDialogAccept();
                newFragment.show(fm, "ActivityAccept");
            }
        }
        else{
            minEditable = minEtext.getText();
            hoursEditable = hoursEtext.getText();
            if (!(String.valueOf(hoursEditable).equals("")) && !(String.valueOf(minEditable).equals(""))){
                nbMin = Integer.parseInt(String.valueOf(minEditable));
                nbHours = Integer.parseInt(String.valueOf(hoursEditable));
                android.app.FragmentManager fm = getFragmentManager();
                DialogFragment newFragment = new textDialogAccept();
                newFragment.show(fm, "ActivityAccept");
            }
        }
    }


}
