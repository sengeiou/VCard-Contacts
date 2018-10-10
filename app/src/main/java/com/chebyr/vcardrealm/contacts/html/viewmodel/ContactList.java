package com.chebyr.vcardrealm.contacts.html.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;

import com.chebyr.vcardrealm.contacts.html.datasource.data.TemplateData;

import java.util.ArrayList;
import java.util.List;

public class ContactList extends MediatorLiveData<List<Contact>>
{
    private static String TAG = ContactList.class.getSimpleName();

    private TemplateParser templateParser;

    public ContactList(Context context)
    {
        setValue(new ContactArrayList());
        templateParser = new TemplateParser(context);
    }
    
    // Use to filter
    //Stream<Contact> contactStream;
    //Stream<Contact> filteredContactStream = contactStream.filter(contact -> contact.data.contactID == contactData.contactID);

    public void mergeContactData(LiveData<PagedList<ContactData>> contactLiveData,
                                 LiveData<PagedList<ContactDetailsData>> contactDetailsLiveData,
                                 LiveData<PagedList<GroupData>> groupLiveData,
                                 LiveData<PagedList<TemplateData>> templateLiveData)
    {
        Log.d(TAG, "Merge contact data");

        addSource(contactLiveData, contactDataList -> addContactData(contactDataList));
        addSource(contactDetailsLiveData, contactDetailsDataList -> addContactDetails(contactDetailsDataList));
        addSource(groupLiveData, groupDataList -> addGroups(groupDataList));
        addSource(templateLiveData, templateDataList -> addTemplates(templateDataList));
    }

    public void addContactData(PagedList<ContactData> contactDataList)
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
    }

    public void addContactDetails(PagedList<ContactDetailsData> contactDetailsDataList)
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
    }

    public void addGroups(PagedList<GroupData> groupDataList)
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
    }

    public void addTemplates(PagedList<TemplateData> templateDataList)
    {
        for(TemplateData templateData: templateDataList)
        {
            List<Contact> contactPagedList = getValue();
            Contact contact = contactPagedList.get((int)templateData.contactID);
            if(contact == null)
            {
                Contact newContact = new Contact();
                newContact.templateData = templateData;
                contactPagedList.add(newContact);
            }
            else
            {
                contact.templateData = templateData;
            }
        }

    }

    public void generateVCards()
    {
        List<Contact> contactPagedList = getValue();

        for(Contact contact: contactPagedList)
        {
            templateParser.parseInputStream(contact.templateData.inputStream);
        }
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
