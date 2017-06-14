package com.fanny.traxivity.admin.view.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.MessageDAO;
import com.fanny.traxivity.admin.model.Message;
import com.fanny.traxivity.admin.view.MessagesAdapter;
import com.fanny.traxivity.admin.view.dialogs.elementDeleteDialog;
import com.fanny.traxivity.admin.view.dialogs.elementDetailsDialog;
import com.fanny.traxivity.admin.view.dialogs.newCategoryDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EnumMap;

public class MessagesManager extends AppCompatActivity {

    private static final String TAG = MessagesManager.class.getSimpleName();

    private String messagesCategoriesFileName = "messagesCategoriesName";

    private static Context context;

    public enum Achievement {
        Low,
        Moderate,
        High,
        Full
    }

    private TaskCompletionSource<ArrayList<Message>> getMessagesTask;
    private TaskCompletionSource<ArrayList<String>> getCategoriesTask;
    private Task getMessagesTaskWaiter;
    private Task getCategoriesTaskWaiter;


    private Spinner spinner_category;
    public static ArrayAdapter<String> categories_adapter;


    private MessagesAdapter mRecyclerAdapter;
    public static ArrayList<String> bctCategory_list;
    private EnumMap<Achievement, String> achievement_map;
    protected static ArrayList<Message> messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_manager);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = this;

        bctCategory_list = new ArrayList<>();
        bctCategory_list.add("Loading list...");

        messagesList = new ArrayList<>();

        getMessagesTask = new TaskCompletionSource<>();
        getMessagesTaskWaiter = getMessagesTask.getTask();


        Button bt_search = (Button)findViewById(R.id.ma_bt_search);
        Button bt_newCategory = (Button)findViewById(R.id.ma_bt_add_category);
        Button bt_newMessage = (Button)findViewById(R.id.ma_bt_newMessage);
        final ListView lv_messages = (ListView)findViewById(R.id.lv_messages);
        final ProgressBar pb_messagesLoading = (ProgressBar)findViewById(R.id.pb_messagesLoading);
        pb_messagesLoading.setVisibility(View.GONE);
        final RadioGroup rg_achievementLevel = (RadioGroup)findViewById(R.id.ma_rg_achievementLevels);
        final TextView tv_none = (TextView) findViewById(R.id.tv_None);
        final TextView tv_listMessages = (TextView)findViewById(R.id.tv_listMessages);
        tv_listMessages.setVisibility(View.GONE);
        tv_none.setVisibility(View.GONE);
        spinner_category = (Spinner)findViewById(R.id.ma_spinner_category);

        final MessagesAdapter adapter = new MessagesAdapter(this, messagesList);
        lv_messages.setAdapter(adapter);

        categories_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bctCategory_list);
        categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_category.setAdapter(categories_adapter);

        getCategoriesTask = new TaskCompletionSource<>();
        getCategoriesTaskWaiter = getCategoriesTask.getTask();

        getCategoriesTaskWaiter.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    bctCategory_list.clear();
                    bctCategory_list.addAll((ArrayList<String>)task.getResult());
                    categories_adapter.notifyDataSetChanged();
                }
                else{
                    Exception e = task.getException();
                    System.out.println(e.getMessage());
                }
            }
        });

        MessageDAO.getInstance().getCategories(getCategoriesTask);


        lv_messages.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message messageClicked = adapter.getItem(position);

                DialogFragment messageDetails = elementDetailsDialog.newInstance(messageClicked);
                messageDetails.show(getFragmentManager(),TAG);
            }
        });

        lv_messages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Message messageClicked = adapter.getItem(position);

                DialogFragment messageDelete = elementDeleteDialog.newInstance(messageClicked);
                messageDelete.show(getFragmentManager(),TAG);

                return true;
            }
        });


        bt_newCategory.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DialogFragment categoryDialog = new newCategoryDialog();
                categoryDialog.show(getFragmentManager(), "new category");
            }

        });


        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMessagesTask = new TaskCompletionSource<>();
                getMessagesTaskWaiter = getMessagesTask.getTask();

                getMessagesTaskWaiter.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            tv_listMessages.setVisibility(View.VISIBLE);
                            Log.d("task", "success");
                            messagesList.clear();
                            messagesList.addAll((ArrayList<Message>)task.getResult());
                            if(messagesList.size() == 0){
                                tv_none.setVisibility(View.VISIBLE);
                                tv_none.setText("None");
                            }
                            else
                                tv_none.setVisibility(View.GONE);
                            pb_messagesLoading.setIndeterminate(false);
                            pb_messagesLoading.setVisibility(View.GONE);
                            Toast.makeText(context, Integer.toString(messagesList.size()) + " message(s) found", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            Exception e = task.getException();
                            System.out.println(e.getMessage());
                        }
                    }
                });

                int rb_index = rg_achievementLevel.getCheckedRadioButtonId();
                Achievement level = Achievement.High;
                if(rb_index == R.id.ma_r_low){
                    level = Achievement.Low;
                }else{
                    if(rb_index == R.id.ma_r_moderate)
                        level = Achievement.Moderate;
                    else{
                        if(rb_index == R.id.ma_r_high){
                            level = Achievement.High;
                        }else{
                            if(rb_index == R.id.ma_r_full)
                                level = Achievement.Full;
                        }
                    }
                }
                pb_messagesLoading.setVisibility(View.VISIBLE);
                pb_messagesLoading.setIndeterminate(true);
                MessageDAO.getInstance().getMessages(level, spinner_category.getSelectedItem().toString(), getMessagesTask);
            };
        });

        bt_newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newMessageIntent = new Intent(MessagesManager.this, NewMessage.class);
                startActivity(newMessageIntent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

}

