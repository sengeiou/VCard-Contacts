package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.queries.ContactQuery;
import com.chebyr.vcardrealm.contacts.html.repository.ContactRepository;
import com.chebyr.vcardrealm.contacts.html.repository.ContactsSectionIndexer;

import java.util.ArrayList;
import java.util.List;

public class ContactDataSource extends PositionalDataSource<ContactData>
{
    private static String TAG = ContactDataSource.class.getSimpleName();

    private ContentResolver contentResolver;
    private Context context;

    private Cursor contactCursor;

    private ContactsSectionIndexer contactsSectionIndexer;
    private DataSetObserver mDataSetObserver;
    private int mRowIdColumn;
    private ContactsObserver.Callback callback;

    private boolean mDataValid;

    public ContactDataSource(Context context, ContactRepository contactRepository)
    {
        this.context = context;
        contentResolver = context.getContentResolver();

        callback = contactRepository.callback;

        // If there's a previously selected search item from a saved state then don't bother
        // initializing the loader as it will be restarted later when the query is populated into
        // the action bar search view (see onQueryTextChange() in onCreateOptionsMenu()).
        //if (mPreviouslySelectedSearchItem == 0)
        {
            // Initialize the loader, and create a loader identified by ContactQuery.QUERY_ID
            startContactLoader("");
        }
        contactsSectionIndexer = new ContactsSectionIndexer(context, ContactQuery.SORT_KEY);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback)
    {
        Log.d(TAG, "loadInitial. Read contacts. requestedLoadSize: " + params.requestedLoadSize + "requestedStartPosition :" + params.requestedStartPosition);
        List<ContactData> contacts = getContacts(params.requestedLoadSize, params.requestedStartPosition);
        Log.d(TAG, "No of contacts read: " + contacts.size());
        callback.onResult(contacts, 0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback callback)
    {
        Log.d(TAG, "loadRange. Read contacts. loadSize: " + params.loadSize+ "startPosition :" + params.startPosition);
        List<ContactData> contacts = getContacts(params.loadSize, params.startPosition);
        Log.d(TAG, "No of contacts read: " + contacts.size());
        callback.onResult(contacts);
    }

    public void loadContacts(Cursor contactCursor)
    {
        for (contactCursor.moveToFirst(); !contactCursor.isAfterLast();contactCursor.moveToNext())
        {
            ContactData contact = getContactData(contactCursor);

        }
    }

    public ContactData getContactData(Cursor cursor)
    {
        ContactData contactData = new ContactData();
        contactData.contactID = cursor.getLong(ContactQuery.ID);
        contactData.lookupKey = cursor.getString(ContactQuery.LOOKUP_KEY);
        contactData.contactUri = ContactsContract.Contacts.getLookupUri(contactData.contactID, contactData.lookupKey);
        contactData.displayName = cursor.getString(ContactQuery.DISPLAY_NAME);
        contactData.photoUriString = cursor.getString(ContactQuery.PHOTO_THUMBNAIL);
        if(contactData.photoUriString == null)
            contactData.photoUriString = "";

        return contactData;
    }

    private List<ContactData> getContacts(int limit, int offset) {
//        String[] PROJECTION = {
//                ContactsContract.Contacts._ID,
//                ContactsContract.Contacts.LOOKUP_KEY,
//                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};

        // Get the cursor
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                ContactQuery.PROJECTION,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY +
                        " ASC LIMIT " + limit + " OFFSET " + offset);

        // load data from cursor into a list
        cursor.moveToFirst();

        List<ContactData> contacts = new ArrayList<>();

        while (!cursor.isLast()) {
            //Long id = cursor.getLong(cursor.getColumnIndex(PROJECTION[0]));
            //String lookupKey = cursor.getString(cursor.getColumnIndex(PROJECTION[0]));
            //String name = cursor.getString(cursor.getColumnIndex(PROJECTION[2]));

            ContactData contactData = getContactData(cursor);
            contacts.add(contactData);//new ContactData(id, lookupKey, name));
            cursor.moveToNext();
        }
        cursor.close();

        // return the list of results
        return contacts;
    }

