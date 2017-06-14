package com.fanny.traxivity.admin.controller;

import com.fanny.traxivity.admin.model.Information;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/05/2017.
 */

public class InformationDAO {

    protected static DatabaseReference mDatabase;
    protected static FirebaseDatabase mFirebaseInstance;

    private static InformationDAO INSTANCE = null;

    private InformationDAO() {
        mFirebaseInstance = FirebaseDatabase.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("information"); //gets the reference of the node 'quotes'
    }

    public final static InformationDAO getInstance(){
        if(INSTANCE == null){
            INSTANCE = new InformationDAO();
        }
        return INSTANCE;
    }

    public void addInformation(Information information){
        if(information != null){
            String informationType = information.getType();
            String informationID = mDatabase.child(informationType).push().getKey();
            try{
                mDatabase.child(informationType).child(informationID).setValue(information);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }

        }
    }

    public void getInformation(final String informationType, final TaskCompletionSource<ArrayList<Information>> informationGetter){
        final ArrayList<Information> informationList = new ArrayList<>();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(informationType)){
                    for(DataSnapshot D : dataSnapshot.child(informationType).getChildren()){
                        informationList.add(D.getValue(Information.class));
                    }
                }
                informationGetter.setResult(informationList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void deleteInformation(final Information information){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(information.getType())){
                    for(DataSnapshot D : dataSnapshot.child(information.getType()).getChildren()){
                        Information infoToCheck = D.getValue(Information.class);
                        if(infoToCheck.getContent().equals(information.getContent())){
                            mDatabase.child(information.getType()).child(D.getKey()).removeValue();
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
