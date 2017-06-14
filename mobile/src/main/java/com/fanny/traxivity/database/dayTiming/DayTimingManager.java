package com.fanny.traxivity.database.dayTiming;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class DayTimingManager {
    private Realm realm;

    public void insertTiming(DbTiming myTiming) {
        realm = Realm.getDefaultInstance();

        if(myTiming.getName().equals("start")){
            List<DbTiming> listStart = getTimingStart();
            if(listStart.size() > 1){
                realm.beginTransaction();
                removeTimingStart();
                DbTiming realmGoal = realm.copyToRealm(myTiming);
                realm.commitTransaction();
            }
            else {
                realm.beginTransaction();
                DbTiming realmGoal = realm.copyToRealm(myTiming);
                realm.commitTransaction();
            }
        } else if(myTiming.getName().equals("mid")){
            List<DbTiming> listMid = getTimingMid();
            if(listMid.size() > 1){
                realm.beginTransaction();
                removeTimingMid();
                DbTiming realmGoal = realm.copyToRealm(myTiming);
                realm.commitTransaction();
            }
            else {
                realm.beginTransaction();
                DbTiming realmGoal = realm.copyToRealm(myTiming);
                realm.commitTransaction();
            }
        } else if(myTiming.getName().equals("end")){
            List<DbTiming> listEnd = getTimingEnd();
            if(listEnd.size() > 1){
                realm.beginTransaction();
                removeTimingEnd();
                DbTiming realmGoal = realm.copyToRealm(myTiming);
                realm.commitTransaction();
            }
            else {
                realm.beginTransaction();
                DbTiming realmGoal = realm.copyToRealm(myTiming);
                realm.commitTransaction();
            }
        }

    }

    public List<DbTiming> getTimingStart() {
        realm = Realm.getDefaultInstance();
        RealmResults<DbTiming> results = realm.where(DbTiming.class).equalTo("name","start").findAll();
        return realm.copyFromRealm(results);
    }

    public DbTiming getTimingStartFirst() {
        realm = Realm.getDefaultInstance();
        DbTiming results = realm.where(DbTiming.class).equalTo("name","start").findFirst();
        return results;
    }

    public List<DbTiming> getTimingMid() {
        realm = Realm.getDefaultInstance();
        RealmResults<DbTiming> results = realm.where(DbTiming.class).equalTo("name","mid").findAll();
        return realm.copyFromRealm(results);
    }

    public DbTiming getTimingMidFirst() {
        realm = Realm.getDefaultInstance();
        DbTiming results = realm.where(DbTiming.class).equalTo("name","mid").findFirst();
        return results;
    }

    public List<DbTiming> getTimingEnd() {
        realm = Realm.getDefaultInstance();
        RealmResults<DbTiming> results = realm.where(DbTiming.class).equalTo("name","end").findAll();
        return realm.copyFromRealm(results);
    }

    public DbTiming getTimingEndFirst() {
        realm = Realm.getDefaultInstance();
        DbTiming results = realm.where(DbTiming.class).equalTo("name","end").findFirst();
        return results;
    }

    private void removeTimingStart(){
        realm.where(DbTiming.class).equalTo("name", "start").findAll().deleteAllFromRealm();
    }
    private void removeTimingMid(){
        realm.where(DbTiming.class).equalTo("name", "mid").findAll().deleteAllFromRealm();
    }
    private void removeTimingEnd(){
        realm.where(DbTiming.class).equalTo("name", "end").findAll().deleteAllFromRealm();
    }
}
