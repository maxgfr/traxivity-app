package com.fanny.traxivity.database.activity;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.drive.events.ChangeListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class ActivityManager {
    private Realm realm;

    public void insertActivity(DbActivity myActivity) {
        List<DbActivity> lastAddedActivityList = new ArrayList<>();
        DbActivity lastAddedActivity;

        realm = Realm.getDefaultInstance();

        if(!getAllActivityDay(new Date()).isEmpty()){
            lastAddedActivityList = getAllActivityDay(new Date());
        }
        else {
            lastAddedActivityList.add(new DbActivity(new Date(),"Unknown",0));
        }
        lastAddedActivity = lastAddedActivityList.get(0);

        if(myActivity.isSpecialActivity()){
            DbActivity newActivity = new DbActivity(myActivity.getStartTime(), myActivity.getEndTime(), myActivity.getActivity(),myActivity.getNbSteps());

            realm.beginTransaction();
            DbActivity realmActivity = realm.copyToRealm(newActivity);
            realm.commitTransaction();
        }
        else if(!myActivity.getActivity().equals(lastAddedActivity.getActivity())) {
            Calendar cal = Calendar.getInstance();
            long sous = myActivity.getStartTime().getTime() - 5000;
            cal.setTimeInMillis(sous);
            myActivity.setStartTime(cal.getTime());
            DbActivity newActivity = new DbActivity(myActivity.getStartTime(), myActivity.getActivity(),myActivity.getNbSteps());

            realm.beginTransaction();
            DbActivity realmActivity = realm.copyToRealm(newActivity);
            realm.commitTransaction();
        }
        else if(myActivity.getStartTime().getHours() == lastAddedActivity.getStartTime().getHours()
                && myActivity.getEndTime().getHours() == lastAddedActivity.getEndTime().getHours()
                && myActivity.getActivity().equals(lastAddedActivity.getActivity())) {

            DbActivity updateActivity = new DbActivity(lastAddedActivity.getStartTime(), myActivity.getActivity(),lastAddedActivity.getNbSteps()+myActivity.getNbSteps());

            updateActivity.setEndTime(myActivity.getEndTime());
            updateActivity.setDuration((myActivity.getEndTime().getTime()-lastAddedActivity.getStartTime().getTime())/1000);

            realm.beginTransaction();
            removeLastActivity();
            DbActivity realmActivity = realm.copyToRealm(updateActivity);
            realm.commitTransaction();
        }
        else if(myActivity.getStartTime().getHours() != lastAddedActivity.getEndTime().getHours() || myActivity.getStartTime().getHours() != lastAddedActivity.getStartTime().getHours()){
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastAddedActivity.getStartTime());
            cal.add(Calendar.HOUR, 1);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date endTime = cal.getTime();
            cal.clear();

            lastAddedActivity.setEndTime(endTime);
            lastAddedActivity.setDuration((endTime.getTime()-lastAddedActivity.getStartTime().getTime())/1000);

            realm.beginTransaction();
            removeLastActivity();
            DbActivity realmActivityLast = realm.copyToRealm(lastAddedActivity);
            realm.commitTransaction();

            DbActivity newActivity = new DbActivity(myActivity.getStartTime(), myActivity.getActivity(),myActivity.getNbSteps());

            newActivity.setStartTime(endTime);
            newActivity.setDuration((newActivity.getEndTime().getTime()-endTime.getTime())/1000);

            realm.beginTransaction();
            DbActivity realmActivity = realm.copyToRealm(newActivity);
            realm.commitTransaction();
        }
        else {
            return;
        }
    }

    /**
     *
     * @param wantedDate the date of the day we want to restore data
     * @return
     */
    public List<DbActivity> getAllActivityDay(Date wantedDate) {
        realm = Realm.getDefaultInstance();
        RealmResults<DbActivity> results = realm.where(DbActivity.class).equalTo("id",wantedDate.getDate()).findAllSorted("startTime", Sort.DESCENDING);
        return realm.copyFromRealm(results);
    }

    public Map<Integer, DbActivity> getAllActivityDayByHours(Date wantedDate) {
        Map<Integer, DbActivity> activityByHours = new HashMap<>();
        realm = Realm.getDefaultInstance();
        List<DbActivity> results = getAllActivityDay(wantedDate);
        for(DbActivity activity : results){
            activityByHours.put(activity.getHoursRange(),activity);
        }
        return activityByHours;
    }

    /**
     *
     * @param beginningDate From this date
     * @param endDate To this date
     * @return
     */
    public List<DbActivity> getAllActivityRangeDay(Date beginningDate, Date endDate) {
        realm = Realm.getDefaultInstance();
        RealmResults<DbActivity> results = realm.where(DbActivity.class).greaterThanOrEqualTo("id", beginningDate.getDate()).lessThanOrEqualTo("id",endDate.getDate()).findAllSorted("startTime", Sort.DESCENDING);
        return realm.copyFromRealm(results);
    }

    /**
     *
     * @param date The date of the day we want to restore the total inactivity
     * @return
     */
    public double getTotalInactivityDay(Date date){
        double result = 0;
        realm = Realm.getDefaultInstance();
        List<DbActivity> listDayActivity = getAllActivityDay(date);
        for(DbActivity activity : listDayActivity){
            if(activity.getActivity().equals("Inactive")){
                result = result + activity.getDuration();
            }
        }
        return result;
    }

    /**
     *
     * @param date The date of the day we want to restore the total activity
     * @return
     */
    public double getTotalActivityDay(Date date){
        double result = 0;
        realm = Realm.getDefaultInstance();
        List<DbActivity> listDayActivity = getAllActivityDay(date);
        for(DbActivity activity : listDayActivity){
            if(!activity.getActivity().equals("Inactive")){
                result = result + activity.getDuration();
            }
        }
        return result;
    }

    public Map<Integer, Integer> getTotalTimeDayByHours(Date wantedDate) {
        Map<Integer, Integer> timeDayByHours = new HashMap<>();

        realm = Realm.getDefaultInstance();
        List<DbActivity> results = getAllActivityDay(wantedDate);

        int lastActivityHoursRange = 999;
        int totalTime = 0;
        int listSize = results.size();

        for(DbActivity myActivity : results) {
            if (!myActivity.getActivity().equals("Inactive")) {
                if(results.size() == 1){
                    timeDayByHours.put(results.get(0).getHoursRange(), results.get(0).getNbSteps());
                }
                if (lastActivityHoursRange == myActivity.getHoursRange() || lastActivityHoursRange == 999) {
                    totalTime = totalTime + (int)myActivity.getDuration();
                    lastActivityHoursRange = myActivity.getHoursRange();
                    listSize = listSize - 1;
                    if(listSize == 0){
                        timeDayByHours.put(lastActivityHoursRange, totalTime);
                    }
                } else {
                    timeDayByHours.put(lastActivityHoursRange, totalTime);
                    lastActivityHoursRange = myActivity.getHoursRange();
                    totalTime = (int) myActivity.getDuration();
                    listSize = listSize - 1;
                    if(listSize == 0){
                        timeDayByHours.put(lastActivityHoursRange, totalTime);
                    }
                }
            }
        }
        return timeDayByHours;
    }

    public int getTotalStepsDay(Date date){
        int result = 0;
        realm = Realm.getDefaultInstance();
        List<DbActivity> listDayActivity = getAllActivityDay(date);
        for(DbActivity activity : listDayActivity){
            if(!activity.getActivity().equals("Inactive")){
                result = result + activity.getNbSteps();
            }
        }
        return result;
    }

    public Map<Integer, Integer> getTotalStepsDayByHours(Date wantedDate) {
        Map<Integer, Integer> stepsDayByHours = new HashMap<>();

        List<DbActivity> results = getAllActivityDay(wantedDate);

        int lastActivityHoursRange = 999;
        int totalNbSteps = 0;
        int listSize = results.size();

        if(results.size() == 1){
            stepsDayByHours.put(results.get(0).getHoursRange(), results.get(0).getNbSteps());
        }
        else {
            for (DbActivity myActivity : results) {
                if (lastActivityHoursRange == myActivity.getHoursRange() || lastActivityHoursRange == 999) {
                    totalNbSteps = totalNbSteps + myActivity.getNbSteps();
                    lastActivityHoursRange = myActivity.getHoursRange();
                    listSize = listSize - 1;
                    if(listSize == 0){
                        stepsDayByHours.put(lastActivityHoursRange, totalNbSteps);
                    }
                } else {
                    stepsDayByHours.put(lastActivityHoursRange, totalNbSteps);
                    lastActivityHoursRange = myActivity.getHoursRange();
                    totalNbSteps = myActivity.getNbSteps();
                    listSize = listSize - 1;
                    if(listSize == 0){
                        stepsDayByHours.put(lastActivityHoursRange, totalNbSteps);
                    }
                }
            }
        }
        Log.d("test",stepsDayByHours.toString());
        return stepsDayByHours;
    }

    /**
     *
     * @param date The date of the day we want to restore the data
     * @param hoursRange Hours range like "1213" = 12h to 13h. Format 24h
     * @return
     */
    public List<DbActivity> getActivityThisHours(Date date, int hoursRange){
        List<DbActivity> activityThisHours = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        List<DbActivity> listDayActivity = getAllActivityDay(date);
        for(DbActivity activity : listDayActivity){
            if(activity.getHoursRange() != hoursRange){
                activityThisHours.add(activity);
            }
        }
        return activityThisHours;
    }

    /**
     * Remove the latest activity stored
     */
    public void removeLastActivity(){
        realm.where(DbActivity.class).findAllSorted("startTime",Sort.DESCENDING).deleteFirstFromRealm();
    }
}
