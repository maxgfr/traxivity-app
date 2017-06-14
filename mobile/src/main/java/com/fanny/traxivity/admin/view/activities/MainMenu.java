package com.fanny.traxivity.admin.view.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.fanny.traxivity.MainActivity;
import com.fanny.traxivity.R;

public class MainMenu extends AppCompatActivity {
    protected static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = this;

        Button bt_activitiesManager = (Button)findViewById(R.id.bt_activities);
        Button bt_messagesManager = (Button)findViewById(R.id.bt_messages);
        Button bt_informationManager = (Button)findViewById(R.id.bt_information);
        Button bt_confirmationManager = (Button)findViewById(R.id.bt_confirmation);
        Button bt_messagesNoCate = (Button)findViewById(R.id.bt_messagesNoCate);




        bt_activitiesManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityConversionActivity = new Intent(MainMenu.this, ConversionManager.class);
                startActivity(activityConversionActivity);
            }
        });

        bt_messagesManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messagesManagerActivity = new Intent(MainMenu.this, MessagesManager.class);
                startActivity(messagesManagerActivity);
            }
        });

        bt_informationManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent informationManagerActivity = new Intent(MainMenu.this, InformationManager.class);
                startActivity(informationManagerActivity);
            }
        });

        bt_confirmationManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent confirmationManagerActivity = new Intent(MainMenu.this, ConfirmationManager.class);
                startActivity(confirmationManagerActivity);
            }
        });

        bt_messagesNoCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewActivityActivity = new Intent(MainMenu.this, MessagesManagerNoCate.class);
                startActivity(NewActivityActivity);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        return true;
    }
}
