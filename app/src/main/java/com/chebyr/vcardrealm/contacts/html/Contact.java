package com.chebyr.vcardrealm.contacts.html;

import android.net.Uri;

import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;

import java.io.InputStream;

public class Contact
{
    public ContactData data;
    public ContactDetailsData details;
    public GroupData groupData;

    public Uri contactUri;
    public Uri photoURI;

    public InputStream photoStream;
    public InputStream logoPhotoStream;
    public InputStream backgroundPhotoStream;

    public String templateHtml;

    public String path;

    public Contact()
    {

    }

    public Contact(String path)
    {
        this.path = path;
    }

    public long getId()
    {
        return data.contactID;
    }

    public void addContactData(ContactData contactData)
    {
        this.data = contactData;
    }

    public void addContactDetailsData(ContactDetailsData contactDetails)
    {
        this.details = contactDetails;
    }

    public void addGroupData(GroupData groupData)
    {
        this.groupData = groupData;
    }


    @Override
    public boolean equals(Object object)
    {
        if(object.getClass() == Contact.class)
        {
            Contact newContact = (Contact)object;
            return data.contactID == newContact.data.contactID;
        }
        return false;
    }

    public String getHtml()
    {
        return null;
    }
}
