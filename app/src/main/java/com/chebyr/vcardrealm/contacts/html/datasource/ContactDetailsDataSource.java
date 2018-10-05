package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.chebyr.vcardrealm.contacts.html.Contact;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.queries.ContactDetailsQuery;
import com.chebyr.vcardrealm.contacts.html.repository.ContactRepository;

public class ContactDetailsDataSource extends PositionalDataSource<ContactDetailsData>
{
    private Context context;
    private ContentResolver contentResolver;
    private GroupDataSource groupDataSource;
    private Cursor contactDetailsCursor = null;

    public ContactDetailsDataSource(Context context, ContactRepository contactRepository)
    {
        contentResolver = context.getContentResolver();
        groupDataSource = new GroupDataSource(context, contactRepository);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ContactDetailsData> callback) {

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ContactDetailsData> callback) {

    }

    public Contact getContactInfo(long contactID)
    {
        Contact contact = new Contact();

        Cursor contactDataCursor = getContactData(contact.contactID);
        String mimeType;
        String separator = ", ";

        for(contactDataCursor.moveToFirst(); !contactDataCursor.isAfterLast(); contactDataCursor.moveToNext())
        {
            mimeType = contactDataCursor.getString(contactDataCursor.getColumnIndex(ContactDetailsQuery.MIMETYPE));

            switch (mimeType)
            {
                case ContactDetailsQuery.ORGANIZATION_MIME:
                {
                    contact.organization = getCompany(contactDataCursor);
                    contact.jobTitle = getJobTitle(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.NICK_NAME_MIME:
                {
                    contact.nickName = getNickName(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.WEBSITE_MIME:
                {
                    contact.website = getWebsite(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.ADDRESS_MIME:
                {
                    contact.address = getAddress(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.PHONE_MIME:
                {
                    String phone = getPhoneNumber(contactDataCursor);

                    if ((contact.phoneNumbers.length() > 0) && (phone.length() > 0))
                        contact.phoneNumbers += separator + phone;
                    else
                        contact.phoneNumbers = phone;

                    break;
                }
                case ContactDetailsQuery.IM_MIME:
                {
                    String instantMessenger = getIM(contactDataCursor);

                    if ((contact.IMs.length() > 0) && (instantMessenger.length() > 0))
                        contact.IMs += separator + instantMessenger;
                    else
                        contact.IMs = instantMessenger;

                    break;
                }
                case ContactDetailsQuery.NOTE_MIME:
                {
                    contact.notes = getNotes(contactDataCursor);
                    break;
                }
                case ContactDetailsQuery.EMAIL_MIME:
                {
                    String email = getEmailAddresses(contactDataCursor);

                    if ((contact.eMails.length() > 0) && (email.length() > 0))
                        contact.eMails += separator + email;
                    else
                        contact.eMails = email;

                    break;
                }
                case ContactDetailsQuery.GROUP_MIME:
                {
                    String group = groupDataSource.getGroups(contactDataCursor);
                    if ((contact.groups.length() > 0) && (group.length() > 0))
                        contact.groups += separator + group;
                    else
                        contact.groups = group;

                    break;
                }
            }
        }
        contactDataCursor.close();

        return contact;
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
        public DataSource<Integer, ContactDetailsData> create()
        {
            return new ContactDetailsDataSource(context, contactRepository);
        }
    }

}
