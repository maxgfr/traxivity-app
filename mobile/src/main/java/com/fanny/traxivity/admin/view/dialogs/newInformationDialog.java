package com.fanny.traxivity.admin.view.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.InformationDAO;
import com.fanny.traxivity.admin.model.Information;

/**
 * Created by Alexandre on 22/05/2017.
 */

public class newInformationDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.new_information, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RadioGroup rg_infoType = (RadioGroup)view.findViewById(R.id.rg_newInfoType);
                        int rb_index = rg_infoType.getCheckedRadioButtonId();
                        String type = "";
                        if(rb_index == R.id.rb_activityNewInfo){
                            type = "Activity";
                        }else{
                            if(rb_index == R.id.rb_stepsNewInfo)
                                type = "Steps";
                            else
                            if(rb_index == R.id.rb_inactivityNewInfo)
                                type = "Inactivity";
                        }
                        EditText et_contentNewInfo = (EditText)view.findViewById(R.id.et_contentNewInfo);

                        Information information = new Information(type, et_contentNewInfo.getText().toString());
                        if(!information.getType().equals("") && !information.getContent().equals("")){
                            InformationDAO.getInstance().addInformation(information);
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newInformationDialog.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Add information message");

        return builder.create();
    }
}