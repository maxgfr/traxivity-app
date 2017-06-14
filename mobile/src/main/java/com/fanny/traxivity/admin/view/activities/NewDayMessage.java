package com.fanny.traxivity.admin.view.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.MessageDAO;
import com.fanny.traxivity.admin.model.Message;
import com.fanny.traxivity.admin.model.Quote;
import com.fanny.traxivity.admin.view.dialogs.newQuoteDialog;

/**
 * Created by extra on 06/06/2017.
 */

public class NewDayMessage extends AppCompatActivity {

    public static TextView tv_quote;
    public static ImageButton bt_removeQuote;

    boolean wereDisabled;

    public static Message newMessage;
    protected Message toEdit;
    protected Quote editQuote;
    protected boolean editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_day_messages);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        newMessage = getIntent().getParcelableExtra("messageToEdit");
        if (newMessage != null) {
            editQuote = newMessage.getQuote();
            editing = true;
        } else {
            editing = false;
            newMessage = new Message();
        }

        Button bt_send = (Button) findViewById(R.id.bt_addDayMessage);
        ImageButton bt_newQuote = (ImageButton) findViewById(R.id.ib_quoteDay);
        bt_removeQuote = (ImageButton) findViewById(R.id.ib_deleteQuoteDay);
        if (newMessage.getQuote() == null) {
            bt_removeQuote.setVisibility(View.GONE);
        }
        final RadioGroup rg_achievementLevel = (RadioGroup) findViewById(R.id.rg_newDayM_achievementLevel);
        final RadioGroup rg_category = (RadioGroup) findViewById(R.id.rg_newM_dayTime);
        final RadioButton rb_anytime = (RadioButton)findViewById(R.id.rb_newDayM_Anytime);
        final RadioButton rb_full = (RadioButton)findViewById(R.id.rb_newDayM_Full);
        wereDisabled = false;
        final RadioButton rb_low = (RadioButton)findViewById(R.id.rb_newDayM_Low);
        final RadioButton rb_mod = (RadioButton)findViewById(R.id.rb_newDayM_Moderate);
        final RadioButton rb_high = (RadioButton)findViewById(R.id.rb_newDayM_High);
        final EditText et_content = (EditText) findViewById(R.id.et_messageContentDay);
        tv_quote = (TextView) findViewById(R.id.tv_quoteDay);
        tv_quote.setVisibility(View.GONE);

        rb_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < rg_category.getChildCount(); i++){
                    (rg_category.getChildAt(i)).setEnabled(false);
                }
                wereDisabled = true;
                rb_anytime.setChecked(true);
            }
        });

        rb_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wereDisabled){
                    for(int i = 0; i < rg_category.getChildCount(); i++){
                        (rg_category.getChildAt(i)).setEnabled(true);
                    }
                    wereDisabled = false;
                }
            }
        });

        rb_mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wereDisabled){
                    for(int i = 0; i < rg_category.getChildCount(); i++){
                        (rg_category.getChildAt(i)).setEnabled(true);
                    }
                    wereDisabled = false;
                }
            }
        });

        rb_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wereDisabled){
                    for(int i = 0; i < rg_category.getChildCount(); i++){
                        (rg_category.getChildAt(i)).setEnabled(true);
                    }
                    wereDisabled = false;
                }
            }
        });

        if (editing) {
            bt_send.setText("Edit");
            switch (newMessage.getAchievement()) {
                case Low:
                    rg_achievementLevel.check(R.id.rb_newDayM_Low);
                    break;
                case Moderate:
                    rg_achievementLevel.check(R.id.rb_newDayM_Moderate);
                    break;
                case High:
                    rg_achievementLevel.check(R.id.rb_newDayM_High);
                    break;
                case Full:
                    rg_achievementLevel.check(R.id.rb_newDayM_Full);
                    break;
            }

            switch (newMessage.getCategory()) {
                case "Beginning":
                    rg_category.check(R.id.rb_newDayM_Beginning);
                    break;
                case "Midday":
                    rg_category.check(R.id.rb_newDayM_Midday);
                    break;
                case "End of Day":
                    rg_category.check(R.id.rb_newDayM_End);
                    break;
                case "Anytime":
                    rg_category.check(R.id.rb_newDayM_Anytime);
                    break;
            }

            if (newMessage.getQuote() != null)
                tv_quote.setText(Html.fromHtml(newMessage.getQuote().toString()));

            et_content.setText(newMessage.getContent(), TextView.BufferType.EDITABLE);
        }

        bt_newQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment quoteDialog = new newQuoteDialog();
                quoteDialog.show(getFragmentManager(), "new quote");
            }
        });

        bt_removeQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMessage.setQuote(null);
                tv_quote.setVisibility(View.GONE);
                bt_removeQuote.setVisibility(View.GONE);
            }
        });

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editing) {
                    toEdit = new Message(newMessage);
                    toEdit.setQuote(editQuote);
                }

                newMessage.setDayWeek("Day");

                int rb_index = rg_category.getCheckedRadioButtonId();
                String category = "";
                if (rb_index == R.id.rb_newDayM_Beginning) {
                    category = "Beginning";
                } else {
                    if (rb_index == R.id.rb_newDayM_Midday)
                        category = "Midday";
                    else {
                        if (rb_index == R.id.rb_newDayM_End) {
                            category = "End of day";
                        } else {
                            if (rb_index == R.id.rb_newDayM_Anytime)
                                category = "Anytime";
                        }
                    }
                }
                newMessage.setCategory(category);
                rb_index = rg_achievementLevel.getCheckedRadioButtonId();
                if (rb_index == R.id.rb_newDayM_Low) {
                    newMessage.setAchievement(MessagesManager.Achievement.Low);
                } else {
                    if (rb_index == R.id.rb_newDayM_Moderate)
                        newMessage.setAchievement(MessagesManager.Achievement.Moderate);
                    else if (rb_index == R.id.rb_newDayM_High)
                        newMessage.setAchievement(MessagesManager.Achievement.High);
                    else
                        newMessage.setAchievement(MessagesManager.Achievement.Full);
                }

                newMessage.setContent(et_content.getText().toString());

                if (editing) {
                    MessageDAO.getInstance().updateMessage(toEdit, newMessage);
                } else {
                    if (MessageDAO.getInstance().addMessage(newMessage))
                        Log.d("add", "message added");
                    else {
                        Log.d("add", "message not added");
                    }
                }
                finish();
                Intent intent = new Intent(NewDayMessage.this, MessagesManagerNoCate.class);
                startActivity(intent);

            }

            ;
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

}
