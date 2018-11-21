package com.chebyr.vcardrealm.contacts.cloud.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Storage
{
    private static String TAG = Storage.class.getSimpleName();

    private StorageReference storageReference;
    private StorageReference riversRef;
    private Callback callback;

    public Storage(Callback callback)
    {
        storageReference = FirebaseStorage.getInstance().getReference();
        riversRef = storageReference.child("images/rivers.jpg");
        this.callback = callback;
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
                callback.fileUploadSuccess(downloadUrl.getResult());
            }
            catch (Exception e)
            {
                Log.d(TAG, e.getMessage());
            }
        });

        uploadTask.addOnFailureListener((@NonNull Exception exception) ->
        {
            // Handle unsuccessful uploads
            callback.fileUploadFail(exception.getMessage());
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

    public interface Callback
    {
        void fileUploadSuccess(Uri uri);
        void fileUploadFail(String errorMessage);
    }
}
