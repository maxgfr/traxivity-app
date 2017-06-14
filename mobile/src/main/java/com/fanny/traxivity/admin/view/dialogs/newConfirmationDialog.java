package com.fanny.traxivity.admin.view.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.ConfirmationDAO;
import com.fanny.traxivity.admin.model.Confirmation;
import com.fanny.traxivity.admin.view.activities.ConfirmationManager;

/**
 * Created by Alexandre on 25/05/2017.
 */

public class newConfirmationDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.new_confirmation, null);

        final Button bt_placeholder = (Button)view.findViewById(R.id.bt_placeholder);
        final EditText et_contentConf = (EditText)view.findViewById(R.id.et_contentNewConf);
        final RadioButton rb_stepsConf = (RadioButton)view.findViewById(R.id.rb_stepsNewConf);
        final RadioButton rb_activityConf = (RadioButton)view.findViewById(R.id.rb_activityNewConf);
        final RadioButton rb_inactivityConf = (RadioButton)view.findViewById(R.id.rb_inactivityNewConf);


        rb_stepsConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = et_contentConf.getText().toString().replace("<time>"," <steps>");
                et_contentConf.setText(newText);
                bt_placeholder.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_steps, 0, 0, 0);
                bt_placeholder.setText(bt_placeholder.getText().toString().replace("'time'","'steps'"));
            }
        });

        rb_activityConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = et_contentConf.getText().toString().replace("<steps>","<time>");
                et_contentConf.setText(newText);
                bt_placeholder.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_activities, 0, 0, 0);
                bt_placeholder.setText(bt_placeholder.getText().toString().replace("'steps'","'time'"));
            }
        });

        rb_inactivityConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = et_contentConf.getText().toString().replace("<steps>","<time>");
                et_contentConf.setText(newText);
                bt_placeholder.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_inactivity, 0, 0, 0);
                bt_placeholder.setText(bt_placeholder.getText().toString().replace("'steps'","'time'"));
            }
        });


        bt_placeholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = "";
                if(rb_activityConf.isChecked())
                    newText = et_contentConf.getText().toString() + " <time> ";

                if(rb_inactivityConf.isChecked())
                    newText = et_contentConf.getText().toString() + " <time> ";

                if(rb_stepsConf.isChecked())
                    newText = et_contentConf.getText().toString() + " <steps> ";

                et_contentConf.setText(newText);
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(!et_contentConf.getText().toString().contains("<mins>") && !et_contentConf.getText().toString().contains("<steps>")){
                            Toast.makeText(ConfirmationManager.context, "You must insert placeholder(s)!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String type = "";
                            if(rb_activityConf.isChecked()){
                                type = "Activity";
                            }else{
                                if(rb_stepsConf.isChecked())
                                    type = "Steps";
                                else
                                if(rb_inactivityConf.isChecked())
                                    type = "Inactivity";
                            }

                            Confirmation confirmation = new Confirmation(type, et_contentConf.getText().toString());
                            if(!confirmation.getType().equals("") && !confirmation.getContent().equals("")){
                                ConfirmationDAO.getInstance().addConfirmation(confirmation);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newConfirmationDialog.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Add information message");

        return builder.create();
    }
}