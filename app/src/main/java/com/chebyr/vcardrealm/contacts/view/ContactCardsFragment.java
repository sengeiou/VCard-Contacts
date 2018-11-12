package com.chebyr.vcardrealm.contacts.view;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.data.Contact;
import com.chebyr.vcardrealm.contacts.list.MultiSelectContactsListFragment;
import com.chebyr.vcardrealm.contacts.view.widget.CircularMenu;
import com.chebyr.vcardrealm.contacts.viewmodel.ContactViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactCardsFragment extends MultiSelectContactsListFragment implements ContactCardListViewAdapter.OnItemClickCallBack, ContactCardListView.Callback
{
    private static String TAG = ContactCardsFragment.class.getSimpleName();

    private ContactCardListView contactCardListView;
    private ContactCardListViewAdapter mContactCardListViewAdapter;
    private CircularMenu circularMenu;

    public ContactCardsFragment()
    {
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        setContext(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        FragmentActivity activity = getActivity();

        createListAdapter();

        View rootView = inflater.inflate(R.layout.contact_list_fragment, container, false);
        contactCardListView = rootView.findViewById(R.id.contact_card_list_view);
        contactCardListView.setAdapter(mContactCardListViewAdapter);
        contactCardListView.initialize(activity, this);

        circularMenu = rootView.findViewById(R.id.circular_menu);
        circularMenu.setEventListener(new MenuEventListener());

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
        mContactCardListViewAdapter.submitList(contactList);
    }

    @Override
    public void onContactCardClick(float yPosition, Contact contact)
    {
        Log.d(TAG, contact.data.displayName);
        circularMenu.setY(yPosition);
    }

    @Override
    protected ContactCardListViewAdapter createListAdapter()
    {
        mContactCardListViewAdapter = new ContactCardListViewAdapter();
        mContactCardListViewAdapter.setCallback(this);
        return mContactCardListViewAdapter;
    }

    @Override
    public void onScrollStateChanged(int newState)
    {
        mContactCardListViewAdapter.onScrollStateChanged(newState);
    }

    private class MenuEventListener implements CircularMenu.EventListener
    {
        @Override
        public void onMenuOpenAnimationStart(@NonNull CircularMenu view)
        {
            Log.d("D", "onMenuOpenAnimationStart");
        }

        @Override
        public void onMenuOpenAnimationEnd(@NonNull CircularMenu view)
        {
            Log.d("D", "onMenuOpenAnimationEnd");
        }

        @Override
        public void onMenuCloseAnimationStart(@NonNull CircularMenu view)
        {
            Log.d("D", "onMenuCloseAnimationStart");
        }

        @Override
        public void onMenuCloseAnimationEnd(@NonNull CircularMenu view)
        {
            Log.d("D", "onMenuCloseAnimationEnd");
        }

        @Override
        public void onButtonClickAnimationStart(@NonNull CircularMenu view, int index)
        {
            Log.d("D", "onButtonClickAnimationStart| index: " + index);
        }

        @Override
        public void onButtonClickAnimationEnd(@NonNull CircularMenu view, int index)
        {
            Log.d("D", "onButtonClickAnimationEnd| index: " + index);
        }

        @Override
        public boolean onButtonLongClick(@NonNull CircularMenu view, int index)
        {
            Log.d("D", "onButtonLongClick| index: " + index);
            return true;
        }

        @Override
        public void onButtonLongClickAnimationStart(@NonNull CircularMenu view, int index)
        {
            Log.d("D", "onButtonLongClickAnimationStart| index: " + index);
        }

        @Override
        public void onButtonLongClickAnimationEnd(@NonNull CircularMenu view, int index)
        {
            Log.d("D", "onButtonLongClickAnimationEnd| index: " + index);
        }
    }
}
