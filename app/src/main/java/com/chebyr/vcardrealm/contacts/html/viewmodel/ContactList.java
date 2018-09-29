package com.chebyr.vcardrealm.contacts.html.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.net.Uri;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.Contact;

public class ContactList extends LiveData<PagedList<Contact>>
{
    private static String TAG = ContactList.class.getSimpleName();

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
