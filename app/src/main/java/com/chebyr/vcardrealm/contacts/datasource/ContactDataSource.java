package com.chebyr.vcardrealm.contacts.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.chebyr.vcardrealm.contacts.data.ContactData;
import com.chebyr.vcardrealm.contacts.datasource.queries.ContactQuery;
import com.chebyr.vcardrealm.contacts.repository.ContactRepository;
import com.chebyr.vcardrealm.contacts.repository.ContactsSectionIndexer;
import com.chebyr.vcardrealm.contacts.data.Contact;
import com.chebyr.vcardrealm.contacts.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactDataSource extends PositionalDataSource<Contact>
{
//    private static String TAG = ContactDataSource.class.getSimpleName();

    private ContentResolver contentResolver;
    private Context context;

    private Cursor contactCursor;

    private ContactsSectionIndexer contactsSectionIndexer;
    private DataSetObserver mDataSetObserver;
    private int mRowIdColumn;
    private ContactsObserver.Callback callback;
    private ContactDetailsDataSource contactDetailsDataSource;
    private TemplateDataSource templateDataSource;
    private TemplateParser templateParser;
    private String filterState;
    private FileUtil fileUtil;

    private boolean mDataValid;

    public ContactDataSource(Context context, ContactRepository contactRepository)
    {
        this.context = context;
        contentResolver = context.getContentResolver();

        callback = contactRepository.callback;
        contactDetailsDataSource = new ContactDetailsDataSource(context, filterState);
        templateDataSource = new TemplateDataSource(context);
        templateParser = new TemplateParser(context);
        // If there's a previously selected search item from a saved state then don't bother
        // initializing the loader as it will be restarted later when the query is populated into
        // the action bar search view (see onQueryTextChange() in onCreateOptionsMenu()).
        //if (mPreviouslySelectedSearchItem == 0)
        {
            // Initialize the loader, and create a loader identified by ContactQuery.QUERY_ID
            startContactLoader("");
        }
        contactsSectionIndexer = new ContactsSectionIndexer(context, ContactQuery.SORT_KEY);
        fileUtil = new FileUtil(context);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback)
    {
  //      Log.d(TAG, "loadInitial. Read contacts. requestedLoadSize: " + params.requestedLoadSize + "requestedStartPosition :" + params.requestedStartPosition);
        List<Contact> contacts = getContacts(params.requestedLoadSize, params.requestedStartPosition);
  //      Log.d(TAG, "No of contacts read: " + contacts.size());
        callback.onResult(contacts, 0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback callback)
    {
    //    Log.d(TAG, "loadRange. Read contacts. loadSize: " + params.loadSize+ "startPosition :" + params.startPosition);
        List<Contact> contacts = getContacts(params.loadSize, params.startPosition);
    //    Log.d(TAG, "No of contacts read: " + contacts.size());
        callback.onResult(contacts);
    }

    public void setFilterState(String filterState)
    {
        this.filterState = filterState;
    }

    private List<Contact> getContacts(int loadSize, int startPosition)
    {
        // Get the cursor
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                ContactQuery.PROJECTION,
                null,
                null,
                ContactQuery.SORT_ORDER
                //ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC LIMIT " + limit + " OFFSET " + offset
        );

        List<Contact> contactDataList = new ArrayList<>();

        // load data from cursor into a list
        cursor.moveToPosition(startPosition);

        for(int count = 0; count < loadSize; count++)
        {
            if(cursor.isAfterLast())
                break;

            Contact contact = new Contact();
            contact.contactID = cursor.getLong(ContactQuery.ID);
            contact.data = getContactData(contact.contactID, cursor);
            contact.details = contactDetailsDataSource.getContactDetailsData(contact.contactID);
            contact.template = templateDataSource.loadTemplate(contact.contactID);
            contact.vcardHtml = templateParser.generateVCardHtml(contact);

            contactDataList.add(contact);

            cursor.moveToNext();
        }
        cursor.close();

        // return the list of results
        return contactDataList;
    }

    private ContactData getContactData(long contactID, Cursor cursor)
    {
        ContactData contactData = new ContactData();
        contactData.lookupKey = cursor.getString(ContactQuery.LOOKUP_KEY);
        contactData.contactUri = ContactsContract.Contacts.getLookupUri(contactID, contactData.lookupKey);
        contactData.displayName = cursor.getString(ContactQuery.DISPLAY_NAME);
        contactData.setPhotoUri(cursor.getString(ContactQuery.PHOTO));
        contactData.setPhotoThumbnailUri(cursor.getString(ContactQuery.PHOTO_THUMBNAIL));

        return contactData;
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

    public Contact lookupNumber(String incomingNumber)
    {
    //    Log.d(TAG, "Lookup incomingNumber: " + incomingNumber);

        Contact contact = new Contact();

        contact.incomingNumber = incomingNumber;

        contact.contactID = getContactProfile(incomingNumber, contact);
        if(contact.contactID == 0)
            return null;

    //    Log.d(TAG, "Contact found. Retrieving additional information");

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


    public long getContactProfile(String incomingNumber, Contact contact)
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
                contact.data.displayName = contactLookupCursor.getString(displayNameIndex);
            }
            // Get thumbnail
            int photoThumbnailUriIndex = contactLookupCursor.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI);
            if (photoThumbnailUriIndex >= 0)
            {
                contact.data.setPhotoThumbnailUri(contactLookupCursor.getString(photoThumbnailUriIndex));
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
        //    Log.d(TAG, e.toString());
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

    public static class Factory extends DataSource.Factory<Integer, Contact>
    {
        private Context context;
        private ContactRepository contactRepository;
        private String filterState;

        public Factory(Context context, ContactRepository contactRepository)
        {
            this.context = context;
            this.contactRepository = contactRepository;
        }

        public void setFilter(String filterState)
        {
            this.filterState = filterState;
        }

        @Override
        public DataSource<Integer, Contact> create()
        {
        //    Log.d(TAG, "Create ContactDataSource");
            return new ContactDataSource(context, contactRepository);
        }
    }
}
