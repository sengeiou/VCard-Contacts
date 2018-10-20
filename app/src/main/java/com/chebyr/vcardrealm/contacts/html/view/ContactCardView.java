package com.chebyr.vcardrealm.contacts.html.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.chebyr.vcardrealm.contacts.html.data.Contact;

public class ContactCardView extends WebView implements WebView.OnClickListener
{
    private static String TAG = ContactCardView.class.getSimpleName();

    private Context context;
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
        //webView = getRootView().findViewById(R.id.web_view);
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
    }

    public void setContact(Contact contact)
    {
        if(contact == null)
        {
            Log.d(TAG, "contact: null");
            return;
        }

        //Log.d(TAG, contact.vcardHtml);
        webViewResourceProvider.setContact(contact);
        //loadData(contact.vcardHtml, "text/html", null);
        loadDataWithBaseURL("file:///android_asset/",contact.vcardHtml, "text/html", null, null);
    }

    @Override
    public void onClick(View view)
    {
        if(view == this)
        {
            WebView.HitTestResult hitTestResult = getHitTestResult();
            switch (hitTestResult.getType())
            {
                case WebView.HitTestResult.EMAIL_TYPE:
                case WebView.HitTestResult.GEO_TYPE:
                case WebView.HitTestResult.PHONE_TYPE:
                case WebView.HitTestResult.SRC_ANCHOR_TYPE:
            }
        }
    }

}
