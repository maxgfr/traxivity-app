package com.fanny.traxivity.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fanny.traxivity.MainActivity;
import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.ConfirmationDAO;
import com.fanny.traxivity.admin.model.Confirmation;
import com.fanny.traxivity.database.inactivity.DbInactivity;
import com.fanny.traxivity.database.inactivity.InactivityManager;
import com.fanny.traxivity.view.InactivityGoalInput;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jbjourget.
 */

public class textDialogInactivity extends DialogFragment {

    private TaskCompletionSource<ArrayList<Confirmation>> getConfirmationTask;
    private Task getConfirmationTaskWaiter;
    protected ArrayList<Confirmation> confirmationList;
    TextView tv1;
    String fullmessage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.text_dialog, null);
        tv1 = (TextView) view.findViewById(R.id.textView);
        fullmessage= "";

        builder.setView(view)
                .setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        InactivityManager managerInactivity = new InactivityManager();
                        DbInactivity myInactivity = new DbInactivity(InactivityGoalInput.dateDeb, InactivityGoalInput.nbHours, InactivityGoalInput.nbMin);
                        managerInactivity.insertInactivity(myInactivity);

                        InactivityGoalInput.dateDeb = null;
                        InactivityGoalInput.nbHours = null;
                        InactivityGoalInput.nbMin = null;
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InactivityGoalInput.nbHours = null;
                        InactivityGoalInput.nbMin = null;
                    }
                });

        confirmationList = new ArrayList<>();
        getConfirmationTask = new TaskCompletionSource<>();
        getConfirmationTaskWaiter = getConfirmationTask.getTask();

        getConfirmationTaskWaiter.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Log.d("task", "success");
                    confirmationList.clear();
                    confirmationList.addAll((ArrayList<Confirmation>)task.getResult());

                    Random rand = new Random();
                    fullmessage = confirmationList.get(rand.nextInt(confirmationList.size())).getContent();//Randomize the message

                    fullmessage = fullmessage.replaceAll("<mins>", InactivityGoalInput.nbHours+" Hours and "+InactivityGoalInput.nbMin+" min");

                    tv1.setText(fullmessage);
                }
                else{
                    Exception e = task.getException();
                    System.out.println(e.getMessage());
                }
            }
        });
        ConfirmationDAO.getInstance().getConfirmation("Inactivity", getConfirmationTask);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}