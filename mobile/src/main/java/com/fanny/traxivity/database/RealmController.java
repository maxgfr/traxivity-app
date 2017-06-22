package com.fanny.traxivity.database;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;

import io.realm.Realm;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {
        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {
        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }
    public static RealmController with(Context mContext){
        if(instance==null){
            Activity activity= (Activity) mContext;
            instance=new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    public void refresh() {
        realm.setAutoRefresh(true);
    }

}
