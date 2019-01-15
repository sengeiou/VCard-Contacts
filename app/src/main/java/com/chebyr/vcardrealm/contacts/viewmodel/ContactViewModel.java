package com.chebyr.vcardrealm.contacts.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.datasource.ContactsObserver;
import com.chebyr.vcardrealm.contacts.data.Contact;
import com.chebyr.vcardrealm.contacts.repository.ContactRepository;

public class ContactViewModel extends AndroidViewModel implements ContactsObserver.Callback
{
    private static String TAG = ContactViewModel.class.getSimpleName();

    private ContactRepository contactRepository;
    private MutableLiveData<String> modelFilter = new MutableLiveData<>();
    private LiveData<PagedList<Contact>> contactList;

    private ContactsObserver.Callback callback;

    public ContactViewModel(Application application)
    {
        super(application);
        contactList = new ContactList();
        contactRepository = new ContactRepository(application, this);

        //Automatically load contact data upon change in filter
        contactList = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadContactList(txFilterState));
    }

    @Override
    public void onDataSetChanged()
    {

    }

    public void setFilter(String filterState)
    {
        Log.d(TAG, "Set filter: " + filterState);
        modelFilter.postValue(filterState);
    }
    
    public LiveData<PagedList<Contact>> getContactList()
    {
        return contactList;
    }
}
