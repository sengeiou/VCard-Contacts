package com.chebyr.vcardrealm.contacts.html;

import android.net.Uri;

import java.io.InputStream;

public class Contact
{
    public long contactID;
    public String lookupKey;
    public Uri contactUri;
    public String photoUriString;

    public String incomingNumber;
    public Uri photoURI;
    public InputStream photoStream;
    public String displayName;
    public String organization;
    public String jobTitle;
    public String phoneNumbers;
    public String IMs;
    public String eMails;
    public String nickName;
    public String groups;
    public String address;
    public String website;
    public String notes;

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

    public Contact(Long id, String lookupKey, String name)
    {

    }

    public long getId()
    {
        return contactID;
    }

    @Override
    public boolean equals(Object object)
    {
        if(object.getClass() == Contact.class)
        {
            Contact newContact = (Contact)object;
            return contactID == newContact.contactID;
        }
        return false;
    }

    public String getHtml()
    {
        return null;
    }
}
