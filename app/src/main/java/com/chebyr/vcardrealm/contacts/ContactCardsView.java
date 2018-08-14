package com.chebyr.vcardrealm.contacts;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class ContactCardsView extends RecyclerView implements ContactCardsViewAdapter.OnItemClickCallBack
{
    private ContactCardsViewAdapter mContactCardsViewAdapter;

    public ContactCardsView(Context context)
    {
        super(context);
    }

    public ContactCardsView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public void initialize(Activity activity)
    {
        mContactCardsViewAdapter = new ContactCardsViewAdapter(this);

        setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(llm);
        setItemAnimator(new DefaultItemAnimator());
        setAdapter(mContactCardsViewAdapter);

        addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                mContactCardsViewAdapter.onScrollStateChanged(newState);
            }
        });
    }

    public void addData(String path)
    {
        mContactCardsViewAdapter.addData(path);
    }

    @Override
    public void onSelectionCleared(int type, String extra)
    {

    }

    public interface OnItemClickCallBack
    {
        public void onSelectionCleared(int type, String extra);
    }
}
