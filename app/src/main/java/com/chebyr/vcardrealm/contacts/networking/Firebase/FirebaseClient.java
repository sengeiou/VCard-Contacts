package com.chebyr.vcardrealm.contacts.networking.Firebase;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseClient
{
    private static String TAG = FirebaseClient.class.getSimpleName();
    private FirebaseDatabase databaseInstance;
    DatabaseReference databaseReference;
    private StorageReference storageReference;
    private StorageReference riversRef;

    protected void FirebaseClient(Context context)
    {
        databaseInstance = FirebaseDatabase.getInstance();
        databaseReference = databaseInstance.getReference("message");

        storageReference = FirebaseStorage.getInstance().getReference();
        riversRef = storageReference.child("images/rivers.jpg");
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

    public void uploadFile()
    {
        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));

        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) ->
        {
            try
            {
                // Get a URL to the uploaded content
                StorageMetadata storageMetadata = taskSnapshot.getMetadata();
                StorageReference storageReference = storageMetadata.getReference();
                Task<Uri> downloadUrl = storageReference.getDownloadUrl();
            }
            catch (Exception e)
            {
                Log.d(TAG, e.getMessage());
            }
        });

        uploadTask.addOnFailureListener((@NonNull Exception exception) ->
        {
                // Handle unsuccessful uploads
                // ...
        });
    }

    public void downloadFile()
    {
        try
        {
            File localFile = File.createTempFile("images", "jpg");
            FileDownloadTask fileDownloadTask = riversRef.getFile(localFile);

            fileDownloadTask.addOnSuccessListener((FileDownloadTask.TaskSnapshot taskSnapshot) ->
            {
                // Successfully downloaded data to local file
                // ...
            });

            fileDownloadTask.addOnFailureListener((@NonNull Exception exception) ->
            {
                // Handle failed download
                // ...
            });
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
    }
}
