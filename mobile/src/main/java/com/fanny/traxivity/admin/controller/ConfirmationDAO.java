package com.fanny.traxivity.admin.controller;

import com.fanny.traxivity.admin.model.Confirmation;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Alexandre on 25/05/2017.
 */

public class ConfirmationDAO {

    protected static DatabaseReference mDatabase;
    protected static FirebaseDatabase mFirebaseInstance;

    private static ConfirmationDAO INSTANCE = null;

    private ConfirmationDAO() {
        mFirebaseInstance = FirebaseDatabase.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("confirmation"); //gets the reference of the node 'quotes'
    }

    public final static ConfirmationDAO getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ConfirmationDAO();
        }
        return INSTANCE;
    }

    public void addConfirmation(Confirmation confirmation){
        if(confirmation != null){
            String confirmationType = confirmation.getType();
            String confirmationID = mDatabase.child(confirmationType).push().getKey();
            try{
                mDatabase.child(confirmationType).child(confirmationID).setValue(confirmation);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }

        }
    }

    public void getConfirmation(final String confirmationType, final TaskCompletionSource<ArrayList<Confirmation>> confirmationsGetter){
        final ArrayList<Confirmation> confirmationList = new ArrayList<>();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(confirmationType)){
                    for(DataSnapshot D : dataSnapshot.child(confirmationType).getChildren()){
                        confirmationList.add(D.getValue(Confirmation.class));
                    }
                }
                confirmationsGetter.setResult(confirmationList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void deleteConfirmation(final Confirmation confirmation){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(confirmation.getType())){
                    for(DataSnapshot D : dataSnapshot.child(confirmation.getType()).getChildren()){
                        Confirmation confToCheck = D.getValue(Confirmation.class);
                        if(confToCheck.getContent().equals(confirmation.getContent())){
                            mDatabase.child(confirmation.getType()).child(D.getKey()).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
