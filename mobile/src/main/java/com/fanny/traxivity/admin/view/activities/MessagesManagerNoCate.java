package com.fanny.traxivity.admin.view.activities;

/**
 * Created by extra on 06/06/2017.
 */

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
import android.widget.RadioButton;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EnumMap;

public class MessagesManagerNoCate extends AppCompatActivity {

    private static final String TAG = MessagesManagerNoCate.class.getSimpleName();

    protected boolean wereDisabled;

    private static Context context;

    private TaskCompletionSource<ArrayList<Message>> getMessagesTask;
    private TaskCompletionSource<ArrayList<String>> getCategoriesTask;
    private Task getMessagesTaskWaiter;
    private Task getCategoriesTaskWaiter;

    private EnumMap<MessagesManager.Achievement, String> achievement_map;
    protected static ArrayList<Message> messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_week_messages_manager);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = this;

        messagesList = new ArrayList<>();

        getMessagesTask = new TaskCompletionSource<>();
        getMessagesTaskWaiter = getMessagesTask.getTask();


        Button bt_search = (Button)findViewById(R.id.mm_bt_search);
        Button bt_newMessage = (Button)findViewById(R.id.mm_bt_newMessage);
        final ListView lv_messages = (ListView)findViewById(R.id.lv_messagesNoCategory);
        final ProgressBar pb_messagesLoading = (ProgressBar)findViewById(R.id.pb_noCate_messagesLoadin);
        pb_messagesLoading.setVisibility(View.GONE);
        final RadioGroup rg_achievementLevel = (RadioGroup)findViewById(R.id.rg_mm_achievementLevel);
        final RadioGroup rg_dayWeek = (RadioGroup)findViewById(R.id.rg_mm_dayWeek);
        final RadioGroup rg_dayTime = (RadioGroup)findViewById(R.id.rg_mm_dayTime);
        final RadioButton rb_day = (RadioButton)findViewById(R.id.rb_mmDay);
        final RadioButton rb_anytime = (RadioButton)findViewById(R.id.rb_mm_none);
        final RadioButton rb_week = (RadioButton)findViewById(R.id.rb_mm_week);
        final RadioButton rb_mid = (RadioButton)findViewById(R.id.rb_mmMidday);
        final RadioButton rb_end = (RadioButton)findViewById(R.id.rb_mm_end);
        final RadioButton rb_full = (RadioButton)findViewById(R.id.rb_mmFull);
        wereDisabled = false;
        final RadioButton rb_low = (RadioButton)findViewById(R.id.rb_mmLow);
        final RadioButton rb_mod = (RadioButton)findViewById(R.id.rb_mmModerate);
        final RadioButton rb_high = (RadioButton)findViewById(R.id.rb_mmHigh);

        final TextView tv_none = (TextView) findViewById(R.id.tv_noCate_None);
        final TextView tv_listMessages = (TextView)findViewById(R.id.tv_listMessages);
        final TextView tv_time = (TextView)findViewById(R.id.tv_time);
        tv_listMessages.setVisibility(View.GONE);
        tv_none.setVisibility(View.GONE);

        final MessagesAdapter adapter = new MessagesAdapter(this, messagesList);
        lv_messages.setAdapter(adapter);

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

        rb_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_time.setText("Day time :");
                rb_mid.setText("Midday");
                rb_end.setText("End of Day");
                if(rb_full.isChecked()){
                    for(int i = 0; i < rg_dayTime.getChildCount(); i++){
                        (rg_dayTime.getChildAt(i)).setEnabled(false);
                    }
                    wereDisabled = true;
                    rb_anytime.setChecked(true);
                }
            }
        });

        rb_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_time.setText("Week time :");
                rb_mid.setText("Mid-week");
                rb_end.setText("End of Week");
                if(wereDisabled){
                    for(int i = 0; i < rg_dayTime.getChildCount(); i++){
                        (rg_dayTime.getChildAt(i)).setEnabled(true);
                    }
                    wereDisabled = false;
                }
            }
        });

        rb_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rb_day.isChecked()){
                    for(int i = 0; i < rg_dayTime.getChildCount(); i++){
                        (rg_dayTime.getChildAt(i)).setEnabled(false);
                    }
                    wereDisabled = true;
                    rb_anytime.setChecked(true);
                }
            }
        });

        rb_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wereDisabled && rb_day.isChecked()){
                    for(int i = 0; i < rg_dayTime.getChildCount(); i++){
                        (rg_dayTime.getChildAt(i)).setEnabled(true);
                    }
                    wereDisabled = false;
                }
            }
        });

        rb_mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wereDisabled && rb_day.isChecked()){
                    for(int i = 0; i < rg_dayTime.getChildCount(); i++){
                        (rg_dayTime.getChildAt(i)).setEnabled(true);
                    }
                    wereDisabled = false;
                }
            }
        });

        rb_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wereDisabled && rb_day.isChecked()){
                    for(int i = 0; i < rg_dayTime.getChildCount(); i++){
                        (rg_dayTime.getChildAt(i)).setEnabled(true);
                    }
                    wereDisabled = false;
                }
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
                MessagesManager.Achievement level = MessagesManager.Achievement.High;
                if(rb_index == R.id.rb_mmLow){
                    level = MessagesManager.Achievement.Low;
                }else{
                    if(rb_index == R.id.rb_mmModerate)
                        level = MessagesManager.Achievement.Moderate;
                    else{
                        if(rb_index == R.id.rb_mmHigh){
                            level = MessagesManager.Achievement.High;
                        }else{
                            if(rb_index == R.id.rb_mmFull)
                                level = MessagesManager.Achievement.Full;
                        }
                    }
                }

                rb_index = rg_dayWeek.getCheckedRadioButtonId();
                String dayWeek = "";
                if(rb_index == R.id.rb_mm_week){
                    dayWeek = "Week";
                }else{
                    if(rb_index == R.id.rb_mmDay)
                        dayWeek = "Day";
                }

                rb_index = rg_dayTime.getCheckedRadioButtonId();
                String category = "";
                if(dayWeek.equals("Day")){
                    if(rb_index == R.id.rb_mmBeginning){
                        category = "Beginning";
                    }else{
                        if(rb_index == R.id.rb_mmMidday)
                            category = "Midday";
                        else{
                            if(rb_index == R.id.rb_mm_end){
                                category = "End of day";
                            }else{
                                if(rb_index == R.id.rb_mm_none)
                                    category = "Anytime";
                            }
                        }
                    }
                }
                else{
                    if(rb_index == R.id.rb_mmBeginning){
                        category = "Beginning";
                    }else{
                        if(rb_index == R.id.rb_mmMidday)
                            category = "Mid-week";
                        else{
                            if(rb_index == R.id.rb_mm_end){
                                category = "End of week";
                            }else{
                                if(rb_index == R.id.rb_mm_none)
                                    category = "Anytime";
                            }
                        }
                    }
                }


                pb_messagesLoading.setVisibility(View.VISIBLE);
                pb_messagesLoading.setIndeterminate(true);
                MessageDAO.getInstance().getMessages(level, category, dayWeek, getMessagesTask);
            };
        });

        bt_newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dayWeekIntent = new Intent(MessagesManagerNoCate.this, DayWeekChoice.class);
                startActivity(dayWeekIntent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}