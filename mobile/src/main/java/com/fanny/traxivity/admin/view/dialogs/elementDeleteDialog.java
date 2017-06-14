package com.fanny.traxivity.admin.view.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.ConfirmationDAO;
import com.fanny.traxivity.admin.controller.InformationDAO;
import com.fanny.traxivity.admin.controller.MessageDAO;
import com.fanny.traxivity.admin.model.Confirmation;
import com.fanny.traxivity.admin.model.Information;
import com.fanny.traxivity.admin.model.Message;

/**
 * Created by Alexandre on 16/05/2017.
 */

public class elementDeleteDialog extends DialogFragment {

    private Message message;
    private Information information;
    private Confirmation confirmation;

    public static elementDeleteDialog newInstance(Object object) {
        elementDeleteDialog mDD = new elementDeleteDialog();

        Bundle args = new Bundle();
        if (object instanceof Message)
            args.putParcelable("message", (Message) object);
        else {
            if (object instanceof Confirmation) {
                args.putParcelable("confirmation", (Confirmation) object);
            }
            else {
                if (object instanceof Information) {
                    args.putParcelable("information", (Information) object);
                }
            }
        }
        mDD.setArguments(args);

        return mDD;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        message = new Message();
        information = new Information();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.message_delete_dialog, null);

        if (bundle.containsKey("message")) {
            message = bundle.getParcelable("message");

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            MessageDAO.getInstance().deleteMessage(message);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            elementDeleteDialog.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Delete message");
        } else {
            if (bundle.containsKey("information")) {
                information = bundle.getParcelable("information");

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(view)
                        // Add action buttons
                        .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                InformationDAO.getInstance().deleteInformation(information);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                elementDeleteDialog.this.getDialog().cancel();
                            }
                        });

                builder.setTitle("Delete information message");
            } else {
                if (bundle.containsKey("confirmation")) {
                    confirmation = bundle.getParcelable("confirmation");

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setView(view)
                            // Add action buttons
                            .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    ConfirmationDAO.getInstance().deleteConfirmation(confirmation);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    elementDeleteDialog.this.getDialog().cancel();
                                }
                            });

                    builder.setTitle("Delete confirmation message");
                }
            }
        }


        return builder.create();
    }
}
