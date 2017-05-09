package com.fanny.traxivity.Database;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.fanny.traxivity.Model.SaveLastActivity.lastActivity;

/**
 * Created by huextrat on 27/04/2017.
 */

public class ActivityManager {
    private GenericTypeIndicator<List<DbActivity>> typeIndicator = new GenericTypeIndicator<List<DbActivity>>() {};
    private List<DbActivity> listActivity;

    private DatabaseReference mDatabase;

    private DateFormat formatterDate = new SimpleDateFormat ("dd-MM-yyyy", Locale.getDefault());
    private DateFormat formatterHour = new SimpleDateFormat ("HH-mm-ss", Locale.getDefault());
    private List<String> resultDates = new ArrayList<>();

    public void insertActivity(DbActivity myActivity) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(currentUser).child("Activity").child(formatterDate.format(myActivity.getStartTime()))
                .child(String.valueOf(myActivity.getStartTime().getTime())).setValue(myActivity);
    }

     public List<DbActivity> getDay(final String wantedDate){
        //listActivity.clear();
         mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
         final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
         mDatabase.child(currentUser).child("Activity").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 for (DataSnapshot child: dataSnapshot.getChildren()) {
                     if(child.getKey().equals(wantedDate)){
                         for (DataSnapshot postSnapshot: child.getChildren()) {
                             listActivity = postSnapshot.getValue(typeIndicator);

                             listActivity.add(postSnapshot.getValue(DbActivity.class));
                         }
                     }
                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
         Log.d("test", String.valueOf(listActivity));
         return listActivity;
     }

    public List<DbActivity> getRangeDay(String startDate, String endDate) throws ParseException {
        listActivity.clear();
        Date dStartDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(startDate);
        Date dEndDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(endDate);

        Calendar start = Calendar.getInstance();
        start.setTime(dStartDate);
        Calendar end = Calendar.getInstance();
        end.setTime(dEndDate);
        end.add(Calendar.DAY_OF_YEAR, 1);

        while(start.before(end)){
            String newDate  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(start.getTime());
            resultDates.add(newDate);
            start.add(Calendar.DAY_OF_YEAR, 1);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child(currentUser).child("Activity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    for(int i=0;i<resultDates.size();i++){
                        if(child.getKey().equals(resultDates.get(i))){
                            listActivity.add(child.getValue(DbActivity.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return listActivity;
    }

    public void addDurationLast(DbActivity myActivity){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(currentUser).child("Activity").child(formatterDate.format(lastActivity.getStartTime())).child(String.valueOf(lastActivity.getStartTime().getTime())).setValue(myActivity);
    }
}
