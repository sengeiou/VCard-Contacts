package com.chebyr.vcardrealm.contacts;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.InputStream;

public class Contact
{
    public String incomingNumber;
    public Uri photoURI;
    public InputStream contactPhotoStream;
    public String displayName;
    public String organization;
    public String jobTitle;
    public String phoneNumbers;
    public String IMs;
    public String eMails;
    public String nickName;
    public String groups;
    public String address;
    public String webSite;
    public String notes;

    public InputStream logoPhotoStream;
    public InputStream backgroundPhotoStream;
    public String templateHtml;
}
