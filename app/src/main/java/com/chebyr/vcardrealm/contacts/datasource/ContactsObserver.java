package com.chebyr.vcardrealm.contacts.datasource;

import android.database.DataSetObserver;

public class ContactsObserver extends DataSetObserver
{
    private Callback callback;
    public boolean mDataValid = false;

    public ContactsObserver(Callback callback)
    {
        this.callback = callback;
    }

    @Override
    public void onChanged()
    {
        super.onChanged();
        mDataValid = true;
        callback.onDataSetChanged();
    }

    @Override
    public void onInvalidated()
    {
        super.onInvalidated();
        mDataValid = false;
        callback.onDataSetChanged();
    }

    public interface Callback
    {
        void onDataSetChanged();
    }
}
