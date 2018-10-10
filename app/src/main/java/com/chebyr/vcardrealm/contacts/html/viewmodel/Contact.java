package com.chebyr.vcardrealm.contacts.html.viewmodel;

import android.net.Uri;

import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.TemplateData;

import java.io.InputStream;

public class Contact
{
    public ContactData data;
    public ContactDetailsData details;
    public GroupData groups;
    public TemplateData template;

    public String templateHtml;

    public Contact()
    {

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
        this.groups = groupData;
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
