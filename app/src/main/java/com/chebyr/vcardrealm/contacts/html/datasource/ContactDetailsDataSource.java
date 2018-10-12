package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;
import com.chebyr.vcardrealm.contacts.html.datasource.queries.ContactDetailsQuery;
import com.chebyr.vcardrealm.contacts.html.repository.ContactRepository;

import java.util.List;

public class ContactDetailsDataSource extends PositionalDataSource<ContactDetailsData>
{
    private ContentResolver contentResolver;
    private Cursor contactDetailsCursor = null;

    List<ContactData> contactDataList;

    public ContactDetailsDataSource(Context context, List<ContactData> contactDataList)
    {
        this.contactDataList = contactDataList;
        contentResolver = context.getContentResolver();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ContactDetailsData> callback)
    {

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ContactDetailsData> callback)
    {

    }

    public ContactDetailsData getContactInfo(long contactID)
    {
        ContactDetailsData contactDetails = new ContactDetailsData();

        Cursor contactDataCursor = getContactData(contactID);
        String mimeType;
        String separator = ", ";

        for(contactDataCursor.moveToFirst(); !contactDataCursor.isAfterLast(); contactDataCursor.moveToNext())
        {
            mimeType = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.MIMETYPE));

            switch (mimeType)
            {
                case ContactDetailsQuery.ORGANIZATION_MIME:
                {
                    contactDetails.organization = getCompany(contactDataCursor);
                    contactDetails.jobTitle = getJobTitle(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.NICK_NAME_MIME:
                {
                    contactDetails.nickName = getNickName(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.WEBSITE_MIME:
                {
                    contactDetails.website = getWebsite(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.ADDRESS_MIME:
                {
                    contactDetails.address = getAddress(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.PHONE_MIME:
                {
                    String phone = getPhoneNumber(contactDataCursor);

                    if ((contactDetails.phoneNumbers.length() > 0) && (phone.length() > 0))
                        contactDetails.phoneNumbers += separator + phone;
                    else
                        contactDetails.phoneNumbers = phone;

                    break;
                }
                case ContactDetailsQuery.IM_MIME:
                {
                    String instantMessenger = getIM(contactDataCursor);

                    if ((contactDetails.IMs.length() > 0) && (instantMessenger.length() > 0))
                        contactDetails.IMs += separator + instantMessenger;
                    else
                        contactDetails.IMs = instantMessenger;

                    break;
                }
                case ContactDetailsQuery.NOTE_MIME:
                {
                    contactDetails.notes = getNotes(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.EMAIL_MIME:
                {
                    String email = getEmailAddresses(contactDataCursor);

                    if ((contactDetails.eMails.length() > 0) && (email.length() > 0))
                        contactDetails.eMails += separator + email;
                    else
                        contactDetails.eMails = email;

                    break;
                }
                case ContactDetailsQuery.GROUP_MIME:
                {
                    contactDetails.groupRowID = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.GROUP_ROW_ID));
                    break;
                }
            }
        }
        contactDataCursor.close();

        return contactDetails;
    }

    public Cursor getContactData(long contactID)
    {
        String[] whereParameters = new String[]{String.valueOf(contactID)};

        Cursor contactDataCursor = contentResolver.query(ContactDetailsQuery.URI, ContactDetailsQuery.PROJECTION, ContactDetailsQuery.SELECTION, whereParameters, null);
        return contactDataCursor;
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
        if(nickName != null)
            return nickName;

        return "";
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

    public static class Factory extends DataSource.Factory<Integer, ContactDetailsData>
    {
        private Context context;
        private List<ContactData> contactDataList;

        public Factory(Context context)
        {
            this.context = context;
        }

        public void setFilter(String filterState)
        {

        }

        public void setContactDataList(List<ContactData> contactDataList)
        {
            this.contactDataList = contactDataList;
        }

        @Override
        public DataSource<Integer, ContactDetailsData> create()
        {
            return new ContactDetailsDataSource(context, contactDataList);
        }
    }

}
