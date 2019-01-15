package com.chebyr.vcardrealm.contacts.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.chebyr.vcardrealm.contacts.R;

public class ContactCardListView extends RecyclerView
{
    private static String TAG = ContactCardListView.class.getSimpleName();
    private Callback callback;
    private LinearLayoutManager llm;

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
        Log.d(TAG, "setDirectory");
        this.callback = callback;

        setHasFixedSize(true);
        llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(llm);
        setItemAnimator(new DefaultItemAnimator());

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                Log.d(TAG, "onScrollStateChanged: " + newState);
                super.onScrollStateChanged(recyclerView, newState);

//                if(newState == RecyclerView.SCROLL_STATE_IDLE)
//                    callback.onScrolled();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled");
                callback.onScrolled();
            }
        });

        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(TAG, "onLayoutChange()");
                callback.onScrolled();
            }
        });
    }

    public boolean checkVisible(int itemPosition)
    {
        int firstVisible = llm.findFirstCompletelyVisibleItemPosition();
        int lastVisible = llm.findLastCompletelyVisibleItemPosition();

        if((itemPosition >= firstVisible) && (itemPosition <= lastVisible))
            return true;

        return false;
    }

    public View getChildView(int itemPosition)
    {
        Log.d(TAG, "getChildView(int itemPosition) at position: " + itemPosition);

        return llm.findViewByPosition(itemPosition);

        //ContactCardListViewAdapter.ContactCardsViewHolder holder = (ContactCardListViewAdapter.ContactCardsViewHolder)findViewHolderForAdapterPosition(itemPosition);
        //return holder.itemView;//.findViewById(R.id.contact_card_view);
    }

    interface Callback
    {
        void onScrolled();
    }
}
