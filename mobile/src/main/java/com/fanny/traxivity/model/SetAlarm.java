package com.fanny.traxivity.model;

import android.content.Context;

import com.fanny.traxivity.database.dayTiming.DayTimingManager;
import com.fanny.traxivity.database.dayTiming.DbTiming;

import java.util.Calendar;

/**
 * Created by huextrat <www.hugoextrat.com>
 */

public class SetAlarm {

    public static Alarm alarm =new Alarm();
    public static Alarm alarm2=new Alarm();
    public static Alarm alarm3=new Alarm();
    private static DayTimingManager managerTiming = new DayTimingManager();
    static final int ID_ALARM_1=1;
    static final int ID_ALARM_2=2;
    static final int ID_ALARM_3=3;

    public static void setAlarmStart(Context mContext){
        DbTiming startTiming = managerTiming.getTimingStartFirst();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, startTiming.getHour());
        calendar.set(Calendar.MINUTE, startTiming.getMinute());
        calendar.set(Calendar.SECOND, 0);
        alarm.setAlarm(mContext,calendar,ID_ALARM_1);
    }

    public static void setAlarmMid(Context mContext){
        DbTiming midTiming = managerTiming.getTimingMidFirst();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, midTiming.getHour());
        calendar.set(Calendar.MINUTE, midTiming.getMinute());
        calendar.set(Calendar.SECOND, 0);
        alarm2.setAlarm(mContext,calendar,ID_ALARM_2);
    }

    public static void setAlarmEnd(Context mContext){
        DbTiming endTiming = managerTiming.getTimingEndFirst();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, endTiming.getHour());
        calendar.set(Calendar.MINUTE, endTiming.getMinute());
        calendar.set(Calendar.SECOND, 0);
        alarm3.setAlarm(mContext,calendar,ID_ALARM_3);
    }

    public static void setAlarm(Context mContext){

        DbTiming startTiming = managerTiming.getTimingStartFirst();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, startTiming.getHour());
        calendar.set(Calendar.MINUTE, startTiming.getMinute());
        calendar.set(Calendar.SECOND, 0);
        alarm.setAlarm(mContext,calendar,ID_ALARM_1);

        DbTiming midTiming = managerTiming.getTimingMidFirst();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, midTiming.getHour());
        calendar.set(Calendar.MINUTE, midTiming.getMinute());
        calendar.set(Calendar.SECOND, 0);
        alarm2.setAlarm(mContext,calendar,ID_ALARM_2);

        DbTiming endTiming = managerTiming.getTimingEndFirst();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, endTiming.getHour());
        calendar.set(Calendar.MINUTE, endTiming.getMinute());
        calendar.set(Calendar.SECOND, 0);
        alarm3.setAlarm(mContext,calendar,ID_ALARM_3);
    }
}
