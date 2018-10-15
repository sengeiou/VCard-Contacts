package com.chebyr.vcardrealm.contacts.html.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.contacts.common.list.ContactListItemView;
import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.html.viewmodel.Contact;

import java.io.InputStream;

public class ContactCardView extends ContactListItemView implements WebView.OnClickListener
{
    private static String TAG = ContactCardView.class.getSimpleName();

    private WebView webView;
    private WebViewResourceProvider webViewResourceProvider;

    public ContactCardView(Context context)
    {
        super(context);
    }

    public ContactCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void initialize()
    {
        webView = getRootView().findViewById(R.id.web_view);
        setOnClickListener(this);

        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //webSettings.setBlockNetworkImage(true);
        webSettings.setBlockNetworkLoads(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webSettings.setOffscreenPreRaster(true);

        webViewResourceProvider = new WebViewResourceProvider();
        webView.setWebViewClient(webViewResourceProvider);
    }

    public void setContact(Contact contact)
    {
        if(contact == null)
            return;

        webViewResourceProvider.setContact(contact);
        webView.loadData(contact.vcardHtml, "text/html", "base64");
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
