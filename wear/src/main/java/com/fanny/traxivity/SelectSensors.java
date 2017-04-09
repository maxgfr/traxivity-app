package com.fanny.traxivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectSensors extends WearableActivity {

    /**
     * The key to get the state of the sensors in the SharedPreferences.
     *
     * @see SelectSensors#onCreate(Bundle)
     * @see MainActivity#onCreate(Bundle)
     * @see MainActivity#onResume()
     */
    public static final String PREFS_NAME = "CaptorsEnabled";

    /**
     * The ArrayList to use the Csensor.
     *
     * The ArrayList gets by with the intent.
     *
     * @see SelectSensors#onCreate(Bundle)
     * @see MainActivity#onCreate(Bundle)
     * @see MainActivity#onResume()
     * @see MainActivity#initSensors()
     * @see Csensor
     */
    private ArrayList<Csensor> Csensor;

    /**
     * ListView to display the sensors in a list with checkbox
     *
     * @see SelectSensors#onCreate(Bundle)
     * @see CustomAdapterSensors
     */
    private ListView mListView;

    /**
     * The button to cancel the activity.
     *
     * Quit the activity without saving the choice of the sensors.
     *
     * @see SelectSensors#onCreate(Bundle)
     */
    private Button mButtonCancel = null;

    /**
     * The button to validate.
     *
     * Validate the choice of the sensors and quit the activity.
     *
     * @see MainActivity#onCreate(Bundle)
     * @see SelectActivity
     */
    private Button mButtonValidate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sensors);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                Csensor = getIntent().getParcelableArrayListExtra("Sensors");

                mListView = (ListView) stub.findViewById(R.id.listView);

                CustomAdapterSensors CAdapter = new CustomAdapterSensors(SelectSensors.this, Csensor);

                if (mListView != null) {
                    mListView.setAdapter(CAdapter);
                }

                mButtonCancel = (Button) stub.findViewById(R.id.btn_cancel);
                mButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();

                    }
                });

                mButtonValidate = (Button) stub.findViewById(R.id.btn_validate);
                mButtonValidate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (int i = 0; i < Csensor.size(); i++) {

                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(Csensor.get(i).getName(), Csensor.get(i).isEnabled());
                            editor.apply();

                        }

                        finish();

                    }
                });

            }

        });

    }

}
