package com.chebyr.vcardrealm.contacts.html.model.queries;

import android.net.Uri;
import android.provider.ContactsContract;

public interface GroupsQuery
{
    int QUERY_ID = 3;

    String TITLE = ContactsContract.Groups.TITLE;

    Uri URI = ContactsContract.Groups.CONTENT_URI;
    String[] PROJECTION = {TITLE};
    String SELECTION = ContactsContract.Groups._ID + " = ?";



}
