package com.fanny.traxivity.admin.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.model.Information;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/05/2017.
 */

public class InformationAdapter extends ArrayAdapter<Information> {

    public InformationAdapter(Context context, ArrayList<Information> informations){
        super(context, 0, informations);
    }

    private class MyViewHolder{
        private TextView tv_content;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_list_messages,parent, false);
        }

        InformationAdapter.MyViewHolder viewHolder = (InformationAdapter.MyViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new InformationAdapter.MyViewHolder();
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Information information = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.tv_content.setText(information.getContent());

        return convertView;
    }
}
