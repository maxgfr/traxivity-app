package com.fanny.traxivity.admin.view.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.model.Information;
import com.fanny.traxivity.admin.model.Message;
import com.fanny.traxivity.admin.view.activities.NewMessage;

/**
 * Created by Alexandre on 05/05/2017.
 */

public class elementDetailsDialog extends DialogFragment {

    private Message message;
    private Information information;

    public static elementDetailsDialog newInstance(Object object){
        elementDetailsDialog mDD = new elementDetailsDialog();

        Bundle args = new Bundle();
        if(object instanceof Message)
            args.putParcelable("message", (Message)object);
        else{
            if(object instanceof Information){
                args.putParcelable("information", (Information)object);
            }
        }
        mDD.setArguments(args);

        return mDD;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if(bundle.containsKey("message")){
            message = bundle.getParcelable("message");

            final View view = inflater.inflate(R.layout.message_details_dialog, null);

            TextView category = (TextView)view.findViewById(R.id.tv_md_category);
            category.setText(message.getCategory());

            TextView achLevel = (TextView)view.findViewById(R.id.tv_md_achLevel);
            achLevel.setText(message.getAchievement().toString());

            TextView content = (TextView)view.findViewById(R.id.tv_md_content);
            content.setText(message.getContent());

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent messageEditing = new Intent(getActivity(), NewMessage.class);
                            messageEditing.putExtra("messageToEdit", message);
                            getActivity().startActivity(messageEditing);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            elementDetailsDialog.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Message details");
        }
        else{
            if(bundle.containsKey("information")){
                information = bundle.getParcelable("information");

                final View view = inflater.inflate(R.layout.information_details_dialog, null);

                TextView type = (TextView)view.findViewById(R.id.tv_id_type);
                type.setText(information.getType());

                TextView content = (TextView)view.findViewById(R.id.tv_id_content);
                content.setText(information.getContent());


                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(view)
                        // Add action buttons
                        .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                elementDetailsDialog.this.getDialog().cancel();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                elementDetailsDialog.this.getDialog().cancel();
                            }
                        });

                builder.setTitle("Message details");
            }
        }
        return builder.create();
    }

}
