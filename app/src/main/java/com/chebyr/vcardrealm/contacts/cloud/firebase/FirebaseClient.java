package com.chebyr.vcardrealm.contacts.cloud.firebase;

public class FirebaseClient
{
    private static String TAG = FirebaseClient.class.getSimpleName();

    private Authenticator authenticator;
    private Storage storage;
    private Database database;

    protected FirebaseClient()
    {
        authenticator = new Authenticator();
        storage = new Storage();
        database = new Database();
    }


}
