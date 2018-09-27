package com.chebyr.vcardrealm.contacts.html.model;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;

import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.html.Contact;
import com.chebyr.vcardrealm.contacts.html.model.queries.ContactDataQuery;
import com.chebyr.vcardrealm.contacts.html.model.queries.ContactQuery;

import java.util.Locale;

public class ContactManager implements SectionIndexer, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener,
        CursorLoader.OnLoadCompleteListener, CursorLoader.OnLoadCanceledListener, ContactLoader.Callback
{
    private static String TAG = "ContactManager";

    Context context;

    public static ContactManager mInstance;
    ContactLoader contactLoader;

    CursorLoader contactCursorLoader;
    CursorLoader contactCursorDataLoader;

    private Cursor contactCursor;
    private Cursor contactDataCursor;
    private Cursor emailCursor;
    private Cursor phoneCursor;
    private Cursor groupCursor;

    // ContactList contacts;

    private DataSetObserver mDataSetObserver;
    private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance

    boolean contactLoadingCompleted = false;

    public String mSearchTerm = null; // Stores the current search query term

    public boolean searchTermEmpty = true;

    // Stores the previously selected search item so that on a configuration change the same item can be reselected again
    public int mPreviouslySelectedSearchItem = 0;

    // Bundle key for saving previously selected search result item
    private static final String STATE_PREVIOUSLY_SELECTED_KEY = "com.example.android.contactslist.ui.SELECTED_ITEM";

    private boolean mDataValid;

    private int mRowIdColumn;

    private Callback callback;

    /**
     * @return An existing retained ImageCache object or a new one if one did not exist
     */
    public static synchronized ContactManager getInstance(Context context, Callback callback)
    {
        if (mInstance == null)
        {
            ContentResolver contentResolver = context.getContentResolver();
            mInstance = new ContactManager(contentResolver, callback);
        }

        return mInstance;
    }

    // Call from service.
    public ContactManager(Context context)
    {
        this.context = context;

    }

    // Call from activity
    private ContactManager(ContentResolver contentResolver, Callback callback)
    {
        this.context = context;
        this.callback = callback;
        contactLoader = new ContactLoader(contentResolver, this);

        // Loads a string containing the English alphabet. To fully localize the app, provide a strings.xml file in res/values-<x>
        // directories, where <x> is a locale. In the file, define a string with android:name="alphabet" and contents set to all of the
        // alphabetic characters in the language in their proper sort order, in upper case if applicable.
        final String alphabet = context.getString(R.string.alphabet);

        // Instantiates a new AlphabetIndexer bound to the column used to sort contact names.
        // The cursor is left null, because it has not yet been retrieved.
        mAlphabetIndexer = new AlphabetIndexer(null, ContactQuery.SORT_KEY, alphabet);


        // If there's a previously selected search item from a saved state then don't bother
        // initializing the loader as it will be restarted later when the query is populated into
        // the action bar search view (see onQueryTextChange() in onCreateOptionsMenu()).
        if (mPreviouslySelectedSearchItem == 0)
        {
            // Initialize the loader, and create a loader identified by ContactQuery.QUERY_ID
            startContactLoader();
        }

        startContactDataLoader();

        contactCursor = null;
        contactDataCursor = null;
        mDataValid = false;
        mRowIdColumn = -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (contactCursor != null)
        {
            contactCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    private void startContactLoader()
    {
        Uri contentUri;

        // There are two types of searches, one which displays all contacts and one which filters contacts by a search query.
        // If mSearchTerm is set then a search query has been entered and the latter should be used.

        if (mSearchTerm == null)
        {
            // Since there's no search string, use the content URI that searches the entire Contacts table
            contentUri = ContactQuery.CONTENT_URI;
        }
        else
        {
            // Since there's a search string, use the special content Uri that searches the
            // Contacts table. The URI consists of a base Uri and the search string.
            contentUri = Uri.withAppendedPath(ContactQuery.FILTER_URI, Uri.encode(mSearchTerm));
        }

        // Returns a new CursorLoader for querying the Contacts table. No arguments are used for the selection clause.
        // The search string is either encoded onto the content URI, or no contacts search string is used.
        // The other search criteria are constants. See the ContactQuery interface.
        contactCursorLoader = new CursorLoader(context, contentUri, ContactQuery.PROJECTION, ContactQuery.SELECTION, ContactQuery.SELECTION_ARGS, ContactQuery.SORT_ORDER);
        contactCursorLoader.registerListener(ContactQuery.QUERY_ID, this);
        contactCursorLoader.startLoading();
    }

    private void stopContactLoader()
    {
        contactCursorLoader.abandon();
        contactCursorLoader.unregisterListener(this);
        //contactCursorLoader.unregisterOnLoadCanceledListener(this);
        contactCursorLoader.reset();
    }

    private void startContactDataLoader()
    {
        contactCursorDataLoader = new CursorLoader(context, ContactDataQuery.URI, ContactDataQuery.PROJECTION, ContactDataQuery.SELECTION, ContactDataQuery.SELECTION_ARGS, ContactDataQuery.SORT_ORDER);
        contactCursorDataLoader.registerListener(ContactQuery.QUERY_ID, this);
        contactCursorDataLoader.startLoading();
    }

    @Override
    public void onLoadComplete(Loader loader, Object data)
    {
        int loaderId = loader.getId();

        Cursor cursor = (Cursor)data;

        switch(loaderId)
        {
            case ContactQuery.QUERY_ID:
                swapCursor(cursor);
                break;

            case ContactDataQuery.QUERY_ID:
                contactDataCursor = cursor;
                break;
        }
    }

    @Override
    public void onLoadCanceled(Loader loader)
    {
        // When the loader is being reset, clear the cursor from the adapter. This allows the cursor resources to be freed.
        int loaderId = loader.getId();
        switch(loaderId)
        {
            case ContactQuery.QUERY_ID:
                swapCursor(null);
                break;

            case ContactDataQuery.QUERY_ID:
                contactDataCursor =  null;
                break;
        }
    }

    /* Overrides swapCursor to move the new Cursor into the AlphabetIndex as well as the CursorAdapter.
     Swap in a new Cursor, returning the old Cursor.  Unlike {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em> closed. */
    public Cursor swapCursor(Cursor newContactCursor)
    {
        if(newContactCursor == contactCursor)
        {
            return null;
        }

        final Cursor oldCursor = contactCursor;
        if (oldCursor != null && mDataSetObserver != null)
        {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }

        contactCursor = newContactCursor;

        if(contactCursor != null)
        {
            if(mDataSetObserver != null)
            {
                contactCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newContactCursor.getColumnIndex(ContactQuery.CONTACT_ID);
            mDataValid = true;

            // Update the AlphabetIndexer with new cursor as well
            mAlphabetIndexer.setCursor(newContactCursor);

            int contactsCount = contactCursor.getCount();
            if(contactsCount > 0)
            {
                Log.d(TAG, "Cursor Loaded. Contact count: " + contactsCount);
                //contacts = new ContactList();
                contactLoader.execute(contactCursor);
            }
        }
        else
        {
            mRowIdColumn = -1;
            mDataValid = false;
        }
        if(callback != null)
            callback.onDataSetChanged();

        return oldCursor;
    }

    /**
     * Defines the SectionIndexer.getPositionForSection() interface.
     */
    @Override
    public int getPositionForSection(int i)
    {
        /*if (contacts == null) {
            return 0;
        }*/
        return mAlphabetIndexer.getPositionForSection(i);
    }

    /**
     * Defines the SectionIndexer.getSections() interface.
     */
    @Override
    public Object[] getSections() {
        return mAlphabetIndexer.getSections();
    }

    /**
     * Defines the SectionIndexer.getSectionForPosition() interface.
     */
    @Override
    public int getSectionForPosition(int i)
    {
        //if (contacts == null)
        {
            //return 0;
        }
        return mAlphabetIndexer.getSectionForPosition(i);
    }

    public int getItemCount()
    {
/*        if (mDataValid && contacts != null)
        {
            int size = contacts.getCount();
            Log.d(TAG, "Contact List size: " + size);
            return size;
        }*/
        return 0;
    }

    public long getItemId(int position)
    {
        if (mDataValid && (contactCursor != null) && (contactCursor.moveToPosition(position)) && (mRowIdColumn != -1))
        {
            return contactCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    public Uri getContactUri(int position)
    {
        /*if (contacts == null)
            return null;

        Contact contact = contacts.getAt(position);
        return contact.contactUri;*/
        return null;
    }

    public Contact getContact(int position)
    {
        /*Contact contact = null;
        if(contacts != null)
             contact = contacts.getAt(position);

        return contact;*/
        return null;
    }

    public void saveCurrentSelection(Bundle outState, int selectedPosition)
    {
        if (!TextUtils.isEmpty(mSearchTerm)) {
            // Saves the current search string
            outState.putString(SearchManager.QUERY, mSearchTerm);
        }
        // Saves the currently selected contact
        outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY, selectedPosition);
    }



    public void retrieveSavedInstance(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            // If we're restoring state after this fragment was recreated then
            // retrieve previous search term and previously selected search result.
            mSearchTerm = savedInstanceState.getString(SearchManager.QUERY);
            searchTermEmpty = TextUtils.isEmpty(mSearchTerm);
            mPreviouslySelectedSearchItem = savedInstanceState.getInt(STATE_PREVIOUSLY_SELECTED_KEY, 0);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String queryText)
    {
        Log.d(TAG, "onQueryTextSubmit: " + queryText);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        Log.d(TAG, "onQueryTextChange" + newText);
        // Called when the action bar search text has changed.  Updates the search filter,
        // and restarts the loader to do a new query using the new search string.
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        // Don't do anything if the filter is empty
        if (mSearchTerm == null && newFilter == null) {
            return true;
        }

        // Don't do anything if the new filter is the same as the current filter
        if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
            return true;
        }

        // Updates current filter to new filter
        mSearchTerm = newFilter;
        searchTermEmpty = TextUtils.isEmpty(mSearchTerm);

        // Restarts the contact loader the necessary content Uri from mSearchTerm.
        //contacts.setSearchTerm(mSearchTerm);
        if(callback != null)
            callback.onDataSetChanged();
        //stopContactLoader();
        //startContactLoader();
        return true;
    }

    /**
     * Identifies the start of the search string in the display name column of a Cursor row.
     * E.g. If displayName was "Adam" and search query (mSearchTerm) was "da" this would return 1.
     *
     * @param displayName The contact display name.
     * @return The starting position of the search string in the display name, 0-based. The
     * method returns -1 if the string is not found in the display name, or if the search string is empty or null.
     */
    public int indexOfSearchQuery(String displayName)
    {
        if (!searchTermEmpty)
        {
            Locale locale = Locale.getDefault();
            String searchTermLowerCase = mSearchTerm.toLowerCase(locale);
            String displayNameLowerCase = displayName.toLowerCase(locale);
            return displayNameLowerCase.indexOf(searchTermLowerCase);
        }
        return -1;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item)
    {
        // Nothing to do when the action item is expanded
        Log.d(TAG, "onMenuItemActionExpand: " + item.toString());

        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item)
    {
        Log.d(TAG, "onMenuItemActionCollapse: " + item.toString());
        // When the user collapses the SearchView the current search string is cleared and the loader restarted.
        if (!searchTermEmpty)
        {
            callback.onSelectionCleared();
        }
        mSearchTerm = null;
        searchTermEmpty = true;

        // contacts.setSearchTerm(null);

        // Restarts the contact loader the necessary content Uri from mSearchTerm.
        //stopContactLoader();
        //startContactLoader();
        return true;
    }

    public void configureSearchView(MenuItem searchItem)
    {
        // Retrieves the system search manager service
        //final SearchManager searchManager = (SearchManager) mMainActivity.getSystemService(Context.SEARCH_SERVICE);
        //Log.d(TAG, "searchManager: " + searchManager.toString());

        // Retrieves the SearchView from the search menu item
        //final SearchView searchView = (SearchView) searchItem.getActionView();

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        Log.d(TAG, "searchView: " + searchView.toString());

        //ComponentName componentName = mMainActivity.getComponentName();
        // Assign searchable info to SearchView

        //Log.d(TAG, "componentName: " + componentName.toString());

        //SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);

        //Log.d(TAG, searchableInfo.toString());

        //searchView.setSearchableInfo(searchableInfo);

        // Set listeners for SearchView
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchItem, this);

        if (mSearchTerm != null)
        {
            // If search term is already set here then this fragment is being restored from a saved state and the search menu item
            // needs to be expanded and populated again.

            // Stores the search term (as it will be wiped out by onQueryTextChange() when the menu item is expanded).
            final String savedSearchTerm = mSearchTerm;

            // Expands the search menu item
            searchItem.expandActionView();

            // Sets the SearchView to the previous search string
            searchView.setQuery(savedSearchTerm, false);
        }
    }

    @Override
    public void onContactLoaded(Contact contact)
    {
        Log.d(TAG, "Contact Loaded: " + contact.displayName + " incoming Number: "+ contact.incomingNumber);

        //contacts.add(contact.incomingNumber, contact);

        if(callback != null)
            callback.onDataSetChanged();
    }

    @Override
    public void onContactLoadingCompleted()
    {
        Log.d(TAG, "Contacts loading completed");
        contactLoadingCompleted = true;
    }

    public String[] getContactNumberFromUri(Uri contactUri)
    {
        return contactLoader.getContactNumberFromUri(contactUri);
    }

    public Contact lookupNumber(String incomingNumber)
    {
        //return contacts.lookup(incomingNumber);
        return null;
    }

    private class NotifyingDataSetObserver extends DataSetObserver
    {
        @Override
        public void onChanged()
        {
            super.onChanged();
            mDataValid = true;
            callback.onDataSetChanged();
        }

        @Override
        public void onInvalidated()
        {
            super.onInvalidated();
            mDataValid = false;
            callback.onDataSetChanged();
        }
    }


    public interface Callback
    {
        void onDataSetChanged();
        void onSelectionCleared();
    }

}
