package com.chebyr.vcardrealm.contacts.html.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.arch.paging.LivePagedListBuilder;

import android.arch.paging.PagedList;
import android.arch.paging.PositionalDataSource;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.datasource.ContactDataSource;
import com.chebyr.vcardrealm.contacts.html.datasource.ContactsObserver;

import com.chebyr.vcardrealm.contacts.html.datasource.TemplateDataSource;
import com.chebyr.vcardrealm.contacts.html.data.TemplateData;
import com.chebyr.vcardrealm.contacts.html.data.Contact;

import java.util.HashMap;
import java.util.List;

public class ContactRepository
{
    private static String TAG = ContactRepository.class.getSimpleName();

    private static int pageSize = 10;

    private ContactDataSource.Factory contactsDataSourceFactory;
    private TemplateDataSource.Factory templateDataSourceFactory;

    public ContactsObserver.Callback callback;

    private PagedList.Config config;

    public ContactRepository(Context context, ContactsObserver.Callback callback)
    {
        config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setEnablePlaceholders(false)
                .build();

        contactsDataSourceFactory = new ContactDataSource.Factory(context, this);
        templateDataSourceFactory = new TemplateDataSource.Factory(context);

//        Log.d(TAG, "contactsDataSourceFactory: " + contactsDataSourceFactory.toString());
//        Log.d(TAG, "contactDetailsDataSourceFactory: " + contactDetailsDataSourceFactory.toString());
//        Log.d(TAG, "groupsDataSourceFactory: " + groupsDataSourceFactory.toString());
//        Log.d(TAG, "templateDataSourceFactory: " + templateDataSourceFactory.toString());
    }

    public LiveData<PagedList<Contact>> loadContactList(String filterState)
    {
        Log.d(TAG, "loadContactList " + "filterState: " + filterState);
        contactsDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(contactsDataSourceFactory, config).build();
    }

    public LiveData<PagedList<TemplateData>> loadTemplateList(String filterState)
    {
        Log.d(TAG, "loadTemplateList " + "filterState: " + filterState);
        templateDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(templateDataSourceFactory, config).build();
    }

    public LiveData<PagedList<TemplateData>> loadTemplateList(List<Contact> contactDataList)
    {
        Log.d(TAG, "loadTemplateList " + "contactDataList: " + contactDataList);
        templateDataSourceFactory.setContactList(contactDataList);
        return new LivePagedListBuilder<>(templateDataSourceFactory, config).build();
    }
}
