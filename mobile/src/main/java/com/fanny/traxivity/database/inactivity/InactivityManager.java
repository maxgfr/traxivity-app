package com.fanny.traxivity.database.inactivity;


import com.fanny.traxivity.database.activity.DbActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class InactivityManager {

    private Realm realm;

    public void insertInactivity(DbInactivity myInactivity) {

        realm = Realm.getDefaultInstance();

        DbInactivity newInactivity = new DbInactivity(myInactivity.getStartTime(), myInactivity.getNbHours(), myInactivity.getNbMinutes());

        realm.beginTransaction();
        DbInactivity realmInactivity = realm.copyToRealm(newInactivity);
        realm.commitTransaction();
    }

    public double getTotalInactivityDay(Date wantedDate) {
        double result = 0;
        realm = Realm.getDefaultInstance();
        RealmResults<DbInactivity> results = realm.where(DbInactivity.class).equalTo("id",wantedDate.getDate()).findAll();
        for(DbInactivity inactivity : results){
            result = result+(inactivity.getNbHours()*3600)+(inactivity.getNbMinutes()*60);
        }
        return result;
    }

    public DbInactivity getInactivityDay(Date wantedDate) {
        realm = Realm.getDefaultInstance();
        DbInactivity results = realm.where(DbInactivity.class).equalTo("id",wantedDate.getDate()).findFirst();
        return realm.copyFromRealm(results);
    }
}
