package com.chebyr.vcardrealm.contacts.html.model;

import android.arch.paging.LivePagedListBuilder;
import android.content.ContentResolver;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.chebyr.vcardrealm.contacts.html.Contact;

public class ContactsRepository
{
    private static String TAG = ContactsRepository.class.getSimpleName();
    private static int pageSize = 10;

    private ContactsDataSourceFactory contactsDataSourceFactory;
    private PagedList.Config config;

    public ContactsRepository(ContentResolver contentResolver)
    {
        config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setEnablePlaceholders(false)
                .build();

        contactsDataSourceFactory = new ContactsDataSourceFactory(contentResolver);
    }

    public LiveData<PagedList<Contact>> loadContactsList(String filterState)
    {
        contactsDataSourceFactory.setFilter(filterState);

        LiveData<PagedList<Contact>> contactsList = new LivePagedListBuilder<>(
                contactsDataSourceFactory, config).build();

        return contactsList;
    }
}
