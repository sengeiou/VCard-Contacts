package com.chebyr.vcardrealm.contacts.html.view;

import android.app.Activity;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.chebyr.vcardrealm.contacts.html.Contact;

public class ContactCardsListView extends RecyclerView implements ContactCardsListViewAdapter.OnItemClickCallBack
{
    private ContactCardsListViewAdapter mContactCardsListViewAdapter;

    public ContactCardsListView(Context context)
    {
        super(context);
    }

    public ContactCardsListView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public void initialize(Activity activity)
    {
        mContactCardsListViewAdapter = new ContactCardsListViewAdapter();
        mContactCardsListViewAdapter.setCallback(this);

        setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(llm);
        setItemAnimator(new DefaultItemAnimator());
        setAdapter(mContactCardsListViewAdapter);

        addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                mContactCardsListViewAdapter.onScrollStateChanged(newState);
            }
        });
    }

    public void setContactPagedList(PagedList<Contact> contactPagedList)
    {
        mContactCardsListViewAdapter.setContactPagedList(contactPagedList);
    }

    public void addData(String path)
    {
        //mContactCardsListViewAdapter.addData(path);
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
