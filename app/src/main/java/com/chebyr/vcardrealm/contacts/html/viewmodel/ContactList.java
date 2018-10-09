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

import java.util.ArrayList;
import java.util.List;

public class ContactList extends MediatorLiveData<List<Contact>>
{
    private static String TAG = ContactList.class.getSimpleName();
    private static int pageSize = 10;

    public ContactList()
    {
        setValue(new ContactArrayList());
    }
    
    // Use to filter
    //Stream<Contact> contactStream;
    //Stream<Contact> filteredContactStream = contactStream.filter(contact -> contact.data.contactID == contactData.contactID);

    public void mergeContactData(LiveData<PagedList<ContactData>> contactLiveData, LiveData<PagedList<ContactDetailsData>> contactDetailsLiveData, LiveData<PagedList<GroupData>> groupLiveData)
    {
        Log.d(TAG, "Merge contact data");

        addSource(contactLiveData, contactDataList ->
        {
            Log.d(TAG, "contactDataList - No of contacts: " + contactDataList.size());
            for(ContactData contactData: contactDataList)
            {
                Log.d(TAG, "contactData: " + contactData.displayName);

                List<Contact> contactPagedList = getValue();
                Log.d(TAG, "contactPagedList: " + contactPagedList);

                Contact contact = contactPagedList.get((int)contactData.contactID);
                if(contact == null)
                {
                    Contact newContact = new Contact();
                    newContact.data = contactData;
                    contactPagedList.add(newContact);
                }
                else
                {
                    contact.data = contactData;
                }
            }
        });

        addSource(contactDetailsLiveData, contactDetailsDataList ->
        {
            for(ContactDetailsData contactDetailsData: contactDetailsDataList)
            {
                List<Contact> contactPagedList = getValue();
                Contact contact = contactPagedList.get((int)contactDetailsData.contactID);
                if(contact == null)
                {
                    Contact newContact = new Contact();
                    newContact.details = contactDetailsData;
                    contactPagedList.add(newContact);
                }
                else
                {
                    contact.details = contactDetailsData;
                }
            }
        });

        addSource(groupLiveData, groupDataList ->
        {
            for(GroupData groupData: groupDataList)
            {
                List<Contact> contactPagedList = getValue();
                Contact contact = contactPagedList.get((int)groupData.contactID);
                if(contact == null)
                {
                    Contact newContact = new Contact();
                    newContact.groupData = groupData;
                    contactPagedList.add(newContact);
                }
                else
                {
                    contact.groupData = groupData;
                }
            }
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

    private class ContactArrayList extends ArrayList<Contact>
    {
        @Override
        public Contact get(int index)
        {
            for(Contact contact: this)
            {
                if(contact.data.contactID == index)
                    return contact;
            }
            return null;
        }
    }
}
