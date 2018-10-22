package com.chebyr.vcardrealm.contacts.html.datasource;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.queries.ContactDetailsQuery;

public class ContactDetailsDataSource
{
    private static String TAG = ContactDetailsDataSource.class.getSimpleName();

    private static String separator = ", ";

    private ContentResolver contentResolver;
    private String filterState;
    private GroupDataSource groupDataSource;

    public ContactDetailsDataSource(Context context, String filterState)
    {
        this.filterState = filterState;
        contentResolver = context.getContentResolver();
        groupDataSource = new GroupDataSource(context, filterState);
    }

    public void setFilterState(String filterState)
    {
        this.filterState = filterState;
    }

    public ContactDetailsData getContactDetailsData(long contactDataID)
    {
        ContactDetailsData contactDetailsData = new ContactDetailsData();
        contactDetailsData.contactID = contactDataID;

        String[] whereParams = new String[]{String.valueOf(contactDataID)};

        Cursor contactDetailsCursor = contentResolver.query(ContactDetailsQuery.URI, ContactDetailsQuery.PROJECTION, ContactDetailsQuery.SELECTION, whereParams, null);
        if(contactDetailsCursor != null)
        {
            for (contactDetailsCursor.moveToFirst(); !contactDetailsCursor.isAfterLast(); contactDetailsCursor.moveToNext())
            {
                String mimeType = contactDetailsCursor.getString(contactDetailsCursor.getColumnIndex(ContactDetailsQuery.MIMETYPE));

                switch (mimeType)
                {
                    case ContactDetailsQuery.ORGANIZATION_MIME:
                    {
                        contactDetailsData.organization = getCompany(contactDetailsCursor);
                        contactDetailsData.jobTitle = getJobTitle(contactDetailsCursor);
                        break;
                    }
                    case ContactDetailsQuery.NICK_NAME_MIME:
                    {
                        contactDetailsData.nickName = getNickName(contactDetailsCursor);
                        break;
                    }
                    case ContactDetailsQuery.WEBSITE_MIME:
                    {
                        contactDetailsData.website = getWebsite(contactDetailsCursor);
                        break;
                    }
                    case ContactDetailsQuery.ADDRESS_MIME:
                    {
                        contactDetailsData.address = getAddress(contactDetailsCursor);
                        break;
                    }
                    case ContactDetailsQuery.PHONE_MIME:
                    {
                        String phone = getPhoneNumber(contactDetailsCursor);

                        if(contactDetailsData.phoneNumbers == null)
                            contactDetailsData.phoneNumbers = phone;
                        else if ((contactDetailsData.phoneNumbers.length() > 0) && (phone.length() > 0))
                            contactDetailsData.phoneNumbers += separator + phone;
                        else
                            contactDetailsData.phoneNumbers = phone;

                        break;
                    }
                    case ContactDetailsQuery.IM_MIME:
                    {
                        String instantMessenger = getIM(contactDetailsCursor);

                        if ((contactDetailsData.IMs.length() > 0) && (instantMessenger.length() > 0))
                            contactDetailsData.IMs += separator + instantMessenger;
                        else
                            contactDetailsData.IMs = instantMessenger;

                        break;
                    }
                    case ContactDetailsQuery.NOTE_MIME:
                    {
                        contactDetailsData.notes = getNotes(contactDetailsCursor);
                        break;
                    }
                    case ContactDetailsQuery.EMAIL_MIME:
                    {
                        String email = getEmailAddresses(contactDetailsCursor);

                        if(contactDetailsData.eMails == null)
                            contactDetailsData.eMails = email;
                        else if((contactDetailsData.eMails.length() > 0) && (email.length() > 0))
                            contactDetailsData.eMails += separator + email;
                        else
                            contactDetailsData.eMails = email;

                        break;
                    }
                    case ContactDetailsQuery.GROUP_MIME:
                    {
                        contactDetailsData.groupRowID = contactDetailsCursor.getString(contactDetailsCursor.getColumnIndex(ContactDetailsQuery.GROUP_ROW_ID));
                        contactDetailsData.groupData = groupDataSource.getGroupData(contactDetailsData.groupRowID);
                        break;
                    }
                }
  //              Log.d(TAG, "eMails: " + contactDetailsData.eMails + " phoneNumbers: " + contactDetailsData.phoneNumbers);
            }
            contactDetailsCursor.close();
        }
        return contactDetailsData;
    }

    public String getPhoneNumber(Cursor contactDataCursor)
    {
        String phone = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.PHONE_NUMBER));
        if(phone != null)
            return phone;

        return "";
    }

    public String getEmailAddresses(Cursor contactDataCursor)
    {
        String email = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.EMAIL));
        if(email != null)
            return  email;

        return "";
    }

    public String getAddress(Cursor contactDataCursor)
    {
        String address = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.POSTAL_ADDRESS));
        if(address != null)
            return  address;

        return "";
    }

    public String getIM(Cursor contactDataCursor)
    {
        String instantMessenger = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.INSTANT_MESSENGER));
        if(instantMessenger != null)
            return instantMessenger;

        return "";
    }

    public String getCompany(Cursor contactDataCursor)
    {
        String company = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.COMPANY));
        if(company != null)
            return company;

        return "";
    }

    public String getJobTitle(Cursor contactDataCursor)
    {
        String jobTitle = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.JOB_TITLE));
        if(jobTitle != null)
            return jobTitle;

        return "";
    }

    public String getNickName(Cursor contactDataCursor)
    {
        String nickName = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.NICK_NAME));
        return nickName;
    }


    public String getWebsite(Cursor contactDataCursor)
    {
        String website = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.WEBSITE));
        if(website != null)
            return website;

        return "";
    }

    public String getNotes(Cursor contactDataCursor)
    {
        String notes = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.NOTE));
        if(notes != null)
            return notes;

        return "";
    }
}
