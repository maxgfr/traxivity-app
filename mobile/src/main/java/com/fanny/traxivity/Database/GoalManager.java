package com.fanny.traxivity.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by extra on 27/04/2017.
 */

public class GoalManager {
    private DatabaseReference mDatabase;
    void insertGoal(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
