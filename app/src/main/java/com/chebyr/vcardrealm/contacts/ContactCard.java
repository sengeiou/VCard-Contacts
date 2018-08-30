package com.chebyr.vcardrealm.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.contacts.common.list.ContactListItemView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ContactCard extends ContactListItemView implements WebView.OnClickListener
{
    private WebView webView;
    private LoadResourceClient loadResourceClient;

    public ContactCard(Context context)
    {
        super(context);
        initialize();
    }

    public ContactCard(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public void initialize()
    {
        webView = getRootView().findViewById(R.id.web_view);
        webView.setOnClickListener(this);

        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //webSettings.setBlockNetworkImage(true);
        webSettings.setBlockNetworkLoads(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webSettings.setOffscreenPreRaster(true);

        webView.setWebViewClient(loadResourceClient);
    }

    public void setHtml(String html)
    {
        webView.loadUrl(html);

        //webView.loadData("<html><body>Hello, world!</body></html>", "text/html", "UTF-8");
//            Log.d(TAG, "Width: " + webView.getWidth() + " Height: " + webView.getHeight());

    }

    @Override
    public void onClick(View view)
    {
        if(view == webView)
        {
            WebView.HitTestResult hitTestResult = webView.getHitTestResult();
            switch (hitTestResult.getType())
            {
                case WebView.HitTestResult.EMAIL_TYPE:
                case WebView.HitTestResult.GEO_TYPE:
                case WebView.HitTestResult.PHONE_TYPE:
                case WebView.HitTestResult.SRC_ANCHOR_TYPE:
            }

            hitTestResult.getExtra();
        }
    }

    private class LoadResourceClient extends WebViewClient
    {
        private InputStream contactPhotoStream;
        private InputStream backgroundPhotoStream;

        public void setImages(InputStream photo, InputStream background)
        {
            contactPhotoStream = photo;
            backgroundPhotoStream = background;
        }

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
            //super.shouldInterceptRequest(view, request);

            String pathSegment = request.getUrl().getLastPathSegment();
            if(pathSegment.contains("photo.png"))
            {
                return new WebResourceResponse("", "", contactPhotoStream);
            }
            else if(pathSegment.contains("background.png"))
            {
                return new WebResourceResponse("", "", backgroundPhotoStream);
            }
            return null;
        }

    }
}
