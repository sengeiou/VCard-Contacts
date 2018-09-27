package com.chebyr.vcardcontacts.Services.ContactManager.Queries;

import android.net.Uri;
import android.provider.ContactsContract;

public interface ContactDataQuery
{
    int QUERY_ID = 2;

    // Uri to fetch additional contact data
    Uri URI = ContactsContract.Data.CONTENT_URI;

    String CONTACT_ID = ContactsContract.Data.CONTACT_ID;

    String MIMETYPE = ContactsContract.Data.MIMETYPE;

    String NICK_NAME = ContactsContract.CommonDataKinds.Nickname.NAME;
    String COMPANY = ContactsContract.CommonDataKinds.Organization.COMPANY;
    String JOB_TITLE = ContactsContract.CommonDataKinds.Organization.TITLE;
    String GROUP_ROW_ID = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID;
    String INSTANT_MESSENGER = ContactsContract.CommonDataKinds.Im.DATA;
    String POSTAL_ADDRESS = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;
    String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    String EMAIL = ContactsContract.CommonDataKinds.Email.ADDRESS;
    String WEBSITE = ContactsContract.CommonDataKinds.Website.URL;
    String NOTE = ContactsContract.CommonDataKinds.Note.NOTE;

    String[] PROJECTION = {CONTACT_ID, MIMETYPE, NICK_NAME, COMPANY, JOB_TITLE, PHONE_NUMBER, EMAIL, INSTANT_MESSENGER, POSTAL_ADDRESS, WEBSITE, NOTE};

    String SELECTION = ContactsContract.Data.CONTACT_ID + " = ?";

    String[] SELECTION_ARGS = null;

    // The desired sort order for the returned Cursor. The primary sort key allows for localization.
    String SORT_ORDER = CONTACT_ID;

    String NICK_NAME_MIME = ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE;
    String ORGANIZATION_MIME = ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE;
    String GROUP_MIME = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;
    String IM_MIME = ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE;
    String ADDRESS_MIME = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;
    String PHONE_MIME = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    String EMAIL_MIME = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
    String WEBSITE_MIME = ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE;
    String NOTE_MIME = ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE;

    // The query column numbers which map to each value in the projection
    //String[] JOIN = {CONTACT_ID};

}
