package com.chebyr.vcardrealm.contacts.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chebyr.vcardrealm.contacts.data.Contact;
import com.chebyr.vcardrealm.contacts.utils.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

class WebViewResourceProvider extends WebViewClient
{
    private static String TAG = WebViewResourceProvider.class.getSimpleName();
    private static String PHOTO = "photo.jpg";

    private Contact contact;
    private FileUtil fileUtil;

    public WebViewResourceProvider(Context context)
    {
        fileUtil = new FileUtil(context);
    }

    public void setContact(Contact contact)
    {
        this.contact = contact;
    }

    public InputStream getTextStream(String text)
    {
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        return inputStream;
    }

    //      Note: This method is called on a thread other than the UI thread so clients should exercise caution when accessing private data
//      or the view system.
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
    {
        Uri uri = request.getUrl();
        if(uri != null)
        {
            String url = uri.toString();
            if(url != null)
            {
                if(url.contains(PHOTO))
                {
                    return loadPhoto(url);
                }
            }
        }

        //Log.d(TAG, request.getMethod());
        //Log.d(TAG, request.getRequestHeaders().toString());

        /*
        String pathSegment = request.getUrl().getLastPathSegment();

        if(pathSegment.contains("photo.png"))
        {

        }
        else if(pathSegment.contains("background.png"))
        {
            return new WebResourceResponse("", "", getBitmapStream(contact.template.backgroundPhotoPath));
        }
        else if(pathSegment.contains("logo.png"))
        {
            return new WebResourceResponse("", "", getBitmapStream(contact.template.logoPhotoPath));
        }
        else if(pathSegment.contains(".css"))
        {
            return new WebResourceResponse("", "", getTextStream(contact.template.css));
        }*/

        return null;
    }

    private WebResourceResponse loadPhoto(String url)
    {
        Log.d(TAG, "URL: " + url);

        if(contact.data.photoUri != null)
        {
            InputStream bitmapStream = fileUtil.getBitmapContentStream(contact.data.photoUri);
            Log.d(TAG, "bitmapStream: " + bitmapStream);
            WebResourceResponse webResourceResponse = new WebResourceResponse("image/png", "binary", bitmapStream);
            Log.d(TAG, "webResourceResponse: " + webResourceResponse);
            return webResourceResponse;
        }
        else if(contact.data.photoThumbnailUri != null)
        {
            InputStream bitmapStream = fileUtil.getBitmapContentStream(contact.data.photoThumbnailUri);
            Log.d(TAG, "bitmapStream: " + bitmapStream);
            WebResourceResponse webResourceResponse = new WebResourceResponse("image/png", "binary", bitmapStream);
            Log.d(TAG, "webResourceResponse: " + webResourceResponse);
            return webResourceResponse;
        }
        else
        {
            InputStream bitmapStream = fileUtil.getBitmapAssetStream(PHOTO);
            Log.d(TAG, "bitmapStream: " + bitmapStream);
            WebResourceResponse webResourceResponse = new WebResourceResponse("image/png", "binary", bitmapStream);
            Log.d(TAG, "webResourceResponse: " + webResourceResponse);
            return webResourceResponse;
        }
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
