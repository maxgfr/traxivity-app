package com.fanny.traxivity.admin.controller;


import com.fanny.traxivity.model.ActivityToSteps;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Alexandre on 16/05/2017.
 */

public class ActivityConversionDAO {

    protected static DatabaseReference mDatabase;
    protected static FirebaseDatabase mFirebaseInstance;

    private static ActivityConversionDAO INSTANCE = null;

    private ActivityConversionDAO() {
        mFirebaseInstance = FirebaseDatabase.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("activities conversion"); //gets the reference of the node 'quotes'
    }

    public final static ActivityConversionDAO getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ActivityConversionDAO();
        }
        return INSTANCE;
    }

    public void addActivityConversion(ActivityToSteps activityToSteps){
        if(activityToSteps != null){
            String activityName = activityToSteps.getActivityName();
            try{
                mDatabase.child(activityName).setValue(activityToSteps);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void getConversionList(final TaskCompletionSource<ArrayList<ActivityToSteps>> conversionsGetter){
        final ArrayList<ActivityToSteps> conversionList = new ArrayList<>();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot D : dataSnapshot.getChildren()){
                    conversionList.add(D.getValue(ActivityToSteps.class));
                }
                conversionsGetter.setResult(conversionList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getConversion(final TaskCompletionSource<Float> conversionGetter, final String activityName){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(activityName)){
                    conversionGetter.setResult((dataSnapshot.child(activityName).getValue(ActivityToSteps.class)).getNumberStepsPerMinute());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void updateConversion(final String activityName, final float newStepsConversion){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActivityToSteps updatedOne = new ActivityToSteps(activityName, newStepsConversion);
                mDatabase.child(activityName).setValue(updatedOne);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}