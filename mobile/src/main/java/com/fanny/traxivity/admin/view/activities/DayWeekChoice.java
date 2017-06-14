package com.fanny.traxivity.admin.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.fanny.traxivity.R;

/**
 * Created by extra on 06/06/2017.
 */
public class DayWeekChoice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_week_choice);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button dayButton = (Button)findViewById(R.id.bt_dayMessages);
        Button weekButton = (Button)findViewById(R.id.bt_weekMessages);

        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dayMessageIntent = new Intent(DayWeekChoice.this, NewDayMessage.class);
                startActivity(dayMessageIntent);
            }
        });

        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weekMessageIntent = new Intent(DayWeekChoice.this, NewWeekMessage.class);
                startActivity(weekMessageIntent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}