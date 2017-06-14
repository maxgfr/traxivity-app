package com.fanny.traxivity.admin.view.activities;

import android.app.DialogFragment;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.MessageDAO;
import com.fanny.traxivity.admin.model.Message;
import com.fanny.traxivity.admin.model.Quote;
import com.fanny.traxivity.admin.view.dialogs.newCategoryDialog;
import com.fanny.traxivity.admin.view.dialogs.newQuoteDialog;

import java.util.ArrayList;

public class NewMessage extends AppCompatActivity {

    private Spinner spinner_category;
    public static ArrayAdapter<String> categories_adapter;
    public static TextView tv_quote;
    public static Button bt_removeQuote;

    protected static ArrayList<String> bctCategory_list;

    public static Message newMessage;
    protected Message toEdit;
    protected Quote editQuote;
    protected boolean editing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        newMessage = getIntent().getParcelableExtra("messageToEdit");
        if(newMessage != null){
            editQuote = newMessage.getQuote();
            editing = true;
        }
        else{
            editing = false;
            newMessage = new Message();
        }

        bctCategory_list = MessagesManager.bctCategory_list;


        Button bt_send = (Button)findViewById(R.id.bt_send);
        Button bt_newCategory = (Button)findViewById(R.id.bt_add_category);
        Button bt_newQuote = (Button)findViewById(R.id.bt_newQuote);
        bt_removeQuote = (Button)findViewById(R.id.bt_removeQuote);
        if(newMessage.getQuote() == null){
            bt_removeQuote.setVisibility(View.GONE);
        }
        final RadioGroup rg_achievementLevel = (RadioGroup)findViewById(R.id.rg_achievementLevels);
        final EditText et_content = (EditText)findViewById(R.id.et_content);
        tv_quote = (TextView)findViewById(R.id.tv_qcontent);
        spinner_category = (Spinner)findViewById(R.id.spinner_category);

        categories_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bctCategory_list);
        categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_category.setAdapter(categories_adapter);

        if(editing){
            bt_send.setText("Edit");
            switch (newMessage.getAchievement()){
                case Low:
                    rg_achievementLevel.check(R.id.r_low);
                    break;
                case Moderate:
                    rg_achievementLevel.check(R.id.r_moderate);
                    break;
                case High:
                    rg_achievementLevel.check(R.id.r_high);
                    break;
                case Full:
                    rg_achievementLevel.check(R.id.r_full);
                    break;
            }

            int spinnerPosition = categories_adapter.getPosition(newMessage.getCategory());
            spinner_category.setSelection(spinnerPosition);

            if(newMessage.getQuote() != null)
                tv_quote.setText(Html.fromHtml(newMessage.getQuote().toString()));

            et_content.setText(newMessage.getContent(),TextView.BufferType.EDITABLE);
        }

        bt_newCategory.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DialogFragment categoryDialog = new newCategoryDialog();
                categoryDialog.show(getFragmentManager(), "new category");
            }

        });

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
                tv_quote.setText("None");
                tv_quote.setTextColor(Color.BLACK);
                bt_removeQuote.setVisibility(View.GONE);
            }
        });

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editing){
                    toEdit = new Message(newMessage);
                    toEdit.setQuote(editQuote);
                }
                newMessage.setCategory(spinner_category.getSelectedItem().toString());
                int rb_index = rg_achievementLevel.getCheckedRadioButtonId();
                if(rb_index == R.id.r_low){
                    newMessage.setAchievement(MessagesManager.Achievement.Low);
                }else{
                    if(rb_index == R.id.r_moderate)
                        newMessage.setAchievement(MessagesManager.Achievement.Moderate);
                    else
                    if(rb_index == R.id.r_high)
                        newMessage.setAchievement(MessagesManager.Achievement.High);
                    else
                        newMessage.setAchievement(MessagesManager.Achievement.Full);
                }

                newMessage.setContent(et_content.getText().toString());

                if(editing){
                    MessageDAO.getInstance().updateMessage(toEdit, newMessage);
                }
                else{
                    if(MessageDAO.getInstance().addMessage(newMessage))
                        Log.d("add","message added");
                    else{
                        Log.d("add","message not added");
                    }
                }
                finish();

            };
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}

