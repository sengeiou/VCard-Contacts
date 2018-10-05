package com.chebyr.vcardrealm.contacts.html.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.contacts.common.list.ContactListItemView;
import com.chebyr.vcardrealm.contacts.R;

import java.io.InputStream;

public class ContactCardView extends ContactListItemView implements WebView.OnClickListener
{
    private WebView webView;
    private WebViewPhotoProvider webViewPhotoProvider;

    public ContactCardView(Context context)
    {
        super(context);
        initialize();
    }

    public ContactCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public void initialize()
    {
        getRootView().findViewById(R.id.web_view);
        setOnClickListener(this);

        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //webSettings.setBlockNetworkImage(true);
        webSettings.setBlockNetworkLoads(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webSettings.setOffscreenPreRaster(true);

        webView.setWebViewClient(webViewPhotoProvider);
    }

    public void setContactPhoto(InputStream contactPhotoStream)
    {
        webViewPhotoProvider.setContactPhoto(contactPhotoStream);
    }

    public void setBackgroundPhoto(InputStream backgroundPhotoStream)
    {
        webViewPhotoProvider.setBackgroundPhoto(backgroundPhotoStream);
    }

    public void setLogoPhoto(InputStream logoPhotoStream)
    {
        webViewPhotoProvider.setLogoPhoto(logoPhotoStream);
    }

    public void loadUrl(String html)
    {
        webView.loadUrl(html);
    }

    @Override
    public void onClick(View view)
    {
        if(view == this)
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

}
