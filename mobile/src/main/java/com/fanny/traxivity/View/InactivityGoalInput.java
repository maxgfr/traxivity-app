package com.fanny.traxivity.View;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.fanny.traxivity.R;

import static com.fanny.traxivity.View.GoalInputActivity.NB_HOUR_FOR_DAY;
import static com.fanny.traxivity.View.GoalInputActivity.NB_MIN_FOR_DAY;
import static com.fanny.traxivity.View.GoalInputActivity.NB_STEPS_FOR_DAY;

/**
 * Created by jbjourget on 05/05/2017.
 */


public class InactivityGoalInput extends AppCompatActivity {


    private static final int DEFAULT_MAX_MIN = 59;
    private static final int DEFAULT_MIN_MIN = 0;
    private static final int DEFAULT_MAX_HOURS = 8;
    private static final int DEFAULT_MIN_HOURS = 0;
    public static Integer nbHours;
    public static Integer nbMin;
    NumberPicker nbPickerMin;
    NumberPicker nbPickerHours;
    Button confirmButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inactivity_goal);
        confirmButton = (Button) findViewById(R.id.appCompatButtonInactivity);
        nbPickerMin = (NumberPicker) findViewById(R.id.numberPickerMin);
        nbPickerHours = (NumberPicker) findViewById(R.id.numberPickerHours);
        nbPickerMin.setMinValue(DEFAULT_MIN_MIN);
        nbPickerMin.setMaxValue(DEFAULT_MAX_MIN);
        nbPickerHours.setMinValue(DEFAULT_MIN_HOURS);
        nbPickerHours.setMaxValue(DEFAULT_MAX_HOURS);
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

    private void SetDividerColor(NumberPicker picker)
    {

    }
}
