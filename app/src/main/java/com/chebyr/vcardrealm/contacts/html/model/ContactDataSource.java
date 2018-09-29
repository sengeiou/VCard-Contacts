package com.chebyr.vcardrealm.contacts.html.model;

import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.Contact;
import com.chebyr.vcardrealm.contacts.html.model.queries.ContactDataQuery;
import com.chebyr.vcardrealm.contacts.html.model.queries.ContactQuery;
import com.chebyr.vcardrealm.contacts.html.model.queries.GroupsQuery;

import java.util.ArrayList;
import java.util.List;

class ContactDataSource extends PositionalDataSource<Contact>
{
    private static String TAG = ContactDataSource.class.getSimpleName();

    private Callback callback;

    private ContentResolver contentResolver;
    private ContactLoader contactLoader;
    private Context context;

    public ContactDataSource(Context context, Callback callback)
    {
        this.context = context;
        this.callback = callback;
        contentResolver = context.getContentResolver();
        contactLoader = new ContactLoader();

        // If there's a previously selected search item from a saved state then don't bother
        // initializing the loader as it will be restarted later when the query is populated into
        // the action bar search view (see onQueryTextChange() in onCreateOptionsMenu()).
        //if (mPreviouslySelectedSearchItem == 0)
        {
            // Initialize the loader, and create a loader identified by ContactQuery.QUERY_ID
            contactLoader.startContactLoader("");
        }
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

    public String[] getContactNumberFromUri(Uri contactUri)
    {
        return contactLoader.getContactNumberFromUri(contactUri);
    }

    public class ContactLoader extends AsyncTask<Cursor, Contact, Boolean> implements CursorLoader.OnLoadCompleteListener, CursorLoader.OnLoadCanceledListener
    {
        CursorLoader contactCursorLoader;
        CursorLoader contactCursorDataLoader;

        private Cursor contactCursor;
        private Cursor contactDataCursor;
        private Cursor emailCursor;
        private Cursor phoneCursor;
        private Cursor groupCursor;

        private ContactsSectionIndexer contactsSectionIndexer;
        private DataSetObserver mDataSetObserver;
        public boolean mDataValid;
        private int mRowIdColumn;

        public ContactLoader()
        {
            contactCursor = null;
            contactDataCursor = null;
            contactsSectionIndexer = new ContactsSectionIndexer(context, ContactQuery.SORT_KEY);
        }

        @Override
        protected Boolean doInBackground(Cursor... cursors)
        {
            Cursor contactCursor = cursors[0];

            long totalLoadingTime = 0;

            for (contactCursor.moveToFirst(); !contactCursor.isAfterLast();contactCursor.moveToNext())
            {
                //if (!contactManager.mDataValid)
                //{
                //    throw new IllegalStateException("this should only be called when the cursor is valid");
                //}
                //if (!contactCursor.moveToPosition(position))
                //{
                //  throw new IllegalStateException("couldn't move cursor to position " + position);
                //}

                long loadingStartTime = System.currentTimeMillis();

                Contact contact = new Contact();
                contact.contactID = contactCursor.getLong(ContactQuery.ID);
                contact.lookupKey = contactCursor.getString(ContactQuery.LOOKUP_KEY);
                contact.contactUri = ContactsContract.Contacts.getLookupUri(contact.contactID, contact.lookupKey);
                contact.displayName = contactCursor.getString(ContactQuery.DISPLAY_NAME);
                contact.photoUriString = contactCursor.getString(ContactQuery.PHOTO_THUMBNAIL);
                if(contact.photoUriString == null)
                    contact.photoUriString = "";

                Cursor contactDataCursor = getContactData(contact.contactID);
                String mimeType;
                String separator = ", ";

                for(contactDataCursor.moveToFirst(); !contactDataCursor.isAfterLast(); contactDataCursor.moveToNext())
                {
                    mimeType = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.MIMETYPE));

                    switch (mimeType)
                    {
                        case ContactDataQuery.ORGANIZATION_MIME:
                        {
                            contact.organization = getCompany(contactDataCursor);
                            contact.jobTitle = getJobTitle(contactDataCursor);
                            break;
                        }
                        case ContactDataQuery.NICK_NAME_MIME:
                        {
                            contact.nickName = getNickName(contactDataCursor);
                            break;
                        }
                        case ContactDataQuery.WEBSITE_MIME:
                        {
                            contact.website = getWebsite(contactDataCursor);
                            break;
                        }
                        case ContactDataQuery.ADDRESS_MIME:
                        {
                            contact.address = getAddress(contactDataCursor);
                            break;
                        }
                        case ContactDataQuery.PHONE_MIME:
                        {
                            String phone = getPhoneNumber(contactDataCursor);

                            if ((contact.phoneNumbers.length() > 0) && (phone.length() > 0))
                                contact.phoneNumbers += separator + phone;
                            else
                                contact.phoneNumbers = phone;

                            break;
                        }
                        case ContactDataQuery.IM_MIME:
                        {
                            String instantMessenger = getIM(contactDataCursor);

                            if ((contact.IMs.length() > 0) && (instantMessenger.length() > 0))
                                contact.IMs += separator + instantMessenger;
                            else
                                contact.IMs = instantMessenger;

                            break;
                        }
                        case ContactDataQuery.NOTE_MIME:
                        {
                            contact.notes = getNotes(contactDataCursor);
                            break;
                        }
                        case ContactDataQuery.EMAIL_MIME:
                        {
                            String email = getEmailAddresses(contactDataCursor);

                            if ((contact.eMails.length() > 0) && (email.length() > 0))
                                contact.eMails += separator + email;
                            else
                                contact.eMails = email;

                            break;
                        }
                        case ContactDataQuery.GROUP_MIME:
                        {
                            String group = getGroups(contactDataCursor);
                            if ((contact.groups.length() > 0) && (group.length() > 0))
                                contact.groups += separator + group;
                            else
                                contact.groups = group;

                            break;
                        }
                    }
                }
                contactDataCursor.close();

                contact.incomingNumber = String.valueOf(contact.contactID);

                // Update the contact list in main thread
                publishProgress(contact);

                long loadingCompletedTime = System.currentTimeMillis();
                totalLoadingTime += loadingCompletedTime - loadingStartTime;
            }
            Log.d(TAG, "Contacts loading time: " + totalLoadingTime);
            return true;
        }

