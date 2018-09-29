package com.chebyr.vcardrealm.contacts.html.model;

import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;

import android.arch.paging.PagedList;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.Contact;
import com.chebyr.vcardrealm.contacts.html.viewmodel.ContactList;

public class ContactRepository implements ContactDataSource.Callback
{
    private static String TAG = ContactRepository.class.getSimpleName();

    private Callback callback;

    private static int pageSize = 10;

    private ContactsDataSourceFactory contactsDataSourceFactory;
    private PagedList.Config config;
    private Context context;

    boolean contactLoadingCompleted = false;

    public ContactRepository(Context context, Callback callback)
    {
        this.context = context;
        this.callback = callback;

        config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setEnablePlaceholders(false)
                .build();

        contactsDataSourceFactory = new ContactsDataSourceFactory();
    }

    public ContactList loadContactList(String filterState)
    {
        contactsDataSourceFactory.setFilter(filterState);

        ContactList contactsList = (ContactList) new LivePagedListBuilder<>(
                contactsDataSourceFactory, config).build();

        return contactsList;
    }

    @Override
    public void onContactLoaded(Contact contact)
    {
        Log.d(TAG, "Contact Loaded: " + contact.displayName + " incoming Number: "+ contact.incomingNumber);

        //contacts.add(contact.incomingNumber, contact);

        if(callback != null)
            callback.onDataSetChanged();
    }

    @Override
    public void onContactLoadingCompleted()
    {
        Log.d(TAG, "Contacts loading completed");
        contactLoadingCompleted = true;
    }

    @Override
    public void onDataSetChanged() {
        callback.onDataSetChanged();
    }

    public interface Callback
    {
        void onDataSetChanged();
    }

    public class ContactsDataSourceFactory extends DataSource.Factory<Integer, Contact>
    {

        public ContactsDataSourceFactory()
        {
        }

        public void setFilter(String filterState)
        {

        }

        @Override
        public DataSource<Integer, Contact> create()
        {
            return new ContactDataSource(context, ContactRepository.this);
        }
    }
}
