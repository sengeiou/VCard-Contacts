package com.chebyr.vcardrealm.contacts.html.model;

import android.arch.paging.DataSource;
import android.content.ContentResolver;

import com.chebyr.vcardrealm.contacts.html.Contact;

public class ContactsDataSourceFactory extends DataSource.Factory<Integer, Contact>
{
    private ContentResolver contentResolver;

    public ContactsDataSourceFactory(ContentResolver contentResolver)
    {
        this.contentResolver = contentResolver;
    }

    public void setFilter(String filterState)
    {

    }

    @Override
    public DataSource<Integer, Contact> create()
    {
        return new ContactsDataSource(contentResolver);
    }
}
