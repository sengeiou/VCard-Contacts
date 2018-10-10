package com.chebyr.vcardrealm.contacts.html.view;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.html.viewmodel.ContactList;
import com.chebyr.vcardrealm.contacts.html.viewmodel.ContactViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactCardsFragment extends Fragment implements ContactCardsListView.OnItemClickCallBack
{
    private static String TAG = ContactCardsFragment.class.getSimpleName();

    private ContactCardsListView mContactCardsListView;
    private ContactCardsListView.OnItemClickCallBack callBack;

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
        Activity activity = getActivity();

        View rootView = inflater.inflate(R.layout.contact_list_fragment, container, false);
        mContactCardsListView = rootView.findViewById(R.id.contact_cards_view);
        mContactCardsListView.initialize(activity);

        ContactViewModel model = ViewModelProviders.of(this).get(ContactViewModel.class);
        Log.d(TAG, "ViewModel created: " +model.toString());

        model.setFilter("");
        ContactList contactsList = model.getContactList();

        contactsList.observe(this, contactPagedList ->
        {
            Log.d(TAG, "update UI. No of contacts: " + contactPagedList.size());
            mContactCardsListView.setContactPagedList(contactPagedList);
        });

        return rootView;
    }

    @Override
    public void onSelectionCleared(int type, String extra)
    {
        callBack.onSelectionCleared(type,extra);
    }
}