        @Override
        protected void onProgressUpdate(Contact... contacts)
        {
            super.onProgressUpdate(contacts);
            callback.onContactLoaded(contacts[0]);
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            callback.onContactLoadingCompleted();
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

            // Returns a new CursorLoader for querying the Contacts table. No arguments are used for the selection clause.
            // The search string is either encoded onto the content URI, or no contacts search string is used.
            // The other search criteria are constants. See the ContactQuery interface.
            contactCursorLoader = new CursorLoader(context, contentUri, ContactQuery.PROJECTION, ContactQuery.SELECTION, ContactQuery.SELECTION_ARGS, ContactQuery.SORT_ORDER);
            contactCursorLoader.registerListener(ContactQuery.QUERY_ID, this);
            contactCursorLoader.startLoading();

            mDataValid = false;
            mRowIdColumn = -1;
            mDataSetObserver = new NotifyingDataSetObserver();
            if (contactCursor != null)
            {
                contactCursor.registerDataSetObserver(mDataSetObserver);
            }

            startContactDataLoader();
        }

        private void stopContactLoader()
        {
            contactCursorLoader.abandon();
            contactCursorLoader.unregisterListener(this);
            //contactCursorLoader.unregisterOnLoadCanceledListener(this);
            contactCursorLoader.reset();
        }

        private void startContactDataLoader()
        {
            contactCursorDataLoader = new CursorLoader(context, ContactDataQuery.URI, ContactDataQuery.PROJECTION, ContactDataQuery.SELECTION, ContactDataQuery.SELECTION_ARGS, ContactDataQuery.SORT_ORDER);
            contactCursorDataLoader.registerListener(ContactQuery.QUERY_ID, this);
            contactCursorDataLoader.startLoading();
        }

