package com.fanny.traxivity.admin.view.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.view.activities.MessagesManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Alexandre on 02/05/2017.
 */

public class newCategoryDialog extends DialogFragment {
    private String messagesCategoriesFileName = "messagesCategoriesName";
    private OutputStreamWriter outputStreamWriter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.new_category_dialog, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText et_category = (EditText)view.findViewById(R.id.et_cname);

                        MessagesManager.bctCategory_list.add(et_category.getText().toString());
                        MessagesManager.categories_adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newCategoryDialog.this.getDialog().cancel();
                    }
                });

        builder.setTitle("New category");

        return builder.create();
    }
}
