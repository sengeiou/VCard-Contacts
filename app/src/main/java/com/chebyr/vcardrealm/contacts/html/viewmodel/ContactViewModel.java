package com.chebyr.vcardrealm.contacts.html.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.datasource.ContactsObserver;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.TemplateData;
import com.chebyr.vcardrealm.contacts.html.repository.ContactRepository;

public class ContactViewModel extends AndroidViewModel implements ContactsObserver.Callback
{
    private static String TAG = ContactViewModel.class.getSimpleName();

    private ContactRepository contactRepository;
    
    private MutableLiveData<String> modelFilter = new MutableLiveData<>();
    private LiveData<PagedList<ContactData>> contactLiveData = new MutableLiveData<>();
    private LiveData<PagedList<ContactDetailsData>> contactDetailsLiveData = new MutableLiveData<>();
    private LiveData<PagedList<GroupData>> groupLiveData = new MutableLiveData<>();
    private LiveData<PagedList<TemplateData>> templateLiveData = new MutableLiveData<>();

    private ContactList contactList;


    private ContactsObserver.Callback callback;

    public ContactViewModel(Application application)
    {
        super(application);
        contactList = new ContactList(application);
        contactRepository = new ContactRepository(application, this);
    }

    public void setFilter(String filterState)
    {
        modelFilter.postValue(filterState);

        Log.d(TAG, "Set filter for ContactList loader");
        contactLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadContactList(txFilterState));

        Log.d(TAG, "Set filter for ContactDetails loader");
        contactDetailsLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadContactDetailsList(txFilterState));

        Log.d(TAG, "Set filter for Groups loader");
        groupLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadGroupList(txFilterState));

        Log.d(TAG, "Set filter for Templates loader");
        templateLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadTemplateList(txFilterState));

        contactList.mergeContactData(contactLiveData, contactDetailsLiveData, groupLiveData, templateLiveData);
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
