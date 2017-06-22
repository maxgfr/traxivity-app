package com.fanny.traxivity.database.goal;

import android.util.Log;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class GoalManager {
    private Realm realm;

    public void insertGoal(DbGoal myGoal) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        DbGoal realmGoal = realm.copyToRealm(myGoal);
        realm.commitTransaction();
    }

    public List<DbGoal> getGoal(Date beginningDate) {
        realm = Realm.getDefaultInstance();
        RealmResults<DbGoal> results = realm.where(DbGoal.class).equalTo("id",beginningDate.getDate()).findAllSorted("beginningDate", Sort.ASCENDING);
        return realm.copyFromRealm(results);
    }

    public List<DbGoal> getAllGoal() {
        realm = Realm.getDefaultInstance();
        RealmResults<DbGoal> results = realm.where(DbGoal.class).findAllSorted("beginningDate", Sort.ASCENDING);
        return realm.copyFromRealm(results);
    }


    public DbGoal goalStepsDaily(Date beginningDate){
        List<DbGoal> listGoal = getGoal(beginningDate);
        for(DbGoal myGoal : listGoal){
            if(myGoal.getType().equals("Steps")){
                return myGoal;
            }
        }
        return null;
    }


    public float goalStatusStepsDaily(Date beginningDate, int steps){
        List<DbGoal> listGoal = getGoal(beginningDate);
        for(DbGoal myGoal : listGoal){
            if(myGoal.getType().equals("Steps")){
                if((((double)steps/myGoal.getStepsNumber())*100) < 100){
                    return (float) (((double)steps/myGoal.getStepsNumber())*100);
                }
                else { return 100; }
            }
        }
        return 0;
    }

    public int goalStatusDurationDaily(Date beginningDate, double duration){
        List<DbGoal> listGoal = getGoal(beginningDate);
        for(DbGoal myGoal : listGoal){
            if(myGoal.getType().equals("Duration")){
                if(((duration/myGoal.getStepsNumber())*100) < 100){
                    return (int)(duration/myGoal.getStepsNumber())*100;
                }
                else { return 100; }
            }
        }
        return 0;
    }

    public DbGoal goalDurationDaily(Date beginningDate){
        List<DbGoal> listGoal = getGoal(beginningDate);
        for(DbGoal myGoal : listGoal){
            if(myGoal.getType().equals("Duration")){
                return myGoal;
            }
        }
        return null;
    }

    public void removeAllGoal(){
        if(getAllGoal() != null) {
            realm.where(DbGoal.class).findAll().deleteAllFromRealm();
        }
    }
}
