package com.chebyr.vcardrealm.contacts.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PermissionManager
{
    private static String TAG = PermissionManager.class.getSimpleName();
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 12345;

    private Activity activity;

    public PermissionManager(Activity activity)
    {
        this.activity = activity;
    }

    public boolean getStorageWritePermission()
    {
        int storageWritePermission = ContextCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(storageWritePermission == PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "permission WRITE_EXTERNAL_STORAGE granted");
            return true;
        }
        else
        {
            Log.d(TAG, "Permission WRITE_EXTERNAL_STORAGE is not granted");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                Log.d(TAG, "Show an explanation to the user *asynchronously*");
                // After the user sees the explanation, try again to request the permission.
            }
            else
            {
                // No explanation needed; request the permission
                Log.d(TAG, "Request for permission WRITE_EXTERNAL_STORAGE");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
            return false;
        }
    }

    public int onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "permission WRITE_EXTERNAL_STORAGE granted");
                    return PERMISSION_WRITE_EXTERNAL_STORAGE;
                }
                else
                {
                    Log.d(TAG, "permission WRITE_EXTERNAL_STORAGE denied.");
                    // Disable the functionality that depends on this permission.
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
        return 0;
    }
}
