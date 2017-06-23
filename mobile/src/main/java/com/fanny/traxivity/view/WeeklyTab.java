package com.fanny.traxivity.view;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fanny.traxivity.database.goal.DbGoal;
import com.fanny.traxivity.database.goal.GoalManager;
import com.fanny.traxivity.database.inactivity.InactivityManager;
import com.fanny.traxivity.history.StepsManager;
import com.fanny.traxivity.model.DateUtil;
import com.fanny.traxivity.model.MyXAxisValueFormatterDays;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.fanny.traxivity.R;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by huextrat <www.hugoextrat.com>.
 */
public class WeeklyTab extends Fragment {
    private StepsManager managerSteps;
    private BarDataSet set;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.weekly_tab,container,false);
        v.setTag("TAG_WEEK");


        managerSteps = StepsManager.getInstance();
        Map<Integer, Integer> mapStepsDayByHour = managerSteps.getStepPerDayOneWeek();
        List<Integer> myListActivity = new ArrayList<Integer>();
        Calendar cal = Calendar.getInstance();
        int actualDay = cal.get(Calendar.DAY_OF_WEEK);
        System.out.println("ACTUAL DAY:" +actualDay);
        switch (actualDay){
            case 1: actualDay = 6;
                break;
            case 2: actualDay = 0;
                break;
            case 3: actualDay = 1;
                break;
            case 4: actualDay = 2;
                break;
            case 5: actualDay = 3;
                break;
            case 6: actualDay = 4;
                break;
            case 7: actualDay = 5;
                break;
        }
        int j =0;
        if (!mapStepsDayByHour.isEmpty()) {
            while (j<7) {
                for(Map.Entry<Integer, Integer> entry : mapStepsDayByHour.entrySet()){
                    if (entry.getKey() == j) {
                        myListActivity.add(entry.getValue());
                        j++;
                    }
                }
            }
        }

        Collections.reverse(myListActivity);

        final Date currentDate = new Date();
        final HorizontalBarChart graphChart = (HorizontalBarChart) v.findViewById(R.id.barChart);

        graphChart.setScaleEnabled(false);
        graphChart.setDragEnabled(false);
        graphChart.setPinchZoom(false);
        graphChart.getDescription().setEnabled(false);
        XAxis xAxis = graphChart.getXAxis();
        xAxis.setAxisMaximum(6f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        final YAxis yAxisL = graphChart.getAxisLeft();
        yAxisL.setDrawGridLines(false);
        final YAxis yAxisR = graphChart.getAxisRight();
        yAxisR.setDrawLabels(false); // no axis labels
        yAxisR.setDrawAxisLine(false); // no axis line
        yAxisR.setDrawGridLines(false); // no grid lines
        yAxisR.setDrawZeroLine(true); // draw a zero line
        yAxisL.setAxisMinimum(0f);
        yAxisR.setAxisMinimum(0f);

        final GoalManager managerGoal = new GoalManager();
        final InactivityManager managerInactivity = new InactivityManager();
        final DbGoal dailyGoalSteps = managerGoal.goalStepsDaily(currentDate);
        final DbGoal dailyGoalDuration = managerGoal.goalDurationDaily(currentDate);
        final List<BarEntry> entries = new ArrayList<>();
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        String[] dataXAxis = new String[]{
                "Mon","Tue","Wed","Thu"," Fri","Sat","Sun"
        };
        MyXAxisValueFormatterDays xValueFromatter = new MyXAxisValueFormatterDays(dataXAxis);
        xAxis.setValueFormatter(xValueFromatter);

        float inactivityDuration;

        if (dailyGoalSteps != null) {
            float stepsNumber = (float) dailyGoalSteps.getStepsNumber();
            LimitLine limitLine = new LimitLine(stepsNumber, "Goal");
            limitLine.setLineColor(Color.GREEN);
            yAxisL.addLimitLine(limitLine);
            yAxisR.setAxisMaximum(stepsNumber*1.5f);
            yAxisL.setAxisMaximum(stepsNumber*1.5f);
            int temps = actualDay+1;
            for(int i=0;i<temps;i++) {
                if (!myListActivity.isEmpty()) {
                    if (i==0) {
                        entries.add(new BarEntry((float) actualDay, (float) managerSteps.getTotalStepsDay()));
                    } else {
                        entries.add(new BarEntry((float) actualDay-i, (float) myListActivity.get(i-1)));
                    }
                }
            }
            for(int i=temps;i<7;i++) {
                if (!myListActivity.isEmpty()) {
                    entries.add(new BarEntry((float) i, (float) 0));
                }
            }
            set = new BarDataSet(entries, "Steps");
        }
        else if(dailyGoalDuration != null){
            float timeDuration = (float) dailyGoalDuration.getDuration();
            timeDuration = timeDuration/3600f;
            LimitLine limitLine = new LimitLine(timeDuration, "Goal");
            limitLine.setLineColor(Color.GREEN);
            yAxisL.addLimitLine(limitLine);
            yAxisR.setAxisMaximum(timeDuration*1.5f);
            yAxisL.setAxisMaximum(timeDuration*1.5f);
            int temps = actualDay+1;
            for(int i=0;i<temps;i++) {
                if (!myListActivity.isEmpty()) {
                    if (i==0) {
                        entries.add(new BarEntry((float) actualDay, (float) managerSteps.getTotalStepsDay()));
                    } else {
                        entries.add(new BarEntry((float) actualDay-i, (float) myListActivity.get(i-1)));
                    }
                }
            }
            for(int i=temps;i<7;i++) {
                if (!myListActivity.isEmpty()) {
                    entries.add(new BarEntry((float) i, (float) 0));
                }
            }
            set = new BarDataSet(entries, "Time");
        }
        else {
            set = new BarDataSet(entries, "Activity");
            yAxisR.setAxisMaximum(500f);
            yAxisL.setAxisMaximum(500f);
        }
        BarData data = new BarData(set);
        data.setBarWidth(0.6f); // set custom bar width
        graphChart.setData(data);
        graphChart.invalidate();
        return v;
    }

    public void refresh () {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}






