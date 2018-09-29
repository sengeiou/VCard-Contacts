package com.chebyr.vcardrealm.contacts.html.model;

import android.content.Context;
import android.database.Cursor;
import android.widget.SectionIndexer;
import android.widget.AlphabetIndexer;

import com.chebyr.vcardrealm.contacts.R;

public class ContactsSectionIndexer implements SectionIndexer
{
    private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance

    public ContactsSectionIndexer(Context context, int sortedColumnIndex)
    {
        // Loads a string containing the English alphabet. To fully localize the app, provide a strings.xml file in res/values-<x>
        // directories, where <x> is a locale. In the file, define a string with android:name="alphabet" and contents set to all of the
        // alphabetic characters in the language in their proper sort order, in upper case if applicable.
        final String alphabet = context.getString(R.string.alphabet);

        // Instantiates a new AlphabetIndexer bound to the column used to sort contact names.
        // The cursor is left null, because it has not yet been retrieved.
        mAlphabetIndexer = new AlphabetIndexer(null, sortedColumnIndex, alphabet);

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
        return mAlphabetIndexer.getSectionForPosition(i);
    }

    /**
     * Defines the SectionIndexer.getPositionForSection() interface.
     */
    @Override
    public int getPositionForSection(int i)
    {
        return mAlphabetIndexer.getPositionForSection(i);
    }

    public void setCursor(Cursor newContactCursor)
    {
        // Update the AlphabetIndexer with new cursor as well
        mAlphabetIndexer.setCursor(newContactCursor);
    }
}
