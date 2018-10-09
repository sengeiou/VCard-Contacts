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

    public ContactList()
    {
        // Use to filter
        //Stream<Contact> contactStream;
        //Stream<Contact> filteredContactStream = contactStream.filter(contact -> contact.data.contactID == contactData.contactID);
    }

    public void mergeContactData(LiveData<PagedList<ContactData>> contactLiveData, LiveData<PagedList<ContactDetailsData>> contactDetailsLiveData, LiveData<PagedList<GroupData>> groupLiveData)
    {
        Log.d(TAG, "Merge contact data");

        addSource(contactLiveData, contactDataList ->
        {
            for(ContactData contactData: contactDataList)
            {
                PagedList<Contact> contactPagedList = getValue();
                Contact contact = contactPagedList.get((int)contactData.contactID);
                if(contact == null)
                {
                    Contact newContact = new Contact();
                    newContact.data = contactData;
                    contactPagedList.add((int)contactData.contactID, newContact);
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
                PagedList<Contact> contactPagedList = getValue();
                Contact contact = contactPagedList.get((int)contactDetailsData.contactID);
                if(contact == null)
                {
                    Contact newContact = new Contact();
                    newContact.details = contactDetailsData;
                    contactPagedList.add((int)contactDetailsData.contactID, newContact);
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
                PagedList<Contact> contactPagedList = getValue();
                Contact contact = contactPagedList.get((int)groupData.contactID);
                if(contact == null)
                {
                    Contact newContact = new Contact();
                    newContact.groupData = groupData;
                    contactPagedList.add((int)groupData.contactID, newContact);
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
}
