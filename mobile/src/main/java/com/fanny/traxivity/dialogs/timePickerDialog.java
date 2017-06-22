package com.fanny.traxivity.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fanny.traxivity.R;
import com.fanny.traxivity.database.dayTiming.DayTimingManager;
import com.fanny.traxivity.database.dayTiming.DbTiming;
import com.fanny.traxivity.model.SetAlarm;
import com.fanny.traxivity.view.AddNewActivity;
import com.fanny.traxivity.view.SettingsActivity;

import java.util.Calendar;
import java.util.Set;

/**
 * Created by almabire.
 */

public class timePickerDialog extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private String value;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle mArgs = getArguments();
        value = mArgs.getString("key");
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        DayTimingManager managerTiming = new DayTimingManager();
        switch (value) {
            case "newActivity":
                TextView tv_date = (TextView) getActivity().findViewById(R.id.tv_time);

                AddNewActivity.hourStartTime = hourOfDay;
                AddNewActivity.minutesStartTime = minute;

                tv_date.setText(Html.fromHtml("Start time : <font color='#000000'>" + Integer.toString(hourOfDay) + ":" + Integer.toString(minute) + "</font>"));
                break;
            case "start":
                TextView tv_start = (TextView) getActivity().findViewById(R.id.tv_start);
                SettingsActivity.hourStartTime = hourOfDay;
                SettingsActivity.minutesStartTime = minute;
                DbTiming timingStart = new DbTiming("start",hourOfDay,minute);
                managerTiming.insertTiming(timingStart);
                tv_start.setText(hourOfDay+":"+minute);

                SetAlarm.alarm.cancelAlarm(view.getContext());
                SetAlarm.setAlarmStart(view.getContext());
                break;
            case "mid":
                TextView tv_mid = (TextView) getActivity().findViewById(R.id.tv_mid);
                SettingsActivity.hourMidTime = hourOfDay;
                SettingsActivity.minutesMidTime = minute;
                DbTiming timingMid = new DbTiming("mid",hourOfDay,minute);
                managerTiming.insertTiming(timingMid);
                tv_mid.setText(hourOfDay+":"+minute);

                SetAlarm.alarm.cancelAlarm(view.getContext());
                SetAlarm.setAlarmMid(view.getContext());
                break;
            case "end":
                TextView tv_end = (TextView) getActivity().findViewById(R.id.tv_end);
                SettingsActivity.hourEndTime = hourOfDay;
                SettingsActivity.minutesEndTime = minute;
                DbTiming timingEnd = new DbTiming("end",hourOfDay,minute);
                managerTiming.insertTiming(timingEnd);
                tv_end.setText(hourOfDay+":"+minute);

                SetAlarm.alarm3.cancelAlarm(view.getContext());
                SetAlarm.setAlarmEnd(view.getContext());
                break;
        }
    }
}
