package com.chebyr.vcardrealm.contacts.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.arch.paging.LivePagedListBuilder;

import android.arch.paging.PagedList;
import android.arch.paging.PositionalDataSource;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.datasource.ContactDataSource;
import com.chebyr.vcardrealm.contacts.datasource.ContactsObserver;

import com.chebyr.vcardrealm.contacts.datasource.TemplateDataSource;
import com.chebyr.vcardrealm.contacts.data.TemplateData;
import com.chebyr.vcardrealm.contacts.data.Contact;

import java.util.HashMap;
import java.util.List;

public class ContactRepository
{
    private static String TAG = ContactRepository.class.getSimpleName();

    private static int pageSize = 4;

    private ContactDataSource.Factory contactsDataSourceFactory;

    public ContactsObserver.Callback callback;

    private PagedList.Config config;

    public ContactRepository(Context context, ContactsObserver.Callback callback)
    {
        config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setEnablePlaceholders(false)
                .build();

        contactsDataSourceFactory = new ContactDataSource.Factory(context, this);
//        Log.d(TAG, "contactsDataSourceFactory: " + contactsDataSourceFactory.toString());
    }

    public LiveData<PagedList<Contact>> loadContactList(String filterState)
    {
        Log.d(TAG, "loadContactList " + "filterState: " + filterState);
        contactsDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(contactsDataSourceFactory, config).build();
    }
}
