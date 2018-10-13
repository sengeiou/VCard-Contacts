package com.chebyr.vcardrealm.contacts.html.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;

import android.arch.paging.PagedList;
import android.content.Context;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.datasource.ContactDataSource;
import com.chebyr.vcardrealm.contacts.html.datasource.ContactDetailsDataSource;
import com.chebyr.vcardrealm.contacts.html.datasource.ContactsObserver;
import com.chebyr.vcardrealm.contacts.html.datasource.GroupDataSource;

import com.chebyr.vcardrealm.contacts.html.datasource.TemplateDataSource;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.TemplateData;

import java.util.List;

public class ContactRepository
{
    private static String TAG = ContactRepository.class.getSimpleName();

    private static int pageSize = 10;

    private ContactDataSource.Factory contactsDataSourceFactory;
    private ContactDetailsDataSource.Factory contactDetailsDataSourceFactory;
    private GroupDataSource.Factory groupsDataSourceFactory;
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
        contactDetailsDataSourceFactory = new ContactDetailsDataSource.Factory(context);
        groupsDataSourceFactory = new GroupDataSource.Factory(context);
        templateDataSourceFactory = new TemplateDataSource.Factory(context);

//        Log.d(TAG, "contactsDataSourceFactory: " + contactsDataSourceFactory.toString());
//        Log.d(TAG, "contactDetailsDataSourceFactory: " + contactDetailsDataSourceFactory.toString());
//        Log.d(TAG, "groupsDataSourceFactory: " + groupsDataSourceFactory.toString());
//        Log.d(TAG, "templateDataSourceFactory: " + templateDataSourceFactory.toString());
    }

    public LiveData<PagedList<ContactData>> loadContactList(String filterState)
    {
        Log.d(TAG, "loadContactList " + "filterState: " + filterState);
        contactsDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(contactsDataSourceFactory, config).build();
    }

    public LiveData<PagedList<ContactDetailsData>> loadContactDetailsList(String filterState)
    {
        Log.d(TAG, "loadContactDetailsList " + "filterState: " + filterState);
        contactDetailsDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(contactDetailsDataSourceFactory, config).build();
    }

    public LiveData<PagedList<ContactDetailsData>> loadContactDetailsList(List<ContactData> contactDataList)
    {
        Log.d(TAG, "loadContactDetailsList " + "contactDataList: " + contactDataList);
        contactDetailsDataSourceFactory.setContactDataList(contactDataList);
        return new LivePagedListBuilder<>(contactDetailsDataSourceFactory, config).build();
    }

    public LiveData<PagedList<GroupData>> loadGroupList(String filterState)
    {
        Log.d(TAG, "loadGroupList " + "filterState: " + filterState);
        groupsDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(groupsDataSourceFactory, config).build();
    }

    public LiveData<PagedList<GroupData>> loadGroupList(List<ContactDetailsData> contactDetailsDataList)
    {
        Log.d(TAG, "loadGroupList " + "contactDetailsDataList: " + contactDetailsDataList);
        groupsDataSourceFactory.setContactDetailsDataList(contactDetailsDataList);
        return new LivePagedListBuilder<>(groupsDataSourceFactory, config).build();
    }

    public LiveData<PagedList<TemplateData>> loadTemplateList(String filterState)
    {
        Log.d(TAG, "loadTemplateList " + "filterState: " + filterState);
        templateDataSourceFactory.setFilter(filterState);
        return new LivePagedListBuilder<>(templateDataSourceFactory, config).build();
    }

    public LiveData<PagedList<TemplateData>> loadTemplateList(List<ContactData> contactDataList)
    {
        Log.d(TAG, "loadTemplateList " + "contactDataList: " + contactDataList);
        templateDataSourceFactory.setContactDataList(contactDataList);
        return new LivePagedListBuilder<>(templateDataSourceFactory, config).build();
    }
}
