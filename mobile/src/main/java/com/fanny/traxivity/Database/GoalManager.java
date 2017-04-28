package com.fanny.traxivity.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huextrat on 27/04/2017.
 */

public class GoalManager {

    private DatabaseReference mDatabase;
    private DbGoal returnGoal;
    private DateFormat formatterDate = new SimpleDateFormat ("dd-MM-yyyy", Locale.getDefault());

    public void insertGoal(DbGoal dbGoal){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(currentUser).child("goal").setValue(dbGoal);
    }

    public DbGoal getGoal(final String beginningDate) throws ParseException {
        final Date dStartDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(beginningDate);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child(currentUser).child("goal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Date beginDate = child.getValue(DbGoal.class).getBeginningDate();
                    Date endDate = child.getValue(DbGoal.class).getEndingDate();
                    if(formatterDate.format(beginDate).equals(formatterDate.format(dStartDate)) || formatterDate.format(endDate).equals(formatterDate.format(dStartDate))){
                        returnGoal = child.getValue(DbGoal.class);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return returnGoal;
    }
}
