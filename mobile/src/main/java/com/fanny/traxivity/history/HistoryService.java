package com.fanny.traxivity.history;

/**
 * Created by maxime on 6/22/2017.
 */

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by maxime on 14-Jun-17.
 */

public class HistoryService {

    private static HistoryService INSTANCE = null;

    private HistoryService() {
    }

    public static synchronized HistoryService getInstance() {
        if (INSTANCE == null)
        { 	INSTANCE = new HistoryService();
        }
        return INSTANCE;
    }

    public void displayMonth(GoogleApiClient[] mClient) {
        GoogleApiClient mApiClient = mClient[0];
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTime = cal.getTimeInMillis();

        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        StepsManager manager = StepsManager.getInstance();
        int i = 0;

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult readResult = Fitness.HistoryApi.readData(mApiClient, readRequest).await(1, TimeUnit.MINUTES);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        for (Bucket bucket : readResult.getBuckets()){
            //List<DataSet> dataSets = bucket.getDataSets();
            //for (DataSet dataSet:dataSets){
            DataSet dataSet = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
            boolean result = false;
            if (DataType.AGGREGATE_STEP_COUNT_DELTA.equals(dataSet.getDataType()))
                result = true;
            System.out.println("DataSet Type: "+result);
            for (DataPoint dataPoint:dataSet.getDataPoints()){
                System.out.println(format.format(new Date(dataPoint.getStartTime(TimeUnit.MILLISECONDS))));
                System.out.println(format.format(new Date(dataPoint.getEndTime(TimeUnit.MILLISECONDS))));
                for(Field field:dataPoint.getDataType().getFields()){
                    map.put(i, dataPoint.getValue(field).asInt());
                    Log.e("History", "\tField: " + field.getName() +
                            " Value: " + dataPoint.getValue(field));
                    i++;
                }
            }
            System.out.println();
        }

        manager.setStepPerDayOneMonth(map);

    }

    public void displayLastWeeksData(GoogleApiClient[] mClient) {
        GoogleApiClient mApiClient = mClient[0];
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mApiClient, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
    }

    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        StepsManager manager = StepsManager.getInstance();
        int i = 0;

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                map.put(i, dp.getValue(field).asInt());
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                i++;
            }
        }

        manager.setStepPerDayOneWeek(map);
    }

}
