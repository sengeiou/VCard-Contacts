package com.chebyr.vcardrealm.contacts.html.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.data.Contact;
import com.chebyr.vcardrealm.contacts.html.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.data.TemplateData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactList extends MediatorLiveData<PagedList<Contact>>
{
    private static String TAG = ContactList.class.getSimpleName();

    private TemplateParser templateParser;

    public ContactList(Context context)
    {
        templateParser = new TemplateParser(context);
    }
    
    // Use to filter
    //Stream<Contact> contactStream;
    //Stream<Contact> filteredContactStream = contactStream.filter(contact -> contact.data.contactID == contactData.contactID);

    public void mergeContactData(LiveData<PagedList<Contact>> contactLiveData,
                                 LiveData<HashMap<Integer,TemplateData>> templateLiveData)
    {
        Log.d(TAG, "Merge contact data");

        addSource(contactLiveData, this::addContactDataList);
        addSource(templateLiveData, this::addTemplatesList);
    }

    private void addContactDataList(PagedList<Contact> contactPagedList)
    {
        for(Contact contact: contactPagedList)
        {
//            Log.d(TAG, "addContactDataList: " + contactData.displayName);
            if(contact != null)
            {
                contact.vcardHtml = templateParser.generateVCardHtml(contact);
            }
        }
        setValue(contactPagedList);
    }

    private void addTemplatesList(HashMap<Integer,TemplateData> templateDataList)
    {
        PagedList<Contact> contactPagedList = getValue();

        for(int count = 0; count < contactPagedList.size(); count++)
        {
            Contact contact = contactPagedList.get(count);
//            Log.d(TAG, "addTemplatesList: " + templateData.contactID);

            TemplateData templateData = templateDataList.get(contact.contactID);
            if(templateData != null)
            {
                contact.vcardHtml = templateParser.generateVCardHtml(contact);
                contactPagedList.set(count, contact);
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
