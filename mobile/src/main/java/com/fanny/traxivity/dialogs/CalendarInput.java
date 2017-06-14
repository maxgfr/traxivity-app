package com.fanny.traxivity.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.fanny.traxivity.R;
import com.fanny.traxivity.view.GoalInputActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jbjourget.
 */

public class CalendarInput extends DialogFragment {

    DatePicker datePicker;
    Date dateSelected;
    DateFormat df;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.calendar_picker, null);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        final GoalInputActivity parent = (GoalInputActivity) getActivity();

        builder.setView(view)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int year = datePicker.getYear();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        dateSelected = calendar.getTime();
                        //GoalInputActivity.dateD = dateSelected;
                        //parent.refreshDate();

                        Log.w("test", dateSelected.toString());
                    }
                });
        return builder.create();
    }
}