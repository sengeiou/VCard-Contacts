package com.chebyr.vcardrealm.contacts.view;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chebyr.vcardrealm.contacts.data.Contact;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class ContactCardView extends WebView implements WebView.OnClickListener
{
    private static String TAG = ContactCardView.class.getSimpleName();

    private Context context;
    private Contact contact;

    private WebViewResourceProvider webViewResourceProvider;

    public ContactCardView(Context context)
    {
        super(context);
        this.context = context;
    }

    public ContactCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }

    public void initialize()
    {
        setOnClickListener(this);

        WebSettings webSettings = getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //webSettings.setBlockNetworkImage(true);
        webSettings.setBlockNetworkLoads(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webSettings.setOffscreenPreRaster(true);

        webViewResourceProvider = new WebViewResourceProvider(context);
        setWebViewClient(webViewResourceProvider);
//        setOnClickListener(this);
    }

    public void setContact(Contact contact)
    {
        if(contact == null)
        {
            Log.d(TAG, "contact: null");
            return;
        }
        this.contact = contact;

        //Log.d(TAG, contact.vcardHtml);
        webViewResourceProvider.setContact(contact);
        Log.d(TAG, contact.template.folderUrl);
        loadDataWithBaseURL(contact.template.folderUrl,contact.vcardHtml, "text/html", null, null);
    }

    public Contact getContact()
    {
        return contact;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                performClick();
                return true;
        }
        return false;
    }

    @Override
    public boolean performClick()
    {
        super.performClick();
//        Log.d(TAG, "WebView clicked");
        return true;
    }

    @Override
    public void onClick(View view)
    {
        if(view == this)
        {
            Log.d(TAG, view.toString());
            WebView.HitTestResult hitTestResult = getHitTestResult();
            Log.d(TAG, hitTestResult.toString());
            switch (hitTestResult.getType())
            {
                case WebView.HitTestResult.EMAIL_TYPE:
                case WebView.HitTestResult.GEO_TYPE:
                case WebView.HitTestResult.PHONE_TYPE:
                case WebView.HitTestResult.SRC_ANCHOR_TYPE:
            }
        }
    }

    public interface OnContactCardClickListener
    {
        void onClick(Contact contact);
    }

    static class WebViewResourceProvider extends WebViewClient
    {
        private static String TAG = WebViewResourceProvider.class.getSimpleName();
        private static String PHOTO_URL = "photo.png";

        private Contact contact;
        public ContentResolver contentResolver;

        public WebViewResourceProvider(Context context)
        {
            contentResolver = context.getContentResolver();
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
                    if(url.contains(PHOTO_URL))
                    {
                        return loadPhoto(url);
                    }
                }
            }
            return null;
        }

        private WebResourceResponse loadPhoto(String url)
        {
            //Log.d(TAG, "URL: " + url);

            if(contact.data.photoUri != null)
            {
                try(InputStream bitmapStream = contentResolver.openInputStream(contact.data.photoUri))
                {
                    return createWebResponse(bitmapStream);
                }
                catch (Exception exception)
                {
                    Log.d(TAG, exception.toString());
                    return null;
                }
            }
            else if(contact.data.photoThumbnailUri != null)
            {
                try(InputStream bitmapStream = contentResolver.openInputStream(contact.data.photoThumbnailUri))
                {
                    return createWebResponse(bitmapStream);
                }
                catch (Exception exception)
                {
                    Log.d(TAG, exception.toString());
                    return null;
                }
            }
            else
            {
                try
                {
                    InputStream bitmapStream = new FileInputStream(contact.template.photoPath);
                    return createWebResponse(bitmapStream);
                }
                catch (Exception exception)
                {
                    Log.d(TAG, exception.toString());
                    return null;
                }

            }
        }

        private WebResourceResponse createWebResponse(InputStream bitmapStream)
        {
            //Log.d(TAG, "bitmapStream: " + bitmapStream);
            WebResourceResponse webResourceResponse = new WebResourceResponse("image/png", "binary", bitmapStream);
            //Log.d(TAG, "webResourceResponse: " + webResourceResponse);
            return webResourceResponse;
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
}
