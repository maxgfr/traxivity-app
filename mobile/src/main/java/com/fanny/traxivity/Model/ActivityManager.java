package com.fanny.traxivity.Model;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.fanny.traxivity.DbObject.DbActivity;
import com.fanny.traxivity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Created by huextrat on 27/04/2017.
 */

class ActivityManager {
    private DatabaseReference mDatabase;
    private ArrayList<DbActivity> listActivity = new ArrayList<>();

    void insertActivity(DbActivity myActivity) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String sDataDate  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(myActivity.getStartTime());
        String sDataHours  = new SimpleDateFormat("HH-mm-ss", Locale.getDefault()).format(myActivity.getStartTime());
        mDatabase.child("users").child(currentUser).child(sDataDate).child(sDataHours).setValue(myActivity);
    }

    private List<DbActivity> getDay(final String wantedDate){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if(child.getKey().equals(wantedDate)){
                        listActivity.add(child.getValue(DbActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return listActivity;
    }
}
