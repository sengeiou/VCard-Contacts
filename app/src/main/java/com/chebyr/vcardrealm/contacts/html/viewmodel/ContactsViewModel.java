package com.chebyr.vcardrealm.contacts.html.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import android.arch.paging.PagedList;
import android.content.ContentResolver;

import com.chebyr.vcardrealm.contacts.html.Contact;
import com.chebyr.vcardrealm.contacts.html.model.ContactsRepository;

public class ContactsViewModel extends ViewModel
{
    private ContactsRepository contactsRepository;
    
    private MutableLiveData<String> modelFilter = new MutableLiveData<>();
    private LiveData<PagedList<Contact>> contactsList;

    public ContactsViewModel(ContentResolver contentResolver)
    {
        contactsRepository = new ContactsRepository(contentResolver);
    }

    public void setFilter(String filterState)
    {
        modelFilter.postValue(filterState);

        contactsList = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactsRepository.loadContactsList(txFilterState));
    }
    
    public LiveData<PagedList<Contact>> getContactsList()
    {
        return contactsList;
    }

}
