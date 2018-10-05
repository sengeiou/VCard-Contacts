package com.chebyr.vcardrealm.contacts.html.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;

import android.arch.paging.PagedList;
import android.content.Context;

import com.chebyr.vcardrealm.contacts.html.datasource.ContactDataSource;
import com.chebyr.vcardrealm.contacts.html.datasource.ContactDetailsDataSource;
import com.chebyr.vcardrealm.contacts.html.datasource.ContactsObserver;
import com.chebyr.vcardrealm.contacts.html.datasource.GroupDataSource;

import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;

public class ContactRepository
{
    private static String TAG = ContactRepository.class.getSimpleName();

    private static int pageSize = 10;

    private ContactDataSource.Factory contactsDataSourceFactory;
    private ContactDetailsDataSource.Factory contactDetailsDataSourceFactory;
    private GroupDataSource.Factory groupsDataSourceFactory;

    private PagedList.Config config;

    public ContactRepository(Context context, ContactsObserver.Callback callback)
    {
        config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setEnablePlaceholders(false)
                .build();

        contactsDataSourceFactory = new ContactDataSource.Factory(context, this);
        contactDetailsDataSourceFactory = new ContactDetailsDataSource.Factory(context, this);
        groupsDataSourceFactory = new GroupDataSource.Factory(context, this);
    }

    public LiveData<PagedList<ContactData>> loadContactList(String filterState)
    {
        contactsDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(contactsDataSourceFactory, config).build();
    }

    public LiveData<PagedList<ContactDetailsData>> loadContactDetailsList(String filterState)
    {
        contactDetailsDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(contactDetailsDataSourceFactory, config).build();
    }

    public LiveData<PagedList<GroupData>> loadGroupsList(String filterState)
    {
        groupsDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(groupsDataSourceFactory, config).build();
    }
}
