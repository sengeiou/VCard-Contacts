package com.chebyr.vcardrealm.contacts.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.data.Contact;
import com.chebyr.vcardrealm.contacts.data.TemplateData;
import com.chebyr.vcardrealm.contacts.datasource.TemplateParser;

import java.util.HashMap;
import java.util.ListIterator;

public class ContactList extends MediatorLiveData<PagedList<Contact>>
{
    private static String TAG = ContactList.class.getSimpleName();

    // Use to filter
    //Stream<Contact> contactStream;
    //Stream<Contact> filteredContactStream = contactStream.filter(contact -> contact.data.contactID == contactData.contactID);

    private void addContactDataList(PagedList<Contact> contactPagedList)
    {
        for(Contact contact: contactPagedList)
        {
//            Log.d(TAG, "addContactDataList: " + contactData.displayName);
            if(contact != null)
            {
            }
        }
        setValue(contactPagedList);
    }

    public Uri getContactUri(int position)
    {
        Contact contact = getValue().get(position);
        return contact.data.contactUri;
    }

    public Contact getContact(int position)
    {
        Contact contact = getValue().get(position);

        return contact;
    }

    public int getItemCount()
    {
        int size = getValue().size();
        Log.d(TAG, "Contact List size: " + size);
        return size;
    }

    public Contact lookupNumber(String incomingNumber)
    {
        return null;
    }
}
