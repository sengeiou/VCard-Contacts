package com.chebyr.vcardrealm.contacts.html.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.Contact;
import com.chebyr.vcardrealm.contacts.html.model.queries.ContactDataQuery;
import com.chebyr.vcardrealm.contacts.html.model.queries.ContactQuery;
import com.chebyr.vcardrealm.contacts.html.model.queries.GroupsQuery;

public class ContactLoader extends AsyncTask<Cursor, Contact, Boolean>
{
    private static String TAG = "ContactLoader";
    private Callback callback;
    private ContentResolver contentResolver;

    public ContactLoader(ContentResolver contentResolver, Callback callback)
    {
        this.callback = callback;
        this.contentResolver = contentResolver;
        //contentResolver = context.getContentResolver();
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
                        String group = getGroup(contactDataCursor);
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

    /*public Contact lookupNumber(String incomingNumber)
    {
        Log.d(TAG, "Lookup incomingNumber: " + incomingNumber);

        Contact contact = new Contact();

        contact.incomingNumber = incomingNumber;

        contact.contactID = getContactProfile(incomingNumber, contact);
        if(contact.contactID == 0)
            return null;

        Log.d(TAG, "Contact found. Retrieving additional information");

        String contractIDStr = String.valueOf(contact.contactID);

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

        return contact;
    }*/


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

    public String getGroup(Cursor contactDataCursor)
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

    public interface Callback
    {
        void onContactLoaded(Contact contact);
        void onContactLoadingCompleted();
    }
}
