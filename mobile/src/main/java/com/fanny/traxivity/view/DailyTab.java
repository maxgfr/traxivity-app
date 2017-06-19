package com.fanny.traxivity.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fanny.traxivity.database.activity.ActivityManager;
import com.fanny.traxivity.database.activity.DbActivity;
import com.fanny.traxivity.database.dayTiming.DbTiming;
import com.fanny.traxivity.database.goal.DbGoal;
import com.fanny.traxivity.database.goal.GoalManager;
import com.fanny.traxivity.database.inactivity.DbInactivity;
import com.fanny.traxivity.database.stepsManagerBeta.DbSteps;
import com.fanny.traxivity.database.stepsManagerBeta.StepsManager;
import com.fanny.traxivity.model.DateUtil;
import com.fanny.traxivity.model.ListenerService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.fanny.traxivity.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;
import io.realm.Realm;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class DailyTab extends Fragment {
    private BarDataSet set;
    private CircleProgressView dailyCircle;
    private ActivityManager managerActivity;
    private StepsManager managerSteps;
    private GoalManager managerGoal;
    private Date currentDate;
    private BroadcastReceiver broadCastNewMessage;
    private List<BarEntry> entries;
    private BarChart graphChart;
    private Map<Integer, Integer> mapStepsDayByHour;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.daily_tab, container, false);

        dailyCircle = (CircleProgressView) v.findViewById(R.id.circleView);
        dailyCircle.setBarColor(getResources().getColor(R.color.red), getResources().getColor(R.color.orange), getResources().getColor(R.color.green));

        //Button clearDb = (Button) v.findViewById(R.id.button_clear);
        TextView dailyGoalTv = (TextView) v.findViewById(R.id.goal_daily);

        currentDate = new Date();

        graphChart = (BarChart) v.findViewById(R.id.barChart);
        graphChart.setScaleEnabled(false);
        graphChart.setDragEnabled(false);
        graphChart.setPinchZoom(false);
        graphChart.getDescription().setEnabled(false);

        XAxis xAxis = graphChart.getXAxis();
        xAxis.setAxisMaximum(23f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        graphChart.getAxisLeft().setDrawGridLines(false);
        final YAxis yAxisR = graphChart.getAxisRight();
        yAxisR.setDrawLabels(false); // no axis labels
        yAxisR.setDrawAxisLine(false); // no axis line
        yAxisR.setDrawGridLines(false); // no grid lines
        yAxisR.setDrawZeroLine(true); // draw a zero line

        managerActivity = new ActivityManager();
        managerGoal = new GoalManager();
        managerSteps = new StepsManager();

        entries = new ArrayList<>();
        final DbGoal dailyGoalSteps = managerGoal.goalStepsDaily(currentDate);
        final DbGoal dailyGoalDuration = managerGoal.goalDurationDaily(currentDate);


        if (dailyGoalSteps != null) {
            broadCastNewMessage = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateCircleStepsText();

                    if(!entries.isEmpty()) {
                        entries.clear();
                    }
                    if(set != null) {
                        set.clear();
                    }
                    yAxisR.setAxisMaximum(dailyGoalSteps.getStepsNumber() * 2f);
                    Integer nbsteps;
                    Map<Integer, Integer> mapStepsDayByHour = managerSteps.getTotalStepsDayByHours(currentDate);
                    for (Integer i = 0; i < 24; i++) {
                        nbsteps = mapStepsDayByHour.get(i);

                        if (nbsteps == null) {
                            entries.add(new BarEntry((float) i, 0f));
                            Log.w("Nbsteps", i.toString());

                        } else {
                            entries.add(new BarEntry((float) i, (float) nbsteps));
                            Log.w("Nbsteps", nbsteps.toString());
                        }
                    }
                    set = new BarDataSet(entries, "Steps");
                    graphChart.invalidate();
                }
            };

            getActivity().registerReceiver(broadCastNewMessage, new IntentFilter("bcNewSteps"));

            int total = 0;
            mapStepsDayByHour = managerSteps.getTotalStepsDayByHours(currentDate);
            for(Map.Entry<Integer, Integer> entry : mapStepsDayByHour.entrySet()){
                total = total + entry.getValue();
            }

            dailyCircle.setValueAnimated(0, managerGoal.goalStatusStepsDaily(currentDate, total), 2000);
            dailyCircle.setTextMode(TextMode.TEXT);
            dailyCircle.setText(total + " steps");

            dailyCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dailyCircle.getUnit().equals(" %")) {
                        updateCircleStepsText();
                    } else {
                        updateCircleStepsPercent();
                    }
                }
            });

            dailyGoalTv.setText(dailyGoalSteps.getStepsNumber() + " steps");
            yAxisR.setAxisMaximum(dailyGoalSteps.getStepsNumber() * 2f);

            Integer nbsteps;
            mapStepsDayByHour = managerSteps.getTotalStepsDayByHours(currentDate);
            for (Integer i = 0; i < 24; i++) {
                nbsteps = mapStepsDayByHour.get(i);

                if (nbsteps == null) {
                    entries.add(new BarEntry((float) i, 0f));
                    Log.w("Nbsteps", i.toString());

                } else {
                    entries.add(new BarEntry((float) i, (float) nbsteps));
                    Log.w("Nbsteps", nbsteps.toString());
                }
            }
            set = new BarDataSet(entries, "Steps");
        } else if (dailyGoalDuration != null) {
            dailyCircle.setValueAnimated(0, managerGoal.goalStatusStepsDaily(currentDate, managerSteps.getTotalStepsDay(currentDate)), 2000);
            dailyCircle.setTextMode(TextMode.TEXT);
            dailyCircle.setText(managerActivity.getTotalStepsDay(currentDate) + " steps");
            dailyCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dailyCircle.getUnit().equals(" %")) {
                        dailyCircle.setValueAnimated(0, managerGoal.goalStatusDurationDaily(currentDate, managerActivity.getTotalActivityDay(currentDate)), 2000);
                        dailyCircle.setUnit("");
                        dailyCircle.setTextSize(100);
                        dailyCircle.setUnitVisible(false);
                        dailyCircle.setValueAnimated(0, managerGoal.goalStatusDurationDaily(currentDate, managerActivity.getTotalActivityDay(currentDate)), 2000);
                        dailyCircle.setTextMode(TextMode.TEXT);
                        dailyCircle.setText(managerActivity.getTotalStepsDay(currentDate) + " seconds");
                    } else {
                        dailyCircle.setValueAnimated(0, managerGoal.goalStatusDurationDaily(currentDate, managerActivity.getTotalActivityDay(currentDate)), 2000);
                        dailyCircle.setTextMode(TextMode.PERCENT);
                        dailyCircle.setUnitSize(200);
                        dailyCircle.setAutoTextSize(true);
                        dailyCircle.setUnit(" %");
                        dailyCircle.setUnitColor(getResources().getColor(R.color.colorPrimary));
                        dailyCircle.setUnitVisible(true);
                        dailyCircle.setUnitScale(1);
                        dailyCircle.setUnitPosition(UnitPosition.RIGHT_TOP);
                        dailyCircle.setText(String.valueOf(managerGoal.goalStatusDurationDaily(currentDate, managerActivity.getTotalActivityDay(currentDate))));
                    }
                }
            });
            dailyGoalTv.setText(dailyGoalSteps.getStepsNumber() + " seconds");

            yAxisR.setAxisMaximum((float) dailyGoalDuration.getDuration() * 2f);

            Integer activityTime;
            Map<Integer, Integer> mapTimeDayByHour = managerSteps.getTotalStepsDayByHours(currentDate);
            for (Integer i = 0; i < 24; i++) {
                activityTime = mapTimeDayByHour.get(i);

                if (activityTime == null) {
                    entries.add(new BarEntry((float) i, 0f));
                    Log.w("Nbsteps", i.toString());

                } else {
                    entries.add(new BarEntry((float) i, (float) activityTime));
                    Log.w("Nbsteps", activityTime.toString());
                }
            }

            set = new BarDataSet(entries, "Time");
        } else {
            dailyGoalTv.setText("No goal set");
            set = new BarDataSet(entries, "Activity");
            yAxisR.setAxisMaximum(10000f);
        }


       /** clearDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearDb();
            }
        });
        **/

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        graphChart.setData(data);
        graphChart.setDrawGridBackground(false);
        graphChart.invalidate();

        return v;
    }

    public void clearDb() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(DbActivity.class);
        realm.delete(DbGoal.class);
        realm.delete(DbInactivity.class);
        realm.delete(DbSteps.class);
        realm.delete(DbTiming.class);
        realm.commitTransaction();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadCastNewMessage);
    }

    public void updateCircleStepsText() {
        int total = 0;
        mapStepsDayByHour = managerSteps.getTotalStepsDayByHours(currentDate);
        for(Map.Entry<Integer, Integer> entry : mapStepsDayByHour.entrySet()){
            total = total + entry.getValue();
        }

        dailyCircle.setValueAnimated(0, managerGoal.goalStatusStepsDaily(currentDate, total), 2000);
        dailyCircle.setUnit("");
        dailyCircle.setAutoTextSize(true);
        dailyCircle.setUnitVisible(false);
        dailyCircle.setTextMode(TextMode.TEXT);
        dailyCircle.setText(total + " steps");
    }

    public void updateCircleStepsPercent() {
        int total = 0;
        mapStepsDayByHour = managerSteps.getTotalStepsDayByHours(currentDate);
        for(Map.Entry<Integer, Integer> entry : mapStepsDayByHour.entrySet()){
            total = total + entry.getValue();
        }

        dailyCircle.setValueAnimated(0, managerGoal.goalStatusStepsDaily(currentDate, total), 2000);
        dailyCircle.setTextMode(TextMode.PERCENT);
        dailyCircle.setUnitSize(200);
        dailyCircle.setAutoTextSize(true);
        dailyCircle.setUnit(" %");
        dailyCircle.setUnitColor(getResources().getColor(R.color.colorPrimary));
        dailyCircle.setUnitVisible(true);
        dailyCircle.setUnitScale(1);
        dailyCircle.setUnitPosition(UnitPosition.RIGHT_TOP);
        dailyCircle.setText(String.valueOf(managerGoal.goalStatusStepsDaily(currentDate, total)));
    }

}
