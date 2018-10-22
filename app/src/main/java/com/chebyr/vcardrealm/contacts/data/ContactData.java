package com.chebyr.vcardrealm.contacts.data;

import android.graphics.Bitmap;
import android.net.Uri;

public class ContactData
{
    public String lookupKey;
    public Uri contactUri;
    public String displayName;
    public Uri photoThumbnailUri;
    public Uri photoUri;

    public void setPhotoUri(String uriString)
    {
        if(uriString != null)
            this.photoUri = Uri.parse(uriString);
    }

    public void setPhotoThumbnailUri(String uriString)
    {
        if(uriString != null)
            this.photoThumbnailUri = Uri.parse(uriString);
    }
}