        public Contact lookupNumber(String incomingNumber)
        {
            Log.d(TAG, "Lookup incomingNumber: " + incomingNumber);

            Contact contact = new Contact();

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

        public String getPhoneNumber(Cursor contactDataCursor)
        {
            String phone = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.PHONE_NUMBER));
            if(phone != null)
                return phone;

            return "";
        }

        public String getEmailAddresses(Cursor contactDataCursor)
        {
            String email = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.EMAIL));
            if(email != null)
                return  email;

            return "";
        }

        public String getAddress(Cursor contactDataCursor)
        {
            String address = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.POSTAL_ADDRESS));
            if(address != null)
                return  address;

            return "";
        }

        public String getIM(Cursor contactDataCursor)
        {
            String instantMessenger = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.INSTANT_MESSENGER));
            if(instantMessenger != null)
                return instantMessenger;

            return "";
        }

        public String getCompany(Cursor contactDataCursor)
        {
            String company = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.COMPANY));
            if(company != null)
                return company;

            return "";
        }

        public String getJobTitle(Cursor contactDataCursor)
        {
            String jobTitle = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.JOB_TITLE));
            if(jobTitle != null)
                return jobTitle;

            return "";
        }

        public String getNickName(Cursor contactDataCursor)
        {
            String nickName = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.NICK_NAME));
            if(nickName != null)
                return nickName;

            return "";
        }

        public String getGroups(Cursor contactDataCursor)
        {
            String groupTitle = null;
            String groupRowID = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.GROUP_ROW_ID));

            // Get Titles from Groups table using groupIDs from groupRowID
            if(groupRowID != null)
            {
                String[] whereParameters = new String[] {groupRowID};

                Cursor groupCursor = contentResolver.query(GroupsQuery.URI, GroupsQuery.PROJECTION, GroupsQuery.SELECTION, whereParameters, null);
                if(groupCursor == null)
                    return "";

                if(groupCursor.moveToFirst())
                {
                    groupTitle = groupCursor.getString(groupCursor.getColumnIndex(GroupsQuery.TITLE));
                }
                groupCursor.close();

                if(groupTitle != null)
                    return groupTitle;
            }
            return "";
        }

        public String getWebsite(Cursor contactDataCursor)
        {
            String website = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.WEBSITE));
            if(website != null)
                return website;

            return "";
        }

        public String getNotes(Cursor contactDataCursor)
        {
            String notes = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDataQuery.NOTE));
            if(notes != null)
                return notes;

            return "";
        }

        public Cursor getContactData(long contactID)
        {
            String[] whereParameters = new String[]{String.valueOf(contactID)};

            Cursor contactDataCursor = contentResolver.query(ContactDataQuery.URI, ContactDataQuery.PROJECTION, ContactDataQuery.SELECTION, whereParameters, null);
            return contactDataCursor;
        }


        @Override
        public void onLoadComplete(Loader loader, Object data)
        {
            int loaderId = loader.getId();

            Cursor cursor = (Cursor)data;

            switch(loaderId)
            {
                case ContactQuery.QUERY_ID:
                    swapCursor(cursor);
                    break;

                case ContactDataQuery.QUERY_ID:
                    contactDataCursor = cursor;
                    break;
            }
        }

        @Override
        public void onLoadCanceled(Loader loader)
        {
            // When the loader is being reset, clear the cursor from the adapter. This allows the cursor resources to be freed.
            int loaderId = loader.getId();
            switch(loaderId)
            {
                case ContactQuery.QUERY_ID:
                    swapCursor(null);
                    break;

                case ContactDataQuery.QUERY_ID:
                    contactDataCursor =  null;
                    break;
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

        private class NotifyingDataSetObserver extends DataSetObserver
        {
            @Override
            public void onChanged()
            {
                super.onChanged();
                mDataValid = true;
                callback.onDataSetChanged();
            }

            @Override
            public void onInvalidated()
            {
                super.onInvalidated();
                mDataValid = false;
                callback.onDataSetChanged();
            }
        }
    }

    public interface Callback
    {
        void onContactLoaded(Contact contact);
        void onContactLoadingCompleted();
        void onDataSetChanged();
    }
}
