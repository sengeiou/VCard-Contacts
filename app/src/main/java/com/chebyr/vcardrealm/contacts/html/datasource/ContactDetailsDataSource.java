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

import java.util.ArrayList;
import java.util.List;

public class ContactDetailsDataSource extends PositionalDataSource<ContactDetailsData>
{
    private ContentResolver contentResolver;
    private Cursor contactDetailsCursor = null;

    private List<ContactData> contactDataList;
    private String filterState;

    public ContactDetailsDataSource(Context context, String filterState, List<ContactData> contactDataList)
    {
        this.contactDataList = contactDataList;
        this.filterState = filterState;
        contentResolver = context.getContentResolver();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ContactDetailsData> callback)
    {
        List<ContactDetailsData> contactDetailsDataList = getContactInfo();
        callback.onResult(contactDetailsDataList, params.requestedStartPosition);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ContactDetailsData> callback)
    {
        List<ContactDetailsData> contactDetailsDataList = getContactInfo();
        callback.onResult(contactDetailsDataList);
    }

    public List<ContactDetailsData> getContactInfo()
    {
        List<ContactDetailsData> contactDetailsDataList = new ArrayList<>();

        if(contactDataList == null)
            return contactDetailsDataList;

        String[] contactIDArray = new String[contactDataList.size()];

        for(int count = 0; count < contactDataList.size(); count++)
            contactIDArray[count] = String.valueOf(contactDataList.get(count).contactID);


        Cursor contactDataCursor = contentResolver.query(ContactDetailsQuery.URI, ContactDetailsQuery.PROJECTION, ContactDetailsQuery.SELECTION, contactIDArray, null);

        String mimeType;
        String separator = ", ";

        for(contactDataCursor.moveToFirst(); !contactDataCursor.isAfterLast(); contactDataCursor.moveToNext())
        {
            ContactDetailsData contactDetailsData = new ContactDetailsData();
            mimeType = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.MIMETYPE));

            switch (mimeType)
            {
                case ContactDetailsQuery.ORGANIZATION_MIME:
                {
                    contactDetailsData.organization = getCompany(contactDataCursor);
                    contactDetailsData.jobTitle = getJobTitle(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.NICK_NAME_MIME:
                {
                    contactDetailsData.nickName = getNickName(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.WEBSITE_MIME:
                {
                    contactDetailsData.website = getWebsite(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.ADDRESS_MIME:
                {
                    contactDetailsData.address = getAddress(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.PHONE_MIME:
                {
                    String phone = getPhoneNumber(contactDataCursor);

                    if ((contactDetailsData.phoneNumbers.length() > 0) && (phone.length() > 0))
                        contactDetailsData.phoneNumbers += separator + phone;
                    else
                        contactDetailsData.phoneNumbers = phone;

                    break;
                }
                case ContactDetailsQuery.IM_MIME:
                {
                    String instantMessenger = getIM(contactDataCursor);

                    if ((contactDetailsData.IMs.length() > 0) && (instantMessenger.length() > 0))
                        contactDetailsData.IMs += separator + instantMessenger;
                    else
                        contactDetailsData.IMs = instantMessenger;

                    break;
                }
                case ContactDetailsQuery.NOTE_MIME:
                {
                    contactDetailsData.notes = getNotes(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.EMAIL_MIME:
                {
                    String email = getEmailAddresses(contactDataCursor);

                    if ((contactDetailsData.eMails.length() > 0) && (email.length() > 0))
                        contactDetailsData.eMails += separator + email;
                    else
                        contactDetailsData.eMails = email;

                    break;
                }
                case ContactDetailsQuery.GROUP_MIME:
                {
                    contactDetailsData.groupRowID = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.GROUP_ROW_ID));
                    break;
                }
            }
            contactDetailsDataList.add(contactDetailsData);
        }
        contactDataCursor.close();

        return contactDetailsDataList;
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
        private String filterState;

        public Factory(Context context)
        {
            this.context = context;
        }

        public void setFilter(String filterState)
        {
            this.filterState = filterState;
        }

        public void setContactDataList(List<ContactData> contactDataList)
        {
            this.contactDataList = contactDataList;
        }

        @Override
        public DataSource<Integer, ContactDetailsData> create()
        {
            return new ContactDetailsDataSource(context, filterState, contactDataList);
        }
    }

}
