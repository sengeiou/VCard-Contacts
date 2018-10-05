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
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
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
    private ContactDetailsDataSource contactDetailsDataSource;

    private Cursor contactCursor;

    private ContactsSectionIndexer contactsSectionIndexer;
    private DataSetObserver mDataSetObserver;
    private int mRowIdColumn;

    public ContactDataSource(Context context, ContactRepository contactRepository)
    {
        this.context = context;
        contentResolver = context.getContentResolver();

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
        List<ContactData> contacts = getContacts(params.requestedLoadSize, params.requestedStartPosition);
        callback.onResult(contacts, 0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback callback)
    {
        List<ContactData> contacts = getContacts(params.loadSize, params.startPosition);
        callback.onResult(contacts);
    }

    public void loadContacts(Cursor contactCursor)
    {
        for (contactCursor.moveToFirst(); !contactCursor.isAfterLast();contactCursor.moveToNext())
        {
            ContactData contact = getContactData();

        }
    }

    public ContactData getContactData()
    {
        ContactData contact = new ContactData();
        contact.contactID = contactCursor.getLong(ContactQuery.ID);
        contact.lookupKey = contactCursor.getString(ContactQuery.LOOKUP_KEY);
        contact.contactUri = ContactsContract.Contacts.getLookupUri(contact.contactID, contact.lookupKey);
        contact.displayName = contactCursor.getString(ContactQuery.DISPLAY_NAME);
        contact.photoUriString = contactCursor.getString(ContactQuery.PHOTO_THUMBNAIL);
        if(contact.photoUriString == null)
            contact.photoUriString = "";

        contactDetailsDataSource = new ContactDetailsDataSource(context);

        ContactDetailsData contactInfo = contactDetailsDataSource.getContactInfo(contact.contactID);

        contact.organization = contactInfo.organization;
        contact.jobTitle = contactInfo.jobTitle;
        contact.nickName = contactInfo.nickName;
        contact.website = contactInfo.website;
        contact.address = contactInfo.address;
        contact.phoneNumbers = contactInfo.phoneNumbers;
        contact.IMs = contactInfo.IMs;
        contact.notes = contactInfo.notes;
        contact.eMails = contactInfo.eMails;
        contact.groups = contactInfo.groups;

        contact.incomingNumber = String.valueOf(contact.contactID);

        return contact;
    }

    private List<ContactData> getContacts(int limit, int offset) {
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

        List<ContactData> contacts = new ArrayList<>();

        while (!cursor.isLast()) {
            Long id = cursor.getLong(cursor.getColumnIndex(PROJECTION[0]));
            String lookupKey = cursor.getString(cursor.getColumnIndex(PROJECTION[0]));
            String name = cursor.getString(cursor.getColumnIndex(PROJECTION[2]));

            contacts.add(new ContactData(id, lookupKey, name));
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
        mDataSetObserver = new ContactsObserver(this);
        if (contactCursor != null)
        {
            contactCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public ContactData lookupNumber(String incomingNumber)
    {
        Log.d(TAG, "Lookup incomingNumber: " + incomingNumber);

        ContactData contact = new ContactData();

        contact.incomingNumber = incomingNumber;

        contact.contactID = getContactProfile(incomingNumber, contact);
        if(contact.contactID == 0)
            return null;

        Log.d(TAG, "Contact found. Retrieving additional information");

        String contractIDStr = String.valueOf(contact.contactID);
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
        return contact;
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
    public Cursor swapCursor(Cursor newContactCursor)
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
    }

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
            return new ContactDataSource(context, contactRepository);
        }
    }
}
