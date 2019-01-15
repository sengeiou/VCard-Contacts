package com.chebyr.vcardrealm.contacts.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistenceManager
{
    private static final String TAG = "PersistenceManager";

    private static final String PREF_FILE_NAME = "VCardCallerID";
    private static final String PREF_VCARD_XML = "vCardXML";
    private static final String PREF_CALLER_ID_TEST_NUMBER = "mCallerIDPreviewNumber";
    private static final String PREF_CALLER_ID_TEST_NAME = "mCallerIDPreviewName";
    private static final String PREF_ACTIVE_FILE_NAME = "mActiveFileName";
    private static final String PREF_DEFAULT_DIRECTORY = "mDefaultDirectory";
    private static final String PREF_APP_INITIALIZED = "mAppInitialized";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String PREF_TOOLBAR_INITIAL_POSITION = "mToolbarInitialPosition";
    private static final String PREF_TOOLBAR_HEIGHT = "mToolbarHeight";
    private static final String PREF_SCREEN_HEIGHT = "mScreenHeight";
    private static final String PREF_SELECTED_POSITION = "selected_navigation_drawer_position";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    public PersistenceManager(Context context)
    {
        mSharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public void commit()
    {
        editor.commit();
    }

    public int getAppInitializedVersion()
    {
        return mSharedPreferences.getInt(PREF_APP_INITIALIZED, 0);
    }

    public void putAppInitializedVersion(int initializedVersion)
    {
        editor.putInt(PREF_APP_INITIALIZED, initializedVersion);
    }

    public boolean getUserLearnedDrawer()
    {
        return mSharedPreferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }

    public int getToolbarInitialPosition()
    {
        return mSharedPreferences.getInt(PREF_TOOLBAR_INITIAL_POSITION, 0);
    }

    public void putToolbarInitialPosition(int toolbarInitialPosition)
    {
        editor.putInt(PREF_TOOLBAR_INITIAL_POSITION, toolbarInitialPosition);
    }

    public int getScreenHeight()
    {
        return mSharedPreferences.getInt(PREF_SCREEN_HEIGHT, 0);
    }

    public void putScreenHeight(int screenHeight)
    {
        editor.putInt(PREF_SCREEN_HEIGHT, screenHeight);
    }

    public int getToolbarHeight()
    {
        return mSharedPreferences.getInt(PREF_TOOLBAR_HEIGHT, 0);
    }

    public void putToolbarHeight(int toolbarHeight)
    {
        editor.putInt(PREF_TOOLBAR_HEIGHT, toolbarHeight);
    }

    public void putCurrentSelectedPosition(int currentSelectedPosition)
    {
        editor.putInt(PREF_SELECTED_POSITION, currentSelectedPosition);
    }

    public int getCurrentSelectedPosition()
    {
        return mSharedPreferences.getInt(PREF_SELECTED_POSITION, 0);
    }

    public String getVCardXML()
    {
        return mSharedPreferences.getString(PREF_VCARD_XML, null);
    }

    public void putVCardXML(String vCardXML)
    {
        editor.putString(PREF_VCARD_XML, vCardXML);
    }

    public String getCallerIDPreviewNumber()
    {
        return mSharedPreferences.getString(PREF_CALLER_ID_TEST_NUMBER, null);
    }

    public void putCallerIDPreviewNumber(String callerIDPreviewNumber)
    {
        editor.putString(PREF_CALLER_ID_TEST_NUMBER, callerIDPreviewNumber);
    }

    public String getCallerIDPreviewName()
    {
        return mSharedPreferences.getString(PREF_CALLER_ID_TEST_NAME, null);
    }

    public void putCallerIDPreviewName(String callerIDPreviewName)
    {
        editor.putString(PREF_CALLER_ID_TEST_NAME, callerIDPreviewName);
    }

    public String getActiveFileName()
    {
        return mSharedPreferences.getString(PREF_ACTIVE_FILE_NAME, null);
    }

    public void putActiveFileName(String activeFileName)
    {
        editor.putString(PREF_ACTIVE_FILE_NAME, activeFileName);
    }

    public String getDefaultDirectory()
    {
        return mSharedPreferences.getString(PREF_DEFAULT_DIRECTORY, null);
    }

    public void putDefaultDirectory(String defaultDirectory)
    {
        editor.putString(PREF_DEFAULT_DIRECTORY, defaultDirectory);
    }
}
