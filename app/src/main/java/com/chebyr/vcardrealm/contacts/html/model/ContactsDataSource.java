package com.chebyr.vcardrealm.contacts.html.model;

import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.chebyr.vcardrealm.contacts.html.Contact;

import java.util.ArrayList;
import java.util.List;

class ContactsDataSource extends PositionalDataSource<Contact>
{
    private ContentResolver contentResolver;

    public ContactsDataSource(ContentResolver contentResolver)
    {
        this.contentResolver = contentResolver;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback)
    {
        List<Contact> contacts = getContacts(params.requestedLoadSize, params.requestedStartPosition);
        callback.onResult(contacts, 0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback callback)
    {
        List<Contact> contacts = getContacts(params.loadSize, params.startPosition);
        callback.onResult(contacts);
    }

    private List<Contact> getContacts(int limit, int offset) {
        String[] PROJECTION = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};

        // Get the cursor
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY +
                        " ASC LIMIT " + limit + " OFFSET " + offset);

        // load data from cursor into a list
        cursor.moveToFirst();

        List<Contact> contacts = new ArrayList<>();

        while (!cursor.isLast()) {
            Long id = cursor.getLong(cursor.getColumnIndex(PROJECTION[0]));
            String lookupKey = cursor.getString(cursor.getColumnIndex(PROJECTION[0]));
            String name = cursor.getString(cursor.getColumnIndex(PROJECTION[2]));

            contacts.add(new Contact(id, lookupKey, name));
            cursor.moveToNext();
        }
        cursor.close();

        // return the list of results
        return contacts;
    }
}
