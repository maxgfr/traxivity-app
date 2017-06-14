package com.fanny.traxivity.admin.view.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fanny.traxivity.R;
import com.fanny.traxivity.admin.controller.InformationDAO;
import com.fanny.traxivity.admin.model.Information;
import com.fanny.traxivity.admin.view.InformationAdapter;
import com.fanny.traxivity.admin.view.dialogs.elementDeleteDialog;
import com.fanny.traxivity.admin.view.dialogs.elementDetailsDialog;
import com.fanny.traxivity.admin.view.dialogs.newInformationDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;

public class InformationManager extends AppCompatActivity {

    private static final String TAG = MessagesManager.class.getSimpleName();

    private Context context;

    private TaskCompletionSource<ArrayList<Information>> getInformationTask;
    private Task getInformationTaskWaiter;

    protected ArrayList<Information> informationList;
    protected Information information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = this;

        informationList = new ArrayList<>();
        information = new Information("","");

        Button bt_addInfo = (Button)findViewById(R.id.bt_addInfo);
        Button bt_searchInfo = (Button)findViewById(R.id.bt_searchInfo);
        final ListView lv_info = (ListView)findViewById(R.id.lv_information);
        final ProgressBar pb_info = (ProgressBar)findViewById(R.id.pb_info);
        pb_info.setVisibility(View.GONE);
        final RadioGroup rg_infoType = (RadioGroup)findViewById(R.id.rg_infoType);
        final TextView tv_noneInfo = (TextView) findViewById(R.id.tv_noneInfo);
        final TextView tv_listInfo = (TextView) findViewById(R.id.tv_listInfo);
        tv_noneInfo.setVisibility(View.GONE);
        tv_listInfo.setVisibility(View.GONE);


        final InformationAdapter adapter = new InformationAdapter(this, informationList);
        lv_info.setAdapter(adapter);

        bt_addInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newInformation = new newInformationDialog();
                newInformation.show(getFragmentManager(),TAG);
            }
        });

        bt_searchInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformationTask = new TaskCompletionSource<>();
                getInformationTaskWaiter = getInformationTask.getTask();

                getInformationTaskWaiter.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            tv_listInfo.setVisibility(View.VISIBLE);
                            Log.d("task", "success");
                            informationList.clear();
                            informationList.addAll((ArrayList<Information>)task.getResult());
                            if(informationList.size() == 0){
                                tv_noneInfo.setVisibility(View.VISIBLE);
                                tv_noneInfo.setText("None");
                            }
                            else
                                tv_noneInfo.setVisibility(View.GONE);
                            pb_info.setIndeterminate(false);
                            pb_info.setVisibility(View.GONE);
                            Toast.makeText(context, Integer.toString(informationList.size()) + " information found", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            Exception e = task.getException();
                            System.out.println(e.getMessage());
                        }
                    }
                });

                int rb_index = rg_infoType.getCheckedRadioButtonId();
                String type = "";
                if(rb_index == R.id.rb_activityInfo){
                    type = "Activity";
                }else{
                    if(rb_index == R.id.rb_stepsInfo)
                        type = "Steps";
                    else
                    if(rb_index == R.id.rb_inactivityInfo)
                        type = "Inactivity";
                }

                pb_info.setVisibility(View.VISIBLE);
                pb_info.setIndeterminate(true);
                InformationDAO.getInstance().getInformation(type, getInformationTask);
            }
        });

        lv_info.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Information infoClicked = adapter.getItem(position);

                DialogFragment infoDetails = elementDetailsDialog.newInstance(infoClicked);
                infoDetails.show(getFragmentManager(),TAG);
            }
        });

        lv_info.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Information infoCLicked = adapter.getItem(position);

                DialogFragment infoDelete = elementDeleteDialog.newInstance(infoCLicked);
                infoDelete.show(getFragmentManager(),TAG);

                return true;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
