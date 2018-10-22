package com.chebyr.vcardrealm.contacts.datasource.queries;

import android.net.Uri;
import android.provider.ContactsContract;

public interface GroupsQuery
{
    int QUERY_ID = 3;

    String GROUP_ID = ContactsContract.Groups._ID;
    String TITLE = ContactsContract.Groups.TITLE;
    Uri URI = ContactsContract.Groups.CONTENT_URI;

    String[] PROJECTION = {GROUP_ID, TITLE};
    String SELECTION = ContactsContract.Groups._ID + " = ?";

    // The desired sort order for the returned Cursor. The primary sort key allows for localization.
    String SORT_ORDER = GROUP_ID;
}
