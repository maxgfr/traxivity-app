package com.fanny.traxivity.admin.view;

/**
 * Created by Alexandre on 05/05/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.model.Message;

import java.util.ArrayList;


public class MessagesAdapter extends ArrayAdapter<Message> {

    public MessagesAdapter(Context context, ArrayList<Message> messages){
        super(context, 0, messages);
    }

    private class MyViewHolder{
        private TextView tv_content;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_list_messages,parent, false);
        }

        MyViewHolder viewHolder = (MyViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new MyViewHolder();
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Message message = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.tv_content.setText(message.getContent());

        return convertView;
    }
}
