package com.chebyr.vcardrealm.contacts.html.view;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.html.data.Contact;
import com.chebyr.vcardrealm.contacts.html.viewmodel.ContactViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactCardsFragment extends Fragment implements ContactCardListView.OnItemClickCallBack
{
    private static String TAG = ContactCardsFragment.class.getSimpleName();

    private ContactCardListView contactCardListView;
    private ContactCardListView.OnItemClickCallBack callBack;

    public ContactCardsFragment()
    {
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentActivity activity = getActivity();

        View rootView = inflater.inflate(R.layout.contact_list_fragment, container, false);
        contactCardListView = rootView.findViewById(R.id.contact_card_list_view);
        contactCardListView.initialize(activity);

        ContactViewModel contactViewModel = ViewModelProviders.of(activity).get(ContactViewModel.class);
        Log.d(TAG, "ViewModel created: " + contactViewModel.toString());

        contactViewModel.setFilter("");

        LiveData<PagedList<Contact>> contactsList = contactViewModel.getContactList();
        contactsList.observe(this, this::onContactsListChanged);

        return rootView;
    }

    private void onContactsListChanged(PagedList<Contact> contactList)
    {
        Log.d(TAG, "onContactsListChanged. No of contacts: " + contactList.size());
        contactCardListView.setContactList(contactList);
    }

    @Override
    public void onSelectionCleared(int type, String extra)
    {
        callBack.onSelectionCleared(type,extra);
    }
}
