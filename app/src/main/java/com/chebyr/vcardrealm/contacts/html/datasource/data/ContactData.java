package com.chebyr.vcardrealm.contacts.html.datasource.data;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.InputStream;

public class ContactData
{
    public long contactID;
    public String lookupKey;
    public Uri contactUri;
    public String displayName;
    public String photoUriString;
    public String incomingNumber;

    public Bitmap photo;
    public Uri photoURI;

    public ContactData()
    {

    }

    public ContactData(Long id, String lookupKey, String name)
    {

    }

}
