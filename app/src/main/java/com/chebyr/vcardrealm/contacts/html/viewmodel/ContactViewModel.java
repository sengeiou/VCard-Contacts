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

        //Automatically load contact data upon change in filter
        contactLiveData = Transformations.switchMap(modelFilter,
                (String txFilterState) -> contactRepository.loadContactList(txFilterState));

        //Automatically load contact details upon change in contact data
        contactDetailsLiveData = Transformations.switchMap(contactLiveData,
                (PagedList<ContactData> contactDataPagedList) -> contactRepository.loadContactDetailsList(contactDataPagedList));

        //Automatically load group details upon change in contact details
        groupLiveData = Transformations.switchMap(contactDetailsLiveData,
                (PagedList<ContactDetailsData> contactDetailsDataPagedList) -> contactRepository.loadGroupList(contactDetailsDataPagedList));

        //Automatically load contact details upon change in contact data
        templateLiveData = Transformations.switchMap(contactLiveData,
                (PagedList<ContactData> contactDataPagedList) -> contactRepository.loadTemplateList(contactDataPagedList));

        templateLiveData.observeForever(this::onTemplateListChanged);
    }

    private void onTemplateListChanged(PagedList<TemplateData> templateDataPagedList)
    {
        Log.d(TAG, "onTemplateListChanged");
        contactList.mergeContactData(contactLiveData, contactDetailsLiveData, groupLiveData, templateLiveData);
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
