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

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.ConfirmationDAO;
import com.fanny.traxivity.admin.model.Confirmation;
import com.fanny.traxivity.database.goal.DbGoal;
import com.fanny.traxivity.database.goal.GoalManager;
import com.fanny.traxivity.model.DateUtil;
import com.fanny.traxivity.view.GoalInputActivity;
import com.fanny.traxivity.view.InactivityGoalInput;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import io.realm.Realm;

/**
 * Created by jbjourget.
 */

public class textDialogAccept extends DialogFragment {


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
        fullmessage = "" ;
        final View view = inflater.inflate(R.layout.text_dialog, null);
        tv1 = (TextView) view.findViewById(R.id.textView);


        builder.setView(view)
                .setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        GoalManager manager = new GoalManager();
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.delete(DbGoal.class);
                        realm.commitTransaction();
                        DbGoal newGoal = new DbGoal();
                        Date today = new Date();

                        if(GoalInputActivity.nbSteps == null){
                            manager.insertGoal(newGoal.withDuration(today, DateUtil.addDays(today,1),GoalInputActivity.nbHours*3600+GoalInputActivity.nbMin*60));
                            for(int i=1; i<7; i++){
                                manager.insertGoal(newGoal.withDuration(DateUtil.addDays(today,i), DateUtil.addDays(today,i+1),GoalInputActivity.nbHours*3600+GoalInputActivity.nbMin*60));
                            }
                        }
                        else {
                            manager.insertGoal(newGoal.withSteps(today, DateUtil.addDays(today,1),GoalInputActivity.nbSteps));
                            for(int i=1; i<7; i++){
                                manager.insertGoal(newGoal.withSteps(DateUtil.addDays(today,i), DateUtil.addDays(today,i+1),GoalInputActivity.nbSteps));
                            }
                        }

                        //  InactivityGoalInput.dateDeb = GoalInputActivity.dateD;
                        //  GoalInputActivity.dateD = null;
                        GoalInputActivity.nbMin = null;
                        GoalInputActivity.nbHours = null;
                        GoalInputActivity.nbSteps = null;
                        Intent intent = new Intent(getActivity(), InactivityGoalInput.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // GoalInputActivity.dateD = null;
                        GoalInputActivity.nbMin = null;
                        GoalInputActivity.nbHours = null;
                        GoalInputActivity.nbSteps = null;
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

                    if(GoalInputActivity.nbSteps != null) {
                        fullmessage = fullmessage.replaceAll("<steps>", String.valueOf(GoalInputActivity.nbSteps));
                    }else {
                        fullmessage = fullmessage.replaceAll("<mins>", String.valueOf(GoalInputActivity.nbHours)+" Hours and "+GoalInputActivity.nbMin+" min");
                    }
                    tv1.setText(fullmessage);
                }
                else{
                    Exception e = task.getException();
                    System.out.println(e.getMessage());
                }
            }
        });




        if(GoalInputActivity.nbSteps != null){
            /*if(GoalInputActivity.isWeek){
                fullmessage = fullmessage+ GoalInputActivity.nbSteps+" steps for a week do you agree to commit to the set objective ?";
            }else{*/
            ConfirmationDAO.getInstance().getConfirmation("Steps", getConfirmationTask);
            //fullmessage = fullmessage+ GoalInputActivity.nbSteps+" steps for each day do you agree to commit to the set objective ?";
            // }
        }else{

            // if(!GoalInputActivity.isWeek){
            ConfirmationDAO.getInstance().getConfirmation("Activity", getConfirmationTask);
            //fullmessage = fullmessage+ GoalInputActivity.nbHours+" h and "+ GoalInputActivity.nbMin+" min for each day do you agree to commit to the set objective ?";
           /* }else{
                fullmessage = fullmessage+ GoalInputActivity.nbHours+" h and "+ GoalInputActivity.nbMin+" min for a week do you agree to commit to the set objective ?";
            }*/
        }


        // Create the AlertDialog object and return it
        return builder.create();
    }

}