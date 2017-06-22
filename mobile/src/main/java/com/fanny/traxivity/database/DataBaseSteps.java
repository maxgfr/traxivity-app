package com.fanny.traxivity.database;

import io.realm.Realm;

/**
 * Created by maxime on 6/22/2017.
 */

public class DataBaseSteps {

    private Realm realm;

    public void insertNew(DbSteps mySteps) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        DbSteps realmActivity = realm.copyToRealm(mySteps);
        realm.commitTransaction();
    }
}
