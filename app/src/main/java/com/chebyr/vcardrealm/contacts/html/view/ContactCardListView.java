package com.chebyr.vcardrealm.contacts.html.view;

import android.app.Activity;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.data.Contact;

public class ContactCardListView extends RecyclerView implements ContactCardListViewAdapter.OnItemClickCallBack
{
    private static String TAG = ContactCardListView.class.getSimpleName();
    private ContactCardListViewAdapter mContactCardListViewAdapter;

    public ContactCardListView(Context context)
    {
        super(context);
    }

    public ContactCardListView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public void initialize(Activity activity)
    {
        Log.d(TAG, "initialize");
        mContactCardListViewAdapter = new ContactCardListViewAdapter();
        mContactCardListViewAdapter.setCallback(this);
        setAdapter(mContactCardListViewAdapter);

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
                mContactCardListViewAdapter.onScrollStateChanged(newState);
            }
        });
    }

    public void setContactList(PagedList<Contact> contactList)
    {
        mContactCardListViewAdapter.setContactList(contactList);
    }

    @Override
    public void onSelectionCleared(int type, String extra)
    {

    }

    public interface OnItemClickCallBack
    {
        void onSelectionCleared(int type, String extra);
    }
}
