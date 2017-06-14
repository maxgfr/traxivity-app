package com.fanny.traxivity.admin.view.activities;

import android.app.DialogFragment;
import android.content.Intent;
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

public class NewWeekMessage extends AppCompatActivity {

    public static TextView tv_quote;
    public static ImageButton bt_removeQuote;

    public static Message newMessage;
    protected Message toEdit;
    protected Quote editQuote;
    protected boolean editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_week_messages);

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

        Button bt_send = (Button) findViewById(R.id.bt_addWeekMessage);
        ImageButton ib_placeholder = (ImageButton)findViewById(R.id.ib_placeHolderWeek);
        ImageButton bt_newQuote = (ImageButton) findViewById(R.id.ib_quoteWeek);
        bt_removeQuote = (ImageButton) findViewById(R.id.ib_deleteQuoteWeek);
        if (newMessage.getQuote() == null) {
            bt_removeQuote.setVisibility(View.GONE);
        }
        final RadioGroup rg_achievementLevel = (RadioGroup) findViewById(R.id.rg_newWeekM_achievementLevel);
        final RadioGroup rg_category = (RadioGroup) findViewById(R.id.rg_newM_weekTime);
        final EditText et_content = (EditText) findViewById(R.id.et_messageContentWeek);
        tv_quote = (TextView) findViewById(R.id.tv_quoteWeek);
        tv_quote.setVisibility(View.GONE);

        if (editing) {
            bt_send.setText("Edit");
            switch (newMessage.getAchievement()) {
                case Low:
                    rg_achievementLevel.check(R.id.rb_newWeekM_Low);
                    break;
                case Moderate:
                    rg_achievementLevel.check(R.id.rb_newWeekM_Moderate);
                    break;
                case High:
                    rg_achievementLevel.check(R.id.rb_newWeekM_High);
                    break;
                case Full:
                    rg_achievementLevel.check(R.id.rb_newWeekM_Full);
                    break;
            }

            switch (newMessage.getCategory()) {
                case "Beginning":
                    rg_category.check(R.id.rb_newWeekM_Beginning);
                    break;
                case "Mid-week":
                    rg_category.check(R.id.rb_newWeekM_Mid);
                    break;
                case "End of Week":
                    rg_category.check(R.id.rb_newWeekM_End);
                    break;
                case "Anytime":
                    rg_category.check(R.id.rb_newWeekM_Any);
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

        ib_placeholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = et_content.getText().toString() + " <day> ";
                et_content.setText(newText);
            }
        });

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editing) {
                    toEdit = new Message(newMessage);
                    toEdit.setQuote(editQuote);
                }

                newMessage.setDayWeek("Week");

                int rb_index = rg_category.getCheckedRadioButtonId();
                String category = "";
                if (rb_index == R.id.rb_newWeekM_Beginning) {
                    category = "Beginning";
                } else {
                    if (rb_index == R.id.rb_newWeekM_Mid)
                        category = "Mid-week";
                    else {
                        if (rb_index == R.id.rb_newWeekM_End) {
                            category = "End of week";
                        } else {
                            if (rb_index == R.id.rb_newWeekM_Any)
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
                Intent intent = new Intent(NewWeekMessage.this, MessagesManagerNoCate.class);
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
