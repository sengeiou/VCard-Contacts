package com.chebyr.vcardrealm.contacts.cloud.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Database
{
    private static String TAG = Database.class.getSimpleName();
    private static String TEMPLATES = "Templates";
    private FirebaseDatabase databaseInstance;
    private DatabaseReference databaseReference;

    public Database()
    {
        databaseInstance = FirebaseDatabase.getInstance();
        databaseReference = databaseInstance.getReference("message");

    }

    public void readDatabase()
    {


        // Read from the databaseInstance
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void writeDatabase()
    {
        // Write a message to the databaseInstance

        databaseReference.setValue("Hello, World!");
    }


    protected void doInBackground(String trialId) {
//        boolean isFinished;

        databaseReference.child(TEMPLATES).child(trialId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Uri> uriList = new ArrayList<>();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    Uri uri = iterator.next().getValue(Uri.class);
                    uriList.add(uri);
                }
//                isFinished = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
