package com.chebyr.vcardrealm.contacts.view;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.data.Contact;
import com.chebyr.vcardrealm.contacts.list.MultiSelectContactsListFragment;
import com.chebyr.vcardrealm.contacts.util.IndentUtil;
import com.chebyr.vcardrealm.contacts.view.widget.CircularMenu;
import com.chebyr.vcardrealm.contacts.viewmodel.ContactViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactCardsFragment extends MultiSelectContactsListFragment
        implements ContactCardListViewAdapter.OnItemClickCallBack,
        ContactCardListView.Callback,
        CircularMenu.Callback
{
    private static String TAG = ContactCardsFragment.class.getSimpleName();

    private ContactCardListView contactCardListView;
    private ContactCardListViewAdapter mContactCardListViewAdapter;
    private CircularMenu circularMenu;
    private boolean initScroll;
    private int clickedItemPosition;
    private Menu menu;
    private Contact selectedContact;

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
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        FragmentActivity activity = getActivity();

        createListAdapter();

        View rootView = layoutInflater.inflate(R.layout.contact_list_fragment, container, false);

        contactCardListView = rootView.findViewById(R.id.contact_card_list_view);
        contactCardListView.setAdapter(mContactCardListViewAdapter);
        contactCardListView.initialize(activity, this);

        circularMenu = rootView.findViewById(R.id.circular_menu);

        ContactViewModel contactViewModel = ViewModelProviders.of(activity).get(ContactViewModel.class);
        Log.d(TAG, "ViewModel created: " + contactViewModel.toString());

        contactViewModel.setFilter("");

        LiveData<PagedList<Contact>> contactsList = contactViewModel.getContactList();
        contactsList.observe(this, this::onContactsListChanged);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
    {
        super.onCreateOptionsMenu(menu, menuInflater);

        menuInflater.inflate(R.menu.circular_menu, menu);
        this.menu = menu;
        circularMenu.initialize(menu, this);
    }

    private void onContactsListChanged(PagedList<Contact> contactList)
    {
        Log.d(TAG, "onContactsListChanged. No of contacts: " + contactList.size());
        mContactCardListViewAdapter.submitList(contactList);
    }

    @Override
    public void onContactCardClick(int position, Contact contact)
    {
        clickedItemPosition = position;
        selectedContact = contact;

        if(contactCardListView.checkVisible(position))
        {
            Log.d(TAG, "View fully visible. showCircularMenu");
            showCircularMenu();
        }
        else
        {
            Log.d(TAG, "View not fully visible. scrollToPosition");
            initScroll = true;
            circularMenu.closeMenu();
            contactCardListView.scrollToPosition(position);
        }
    }

    private void showCircularMenu()
    {
        View itemView = contactCardListView.getChildView(clickedItemPosition);
        if(itemView == null)
        {
            Log.d(TAG, "contactCardListView.getChildAt(clickedItemPosition " + clickedItemPosition + ") is null");
            return;
        }
        float yPos =  itemView.getY();
        float offset = itemView.getHeight() / 2;
        float midCoordinate = yPos + offset;
        Log.d(TAG, "showCircularMenu with yPos: " + yPos + " offset: " + offset + " midCoordinate: " + midCoordinate);
        circularMenu.setMidCoordinate(midCoordinate);
        circularMenu.openMenu();
    }

    @Override
    protected ContactCardListViewAdapter createListAdapter()
    {
        mContactCardListViewAdapter = new ContactCardListViewAdapter();
        mContactCardListViewAdapter.setCallback(this);
        return mContactCardListViewAdapter;
    }

    @Override
    public void onScrolled()
    {
        Log.d(TAG, "Scroll completed");
        if (initScroll)
        {
            Log.d(TAG, "Show menu at new position");
            initScroll = false;
            showCircularMenu();
        }
        else
            circularMenu.closeMenu();
    }

    // Simulate menu button click
    @Override
    public void onMenuButtonClick(int index)
    {
        Log.d("D", "onMenuButtonClick| index: " + index);
        onOptionsItemSelected(menu.findItem(index));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        try {
            IndentUtil indentUtil = new IndentUtil(getContext());
            String uriText;

            switch (item.getItemId()) {
                case R.id.call_phone:
                    uriText = "tel:" + selectedContact.details.phoneNumbers;
                    indentUtil.launchApplication(IndentUtil.ANDROID_DIALER, uriText);
                    return true;

                case R.id.send_sms:
                    uriText = "smsto:" + selectedContact.details.phoneNumbers;
                    indentUtil.launchApplication(IndentUtil.ANDROID_SMS, uriText);
                    return true;

                case R.id.open_whatsapp:
//                indentUtil.launchApplication(IndentUtil.WHATSAPP);
                    return true;

                case R.id.open_map:
//                indentUtil.launchApplication(IndentUtil.GOOGLE_MAPS);
                    return true;

                case R.id.open_facebook:
//                indentUtil.launchApplication(IndentUtil.FACEBOOK_APP);
                    return true;

                case R.id.send_email:
                    uriText = "mailto:" + selectedContact.details.eMails;
                    indentUtil.launchApplication(IndentUtil.GMAIL, uriText);
                    return true;

                case R.id.share_contact:
//                indentUtil.launchApplication(IndentUtil.);
                    return true;
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }
}
