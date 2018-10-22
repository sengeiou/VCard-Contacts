package com.chebyr.vcardrealm.contacts.datasource.queries;

import android.net.Uri;
import android.provider.ContactsContract;

/* This interface defines constants for the Cursor and CursorLoader, based on constants defined
 * in the {@link android.provider.ContactsContract.Contacts} class. */
public interface ContactQuery
{
    // An identifier for the loader
    int QUERY_ID = 1;

    // A content URI for the Contacts table
    Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

    // The search/filter query Uri
    Uri FILTER_URI = ContactsContract.Contacts.CONTENT_FILTER_URI;

    String CONTACT_ID = ContactsContract.Contacts._ID; // The contact's row id
    String LOOKUP_KEY_STR = ContactsContract.Contacts.LOOKUP_KEY; // A pointer to the contact that is guaranteed to be more permanent than _ID.
    String DISPLAY_NAME_STR = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY; // The contact's displayable name or some other useful identifier such as an email address.
    String PHOTO_URI = ContactsContract.Contacts.PHOTO_URI; // The thumbnail image is pointed to by PHOTO_THUMBNAIL_URI.
    String PHOTO_THUMBNAIL_URI = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI; // The thumbnail image is pointed to by PHOTO_THUMBNAIL_URI.
    String SORT_KEY_STR = ContactsContract.Contacts.SORT_KEY_PRIMARY; // The sort order column for the returned Cursor, used by the AlphabetIndexer

    // The projection for the CursorLoader query. This is a list of columns that the Contacts Provider should return in the Cursor.
    // Given a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate a "permanent" contact URI.
    String[] PROJECTION = {CONTACT_ID, LOOKUP_KEY_STR, DISPLAY_NAME_STR, PHOTO_URI, PHOTO_THUMBNAIL_URI, SORT_KEY_STR};

    // The selection clause for the CursorLoader query. The search criteria defined here restrict results to contacts that have
    // a display name and are linked to visible groups. Notice that the search on the string provided by the user is
    // implemented by appending the search string to CONTENT_FILTER_URI.

    String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "<>''" + " AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1";

    String[] SELECTION_ARGS = null;

    // The desired sort order for the returned Cursor. The primary sort key allows for localization.
    String SORT_ORDER = DISPLAY_NAME_STR;//ContactsContract.Contacts.SORT_KEY_PRIMARY;

    // The query column numbers which map to each value in the projection
    int ID = 0;
    int LOOKUP_KEY = 1;
    int DISPLAY_NAME = 2;
    int PHOTO = 3;
    int PHOTO_THUMBNAIL = 4;
    int SORT_KEY = 5;

    //String[] JOIN = {CONTACT_ID};
}
