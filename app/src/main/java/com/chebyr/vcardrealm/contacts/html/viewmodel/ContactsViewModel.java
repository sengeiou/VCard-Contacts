package com.chebyr.vcardrealm.contacts.html.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import android.arch.paging.PagedList;
import android.content.Context;

import com.chebyr.vcardrealm.contacts.html.Contact;
import com.chebyr.vcardrealm.contacts.html.model.ContactRepository;

public class ContactsViewModel extends AndroidViewModel implements ContactRepository.Callback
{
    private static String TAG = ContactsViewModel.class.getSimpleName();

    Context context;

    private ContactRepository contactRepository;
    
    private MutableLiveData<String> modelFilter = new MutableLiveData<>();
    private ContactList contactList = new ContactList();

    private ContactRepository.Callback callback;

    public ContactsViewModel(Application application)
    {
        super(application);
        this.context = application;

        contactRepository = new ContactRepository(application, this);
    }

    public void setFilter(String filterState)
    {
        modelFilter.postValue(filterState);

        contactList =  (ContactList) Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadContactList(txFilterState));
    }
    
    public ContactList getContactList()
    {
        return contactList;
    }

    @Override
    public void onDataSetChanged() {
    }

    public Contact lookupNumber(String incomingNumber)
    {
        return contactList.lookupNumber(incomingNumber);
    }
}
