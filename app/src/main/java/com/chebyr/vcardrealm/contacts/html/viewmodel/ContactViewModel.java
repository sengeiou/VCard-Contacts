package com.chebyr.vcardrealm.contacts.html.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.datasource.ContactsObserver;
import com.chebyr.vcardrealm.contacts.html.data.Contact;
import com.chebyr.vcardrealm.contacts.html.data.TemplateData;
import com.chebyr.vcardrealm.contacts.html.repository.ContactRepository;

import java.util.HashMap;

public class ContactViewModel extends AndroidViewModel implements ContactsObserver.Callback
{
    private static String TAG = ContactViewModel.class.getSimpleName();

    private ContactRepository contactRepository;
    
    private MutableLiveData<String> modelFilter = new MutableLiveData<>();
    private LiveData<PagedList<Contact>> contactLiveData;
    private LiveData<PagedList<TemplateData>> templateLiveData;

    private ContactList contactList;


    private ContactsObserver.Callback callback;

    public ContactViewModel(Application application)
    {
        super(application);
        contactList = new ContactList(application);
        contactRepository = new ContactRepository(application, this);

        //Automatically load contact data upon change in filter
        contactLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadContactList(txFilterState));

        //Automatically load templates upon change in contact data
        templateLiveData = Transformations.switchMap(contactLiveData,
                (PagedList<Contact> contactDataPagedList) -> contactRepository.loadTemplateList(contactDataPagedList));

        templateLiveData.observeForever(this::onTemplateListChanged);
    }

    private void onTemplateListChanged(PagedList<TemplateData> templateDataPagedList)
    {
        Log.d(TAG, "onTemplateListChanged");
        contactList.mergeContactData(contactLiveData, templateLiveData);
    }

    public void setFilter(String filterState)
    {
        Log.d(TAG, "Set filter: " + filterState);

        modelFilter.postValue(filterState);

        /*
        contactLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadContactList(txFilterState));

        Log.d(TAG, "Set filter for ContactDetails loader");
        contactDetailsLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadContactDetailsList(txFilterState));

//        Log.d(TAG, "Set filter for Groups loader");
        groupLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadGroupList(txFilterState));

//        Log.d(TAG, "Set filter for Templates loader");
        templateLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadTemplateList(txFilterState));
*/
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