    public void startContactLoader(String searchTerm)
    {
        Uri contentUri;

        // There are two types of searches, one which displays all contacts and one which filters contacts by a search query.
        // If mSearchTerm is set then a search query has been entered and the latter should be used.

        if (searchTerm == null)
        {
            // Since there's no search string, use the content URI that searches the entire Contacts table
            contentUri = ContactQuery.CONTENT_URI;
        }
        else
        {
            // Since there's a search string, use the special content Uri that searches the
            // Contacts table. The URI consists of a base Uri and the search string.
            contentUri = Uri.withAppendedPath(ContactQuery.FILTER_URI, Uri.encode(searchTerm));
        }

        mRowIdColumn = -1;
        mDataSetObserver = new ContactsObserver(callback);
        if (contactCursor != null)
        {
            contactCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public ContactData lookupNumber(String incomingNumber)
    {
        Log.d(TAG, "Lookup incomingNumber: " + incomingNumber);

        ContactData contactData = new ContactData();

        contactData.incomingNumber = incomingNumber;

        contactData.contactID = getContactProfile(incomingNumber, contactData);
        if(contactData.contactID == 0)
            return null;

        Log.d(TAG, "Contact found. Retrieving additional information");

        String contractIDStr = String.valueOf(contactData.contactID);
/*
        getCompany(contractIDStr, contact);

        contact.photoUriString = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact.contactID).toString();
        contact.phoneNumbers = getPhoneNumber(contractIDStr);
        contact.eMails = getEmailAddresses(contractIDStr);
        contact.IMs = getIM(contractIDStr);
        contact.nickName = getNickName(contractIDStr);
        contact.groups = getGroups(contractIDStr);
        contact.address = getAddress(contractIDStr);
        contact.website = getWebsite(contractIDStr);
        contact.notes = getNotes(contractIDStr);
*/
        return contactData;
    }


    public long getContactProfile(String incomingNumber, ContactData contact)
    {
        long contactID = 0;

        Uri profileUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
        String[] projection = new String[]
                {
                        ContactsContract.Profile._ID,
                        ContactsContract.Profile.DISPLAY_NAME,
                        ContactsContract.Profile.PHOTO_THUMBNAIL_URI
                };

        Cursor contactLookupCursor = contentResolver.query(profileUri, projection, null, null, null);

        if (contactLookupCursor.moveToFirst())
        {
            // Get Contact ID

            int contactIDIndex = contactLookupCursor.getColumnIndex(ContactsContract.Profile._ID);
            if (contactIDIndex >= 0)
                contactID = contactLookupCursor.getLong(contactIDIndex);

            // Get Display Name
            int displayNameIndex = contactLookupCursor.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME);
            if (displayNameIndex >= 0)
            {
                contact.displayName = contactLookupCursor.getString(displayNameIndex);
            }
            // Get thumbnail
            int photoThumbnailUriIndex = contactLookupCursor.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI);
            if (photoThumbnailUriIndex >= 0)
            {
                contact.photoUriString = contactLookupCursor.getString(photoThumbnailUriIndex);
            }
        }
        contactLookupCursor.close();
        return contactID;
    }


    public String[] getContactNumberFromUri(Uri contactUri)
    {
        try
        {
            String[] contactDetails = new String[]{null, null};
            Cursor cursor = contentResolver.query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            contactDetails[0] = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactDetails[1] = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Profile.DISPLAY_NAME));
            return contactDetails;
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    /* Overrides swapCursor to move the new Cursor into the AlphabetIndex as well as the CursorAdapter.
     Swap in a new Cursor, returning the old Cursor.  Unlike {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em> closed. */
/*    public Cursor swapCursor(Cursor newContactCursor)
    {
        if(newContactCursor == contactCursor)
        {
            return null;
        }

        final Cursor oldCursor = contactCursor;
        if (oldCursor != null && mDataSetObserver != null)
        {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }

        contactCursor = newContactCursor;

        if(contactCursor != null)
        {
            if(mDataSetObserver != null)
            {
                contactCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newContactCursor.getColumnIndex(ContactQuery.CONTACT_ID);
            mDataValid = true;

            contactsSectionIndexer.setCursor(newContactCursor);

            int contactsCount = contactCursor.getCount();
            if(contactsCount > 0)
            {
                Log.d(TAG, "Cursor Loaded. Contact count: " + contactsCount);
                //contacts = new ContactList();
                execute(contactCursor);
            }
        }
        else
        {
            mRowIdColumn = -1;
            mDataValid = false;
        }
        if(callback != null)
            callback.onDataSetChanged();

        return oldCursor;
    }*/

    public long getItemId(int position)
    {
        if (mDataValid && (contactCursor != null) && (contactCursor.moveToPosition(position)) && (mRowIdColumn != -1))
        {
            return contactCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    public static class Factory extends DataSource.Factory<Integer, ContactData>
    {
        private Context context;
        private ContactRepository contactRepository;

        public Factory(Context context, ContactRepository contactRepository)
        {
            this.context = context;
            this.contactRepository = contactRepository;
        }

        public void setFilter(String filterState)
        {

        }

        @Override
        public DataSource<Integer, ContactData> create()
        {
            Log.d(TAG, "Create Contact Data source");
            return new ContactDataSource(context, contactRepository);
        }
    }
}
