package com.chebyr.vcardrealm.contacts.view;

import android.app.Activity;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.data.Contact;

public class ContactCardListView extends RecyclerView
{
    private static String TAG = ContactCardListView.class.getSimpleName();
    private Callback callback;

    public ContactCardListView(Context context)
    {
        super(context);
    }

    public ContactCardListView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public void initialize(Activity activity, Callback callback)
    {
        Log.d(TAG, "initialize");
        this.callback = callback;

        setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(llm);
        setItemAnimator(new DefaultItemAnimator());

        addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                callback.onScrollStateChanged(newState);

            }
        });
    }

    interface Callback
    {
        void onScrollStateChanged(int newState);
    }
}
