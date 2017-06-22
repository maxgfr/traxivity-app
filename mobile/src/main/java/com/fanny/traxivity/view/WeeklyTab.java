package com.fanny.traxivity.view;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fanny.traxivity.database.goal.DbGoal;
import com.fanny.traxivity.database.goal.GoalManager;
import com.fanny.traxivity.database.inactivity.InactivityManager;
import com.fanny.traxivity.database.stepsManagerBeta.StepsManager;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */
public class WeeklyTab extends Fragment {
    private StepsManager managerSteps;
    private BarDataSet set;
    private Date dateImpl;
    private Map<Integer, Integer> mapStepsDayByHour;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.weekly_tab,container,false);
        // DonutProgress weeklyCircle = (DonutProgress) v.findViewById(R.id.circle_progress_week);
        //TextView weeklyGoalTv = (TextView) v.findViewById(R.id.goal_weekly);
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
        /*HorizontalBarChart graphChart2 = (HorizontalBarChart) v.findViewById(R.id.barChart2);
        graphChart2.setScaleEnabled(false);
        graphChart2.setDragEnabled(false);
        graphChart2.setPinchZoom(false);
        graphChart2.getDescription().setEnabled(false);
        XAxis xAxis2 = graphChart2.getXAxis();
        xAxis2.setAxisMaximum(6f);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setDrawGridLines(false);
        graphChart2.getAxisLeft().setDrawGridLines(false);
        YAxis yAxisR2 = graphChart2.getAxisRight();
        yAxisR2.setDrawLabels(false); // no axis labels
        yAxisR2.setDrawAxisLine(false); // no axis line
        yAxisR2.setDrawGridLines(false); // no grid lines
        yAxisR2.setDrawZeroLine(true); // draw a zero line*/
        //ActivityManager managerActivity = new ActivityManager();
        managerSteps = new StepsManager();
        final GoalManager managerGoal = new GoalManager();
        final InactivityManager managerInactivity = new InactivityManager();
        final DbGoal dailyGoalSteps = managerGoal.goalStepsDaily(currentDate);
        final DbGoal dailyGoalDuration = managerGoal.goalDurationDaily(currentDate);
        final List<BarEntry> entries = new ArrayList<>();
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        dateImpl = c.getTime();

        String[] dataXAxis = new String[]{
                "Mon","Tue","Wed","Thu"," Fri","Sat","Sun"
        };
        MyXAxisValueFormatterDays xValueFromatter = new MyXAxisValueFormatterDays(dataXAxis);
        xAxis.setValueFormatter(xValueFromatter);
        // BarDataSet set2;
        float inactivityDuration;

        // List<BarEntry> entries2 = new ArrayList<>();
        if (dailyGoalSteps != null) {
            float stepsNumber = (float) dailyGoalSteps.getStepsNumber();
           /* weeklyGoalTv.setText(Integer.toString(weeklyGoalSteps.getStepsNumber()) + " steps");
            weeklyCircle.setProgress(managerGoal.goalStatusStepsWeekly(currentDate, managerActivity.getTotalStepsDay(currentDate)));*/
            LimitLine limitLine = new LimitLine(stepsNumber, "Goal");
            limitLine.setLineColor(Color.GREEN);
            yAxisL.addLimitLine(limitLine);
            yAxisR.setAxisMaximum(stepsNumber*1.5f);
            yAxisL.setAxisMaximum(stepsNumber*1.5f);
            for(int i=0;i<7;i++) {
                int total = 0;
                mapStepsDayByHour = managerSteps.getTotalStepsDayByHours(dateImpl);
                for(Map.Entry<Integer, Integer> entry : mapStepsDayByHour.entrySet()){
                    total = total + entry.getValue();
                }
                entries.add(new BarEntry((float) i, (float) total));
               /* inactivityDuration = (float) managerInactivity.getTotalInactivityDay(dateImpl);
                inactivityDuration = inactivityDuration/3600f;
                entries2.add(new BarEntry((float) i, inactivityDuration));*/
                dateImpl = DateUtil.addDays(dateImpl,1);
            }
            set = new BarDataSet(entries, "Steps");
            //  set2 = new BarDataSet(entries2, "Inactivity");
        }
        else if(dailyGoalDuration != null){
            float timeDuration = (float) dailyGoalDuration.getDuration();
            timeDuration = timeDuration/3600f;
           /* weeklyGoalTv.setText(Double.toString(weeklyGoalDuration.getDuration()) + " seconds");
            weeklyCircle.setProgress(managerGoal.goalStatusDurationWeekly(currentDate, managerActivity.getTotalActivityDay(currentDate)));*/
            LimitLine limitLine = new LimitLine(timeDuration, "Goal");
            limitLine.setLineColor(Color.GREEN);
            yAxisL.addLimitLine(limitLine);
            yAxisR.setAxisMaximum(timeDuration*1.5f);
            yAxisL.setAxisMaximum(timeDuration*1.5f);
            float duration;
            for(int i=0;i<7;i++) {
                duration = (float) managerSteps.getTotalStepsDay(dateImpl);
                duration = duration/3600f; //TO get Hours of activity
                entries.add(new BarEntry((float) i, duration));
                inactivityDuration = (float) managerInactivity.getTotalInactivityDay(dateImpl);
                inactivityDuration = inactivityDuration/3600f;
                //entries2.add(new BarEntry((float) i, inactivityDuration));
                dateImpl = DateUtil.addDays(dateImpl,1);
            }
            set = new BarDataSet(entries, "Time");
            // set2 = new BarDataSet(entries2, "Inactivity");
        }
        else {
            // weeklyGoalTv.setText("No goal set");
            set = new BarDataSet(entries, "Activity");
            // set2 = new BarDataSet(entries2, "Inactivity");
            yAxisR.setAxisMaximum(500f);
            yAxisL.setAxisMaximum(500f);        }
      /*  set2.setColor(getResources().getColor(R.color.red));
        BarData data2 = new BarData(set2);
        data2.setBarWidth(0.6f); // set custom bar width
        graphChart2.setData(data2);
        graphChart2.invalidate();*/
        BarData data = new BarData(set);
        data.setBarWidth(0.6f); // set custom bar width
        graphChart.setData(data);
        graphChart.invalidate();
        return v;
    }
}