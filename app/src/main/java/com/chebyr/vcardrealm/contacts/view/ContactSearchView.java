package com.chebyr.vcardrealm.contacts.view;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;

import java.util.Locale;

public class ContactSearchView implements SearchView.OnQueryTextListener
{
    private static String TAG = ContactSearchView.class.getSimpleName();

    public String mSearchTerm = null; // Stores the current search query term
    public boolean searchTermEmpty = true;

    // Bundle key for saving previously selected search result item
    private static final String STATE_PREVIOUSLY_SELECTED_KEY = "com.example.android.contactslist.ui.SELECTED_ITEM";

    // Stores the previously selected search item so that on a configuration change the same item can be reselected again
    public int mPreviouslySelectedSearchItem = 0;

    private Callback callback;
    private Activity activity;

    public ContactSearchView(Activity activity, Callback callback)
    {
        this.activity = activity;
        this.callback = callback;
    }

    public void configureSearchView(MenuItem searchItem)
    {
        Log.d(TAG, "searchItem: " + searchItem);

        searchItem.expandActionView();
        // Retrieves the system search manager service
        final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        Log.d(TAG, "searchManager: " + searchManager.toString());

        // Retrieves the SearchView from the search menu item
        final SearchView searchView = (SearchView) searchItem.getActionView();
        //searchView.setMaxWidth(Integer.MAX_VALUE);

        ComponentName componentName = activity.getComponentName();
        // Assign searchable info to SearchView

        //Log.d(TAG, "componentName: " + componentName.toString());

        SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);

        //Log.d(TAG, searchableInfo.toString());
        searchView.setSearchableInfo(searchableInfo);

        searchView.setIconifiedByDefault(false);


        // Set listeners for SearchView
        searchView.setOnQueryTextListener(this);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
        {
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

        });

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


    @Override
    public boolean onQueryTextSubmit(String queryText)
    {
        Log.d(TAG, "onQueryTextSubmit: " + queryText);
        return true;
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

    public interface Callback
    {
        void onDataSetChanged();
        void onSelectionCleared();
    }
}
