package com.chebyr.vcardrealm.contacts.view;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.chebyr.vcardrealm.contacts.view.widget.CircularMenu;
import com.chebyr.vcardrealm.contacts.viewmodel.ContactViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactCardsFragment extends MultiSelectContactsListFragment
        implements ContactCardListViewAdapter.OnItemClickCallBack,
        ContactCardListView.Callback,
        View.OnScrollChangeListener,
        CircularMenu.Callback
{
    private static String TAG = ContactCardsFragment.class.getSimpleName();

    private ContactCardListView contactCardListView;
    private ContactCardListViewAdapter mContactCardListViewAdapter;
    private CircularMenu circularMenu;
    private boolean initScroll;
    private int clickedItemPosition;
    private Menu menu;

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
        contactCardListView.setOnScrollChangeListener(this);

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

        if(contactCardListView.checkVisible(position))
            showCircularMenu();
        else
        {
            initScroll = true;
            contactCardListView.scrollToPosition(position);
        }
    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3)
    {
        if(initScroll)
        {
            initScroll = false;
            showCircularMenu();
        }
        else
            circularMenu.closeMenu();
    }

    private void showCircularMenu()
    {
        View itemView = contactCardListView.getChildAt(clickedItemPosition);
        float midCoordinate = itemView.getY() + itemView.getHeight() / 2;
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
    public void onScrollStateChanged(int newState)
    {
        mContactCardListViewAdapter.onScrollStateChanged(newState);
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
        switch (item.getItemId())
        {
            case R.id.call_phone:
                return true;

            case R.id.send_sms:
                return true;

            case R.id.open_whatsapp:
                return true;

            case R.id.open_map:
                return true;

            case R.id.open_facebook:
                return true;

            case R.id.send_email:
                return true;

            case R.id.share_contact:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
