package com.fanny.traxivity.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by huextrat on 24/04/2017.
 */

public class ActivityManager {
    public void insertActivity(Realm realm, Date startTime, double duration, String activity) {
        realm.beginTransaction();
        TActivity newActivity = realm.createObject(TActivity.class);
        newActivity.setStartTime(startTime);
        newActivity.setDuration(duration);
        newActivity.setActivity(activity);
        realm.commitTransaction();
    }

    public List<TActivity> getDay(Realm realm, Date wantedDate) {
        Date d = new Date();
        String sCurrentTime  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(d);
        String sDataTime  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(wantedDate);
        RealmResults<TActivity> results = realm.where(TActivity.class).equalTo(sCurrentTime, sDataTime).findAll();
        return realm.copyFromRealm(results);
    }

    public List<TActivity> selectAll(Realm realm) {
        return realm.where(TActivity.class).findAll();
    }
}
