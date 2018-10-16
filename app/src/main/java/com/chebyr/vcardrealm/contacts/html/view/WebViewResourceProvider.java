package com.chebyr.vcardrealm.contacts.html.view;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chebyr.vcardrealm.contacts.html.data.Contact;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

class WebViewResourceProvider extends WebViewClient
{
    private Contact contact;

    public void setContact(Contact contact)
    {
        this.contact = contact;
    }

    public InputStream getTextStream(String text)
    {
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        return inputStream;
    }

    // TODO: Don't do
    public InputStream getBitmapStream(Bitmap photo)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100 , byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        InputStream contactPhotoStream = new ByteArrayInputStream(bytes);
        return contactPhotoStream;
    }

//      Note: This method is called on a thread other than the UI thread so clients should exercise caution when accessing private data
//      or the view system.
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
    {
        String pathSegment = request.getUrl().getLastPathSegment();
        if(pathSegment.contains("photo.png"))
        {
            return new WebResourceResponse("", "", getBitmapStream(contact.data.photo));
        }
        else if(pathSegment.contains("background.png"))
        {
            return new WebResourceResponse("", "", getBitmapStream(contact.template.backgroundPhoto));
        }
        else if(pathSegment.contains("logo.png"))
        {
            return new WebResourceResponse("", "", getBitmapStream(contact.template.logoPhoto));
        }
        else if(pathSegment.contains(".css"))
        {
            return new WebResourceResponse("", "", getTextStream(contact.template.css));
        }
        return null;
    }

    // Inject CSS method: read style.css from assets folder
    // Append stylesheet to document head
    private String injectCSS(byte[] buffer)
    {
        String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
        String css = ("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "style.innerHTML = window.atob('" + encoded + "');" +
                "parent.appendChild(style)" +
                "})()");
        return css;
    }

    public String getHtml()
    {
        String unencodedHtml =
                "<html><body>'%28' is the code for '('</body></html>";
        String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(), Base64.NO_PADDING);
        return null;
    }

}
