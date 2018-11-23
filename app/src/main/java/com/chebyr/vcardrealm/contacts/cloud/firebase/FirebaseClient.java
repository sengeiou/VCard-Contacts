package com.chebyr.vcardrealm.contacts.cloud.firebase;

import android.net.Uri;

public class FirebaseClient implements Storage.Callback
{
    private static String TAG = FirebaseClient.class.getSimpleName();

    private Authenticator authenticator;
    private Storage storage;
    private Database database;

    protected FirebaseClient()
    {
        authenticator = new Authenticator();
        storage = new Storage(this);
        database = new Database();
    }

    @Override
    public void onFileUploadSuccess(Uri uri) {

    }

    @Override
    public void onFileUploadFail(String errorMessage) {

    }
}
