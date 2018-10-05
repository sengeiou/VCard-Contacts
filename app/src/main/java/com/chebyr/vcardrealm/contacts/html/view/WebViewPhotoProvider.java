package com.chebyr.vcardrealm.contacts.html.view;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

class WebViewPhotoProvider extends WebViewClient
{
    private InputStream contactPhotoStream;
    private InputStream backgroundPhotoStream;
    private InputStream logoPhotoStream;

    public void setContactPhoto(InputStream contactPhotoStream)
    {
        this.contactPhotoStream = contactPhotoStream;
    }

    public void setBackgroundPhoto(InputStream backgroundPhotoStream)
    {
        this.backgroundPhotoStream = backgroundPhotoStream;
    }

    public void setLogoPhoto(InputStream logoPhotoStream)
    {
        this.logoPhotoStream = logoPhotoStream;
    }

    // TODO: Don't do 
    public void setContactPhoto(Bitmap photo)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100 , byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        contactPhotoStream = new ByteArrayInputStream(bytes);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
    {
        String pathSegment = request.getUrl().getLastPathSegment();
        if(pathSegment.contains("photo.png"))
        {
            return new WebResourceResponse("", "", contactPhotoStream);
        }
        else if(pathSegment.contains("background.png"))
        {
            return new WebResourceResponse("", "", backgroundPhotoStream);
        }
        else if(pathSegment.contains("logo.png"))
        {
            return new WebResourceResponse("", "", logoPhotoStream);
        }
        return null;
    }
}
