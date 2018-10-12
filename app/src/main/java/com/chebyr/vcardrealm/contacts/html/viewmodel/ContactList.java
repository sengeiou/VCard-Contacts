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

        addSource(contactLiveData, this::addContactDataList);//contactDataList -> addContactDataList(contactDataList));
        addSource(contactDetailsLiveData, this::addContactDetailsList);//contactDetailsDataList -> addContactDetailsList(contactDetailsDataList));
        addSource(groupLiveData, this::addGroupsList);//groupDataList -> addGroupsList(groupDataList));
        addSource(templateLiveData, this::addTemplatesList);//templateDataList -> addTemplatesList(templateDataList));
    }

    private void addContactDataList(PagedList<ContactData> contactDataList)
    {
        for(ContactData contactData: contactDataList)
        {
            Log.d(TAG, "addContactDataList: " + contactData.displayName);

            ContactArrayList contactList = (ContactArrayList) getValue();

            Contact contact = contactList.get((int)contactData.contactID);

            if(contact == null)
            {
                contact = new Contact();
                contact.data = contactData;
                contactList.add(contact);
            }
            else
            {
                contact.data = contactData;
            }

            contact.templateHtml = templateParser.generateVCardHtml(contact);
        }
    }

    private void addContactDetailsList(PagedList<ContactDetailsData> contactDetailsDataList)
    {
        for(ContactDetailsData contactDetailsData: contactDetailsDataList)
        {
            Log.d(TAG, "addContactDetailsList: " + contactDetailsData.contactID);

            ContactArrayList contactPagedList = (ContactArrayList)getValue();
            Contact contact = contactPagedList.get((int)contactDetailsData.contactID);
            if(contact == null)
            {
                contact = new Contact();
                contact.details = contactDetailsData;
                contactPagedList.add(contact);
            }
            else
            {
                contact.details = contactDetailsData;
            }

            contact.templateHtml = templateParser.generateVCardHtml(contact);
        }
    }

    private void addGroupsList(PagedList<GroupData> groupDataList)
    {
        for(GroupData groupData: groupDataList)
        {
            Log.d(TAG, "addGroupsList: " + groupData.contactID);

            ContactArrayList contactPagedList = (ContactArrayList)getValue();
            Contact contact = contactPagedList.get((int)groupData.contactID);
            if(contact == null)
            {
                contact = new Contact();
                contact.groups = groupData;
                contactPagedList.add(contact);
            }
            else
            {
                contact.groups = groupData;
            }

            contact.templateHtml = templateParser.generateVCardHtml(contact);
        }
    }

    private void addTemplatesList(PagedList<TemplateData> templateDataList)
    {
        for(TemplateData templateData: templateDataList)
        {
            Log.d(TAG, "addTemplatesList: " + templateData.contactID);

            ContactArrayList contactPagedList = (ContactArrayList)getValue();
            Contact contact = contactPagedList.get((int)templateData.contactID);
            if(contact == null)
            {
                contact = new Contact();
                contact.template = templateData;
                contactPagedList.add(contact);
            }
            else
            {
                contact.template = templateData;
            }

            contact.templateHtml = templateParser.generateVCardHtml(contact);
        }
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

    private class ContactArrayList extends ArrayList<Contact>
    {
        @Override
        public Contact get(int index)
        {
            for(Contact contact: this)
            {
                if(contact.data != null)
                    if(contact.data.contactID == index)
                        return contact;
            }
            return null;
        }
    }
}
