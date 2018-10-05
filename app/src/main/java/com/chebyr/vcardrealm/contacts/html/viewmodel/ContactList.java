package com.chebyr.vcardrealm.contacts.html.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.paging.PagedList;
import android.net.Uri;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.Contact;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;

public class ContactList extends MediatorLiveData<PagedList<Contact>>
{
    private static String TAG = ContactList.class.getSimpleName();

    public void mergeContactData(LiveData<PagedList<ContactData>> contactLiveData, LiveData<PagedList<ContactDetailsData>> contactDetailsLiveData, LiveData<PagedList<GroupData>> groupLiveData)
    {
        addSource(contactLiveData, contactData ->
        {
            int a = contactData;
        });

        addSource(contactDetailsLiveData, contactDetailsData ->
        {

        });

        addSource(groupLiveData, groupData ->
        {

        });
    }



    public Uri getContactUri(int position)
    {
        Contact contact = getValue().get(position);
        return contact.contactUri;
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
