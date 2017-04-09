package com.fanny.traxivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SelectActivity extends WearableActivity {

    /**
     * The name of the activity selected. It is changing with the user selection.
     *
     * @see MainActivity#onActivityResult(int, int, Intent)
     */
    private String radiobutton = "";

    /**
     * Key to save the activity selected in the SharedPreferences.
     *
     * @see SelectActivity#onCreate(Bundle)
     */
    final String KEY_SAVED_RADIO_BUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";

    /**
     * Index of the saved radiobutton.
     *
     * This index is save in the SharedReferences.
     *
     * @see SelectActivity#onCreate(Bundle
     * @see SensorService
     * @see MainActivity#onActivityResult(int, int, Intent)
     * @see MainActivity#onCreate(Bundle)
     * @see MainActivity#onResume()
     */
    private int checkedIndex;

    /**
     * Active radio button in the RadioGroup.
     *
     * @see SelectActivity#onCreate(Bundle)
     */
    private RadioButton rb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activity);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

                SharedPreferences sharedPreferences = getSharedPreferences("RadioActivity", MODE_PRIVATE);
                int savedRadioIndex = sharedPreferences.getInt(KEY_SAVED_RADIO_BUTTON_INDEX, 0);
                RadioButton savedCheckedRadioButton = (RadioButton)radioGroup.getChildAt(savedRadioIndex);
                savedCheckedRadioButton.setChecked(true);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // checkedId is the RadioButton selected

                        rb = (RadioButton) findViewById(checkedId);

                        checkedIndex = radioGroup.indexOfChild(rb);


                    }
                });

                Button btn_validate = (Button) findViewById(R.id.btn_validate);
                if (btn_validate != null) {
                    btn_validate.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            if (rb != null) {

                                SavePreferences(KEY_SAVED_RADIO_BUTTON_INDEX, checkedIndex);

                                if (rb.getText().equals("Blank")){

                                    SavePreferences("SAVED_RADIO_BUTTON_NAME", "");

                                }else {

                                    SavePreferences("SAVED_RADIO_BUTTON_NAME", (String) rb.getText());
                                    radiobutton = (String) rb.getText();

                                }
                            }

                            Intent result = new Intent();
                            result.putExtra("checkbox", radiobutton);
                            setResult(RESULT_OK, result);
                            finish();

                        }

                    });
                }

                Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
                if (btn_cancel != null) {
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            finish();

                        }

                    });
                }

            }

        });

    }


    /**
     * Save the key of the RadioButton selected.
     *
     * Save the index of the RadioButton selected in the SharedPreferences.
     *
     * @param key
     *          The key to save the index of the RadioButton.
     * @param value
     *          The index of the RadioButton to save.
     *
     * @see SelectActivity#onCreate(Bundle)
     */
    private void SavePreferences(String key, int value){
        SharedPreferences sharedPreferences = getSharedPreferences("RadioActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Save the name of the RadioButton selected.
     *
     * Save the name of the RadioButton selected in the SharedPreferences.
     *
     * @param key
     *          The key to save the name of the RadioButton.
     * @param value
     *          The name of the RadioButton to save.
     *
     * @see SelectActivity#onCreate(Bundle)
     * @see SensorService#onSensorChanged(SensorEvent)
     */
    private void SavePreferences(String key, String value){
        SharedPreferences sharedPreferences = getSharedPreferences("RadioActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
