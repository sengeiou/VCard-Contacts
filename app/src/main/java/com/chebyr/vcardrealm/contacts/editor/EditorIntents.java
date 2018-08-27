package com.chebyr.vcardrealm.contacts.editor;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.android.contacts.common.model.RawContactDeltaList;
import com.android.contacts.common.util.MaterialColorMapUtils;

import java.util.ArrayList;

public class EditorIntents
{
    /**
     * Returns an Intent to start the {@link //CompactContactEditorActivity} for a new contact.
     */
    public static Intent createCompactInsertContactIntent() {
        return createCompactInsertContactIntent(/* rawContactDeltaList =*/ null,
                /* displayName =*/ null, /* phoneticName =*/ null, /* updatedPhotos =*/ null);
    }

    /**
     * Returns an Intent to start the {@link //CompactContactEditorActivity} for a new contact with
     * the field values specified by rawContactDeltaList pre-populate in the form.
     */
    public static Intent createCompactInsertContactIntent(RawContactDeltaList rawContactDeltaList,
                                                          String displayName, String phoneticName, Bundle updatedPhotos) {
        final Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        if (rawContactDeltaList != null || displayName != null || phoneticName != null) {
            putRawContactDeltaValues(intent, rawContactDeltaList, displayName, phoneticName);
        }
        putUpdatedPhotos(intent, updatedPhotos);
        return intent;
    }

    private static void putRawContactDeltaValues(Intent intent,
                                                 RawContactDeltaList rawContactDeltaList, String displayName, String phoneticName) {
        // Pass on all the data that has been entered so far
        if (rawContactDeltaList != null && !rawContactDeltaList.isEmpty()) {
            ArrayList<ContentValues> contentValues = rawContactDeltaList.get(0).getContentValues();
            if (contentValues != null && contentValues.size() != 0) {
                intent.putParcelableArrayListExtra(
                        ContactsContract.Intents.Insert.DATA, contentValues);
            }
        }
        // Names must be passed separately since they are skipped in RawContactModifier.parseValues
        if (!TextUtils.isEmpty(displayName)) {
            intent.putExtra(ContactsContract.Intents.Insert.NAME, displayName);
        }
        if (!TextUtils.isEmpty(phoneticName)) {
            intent.putExtra(ContactsContract.Intents.Insert.PHONETIC_NAME, phoneticName);
        }
    }

    private static void putUpdatedPhotos(Intent intent, Bundle updatedPhotos) {
        if (updatedPhotos != null && !updatedPhotos.isEmpty()) {
            intent.putExtra(ContactEditorBaseFragment.INTENT_EXTRA_UPDATED_PHOTOS, updatedPhotos);
        }
    }

    /**
     * Returns an Intent to start the {@link //CompactContactEditorActivity} for an
     * existing contact.
     */
    public static Intent createCompactEditContactIntent(Uri contactLookupUri,
                                                        MaterialColorMapUtils.MaterialPalette materialPalette, Bundle updatedPhotos, long photoId, long nameId) {
        final Intent intent = new Intent(Intent.ACTION_EDIT, contactLookupUri);
        putMaterialPalette(intent, materialPalette);
        putUpdatedPhotos(intent, updatedPhotos);
        putPhotoId(intent, photoId);
        putNameId(intent, nameId);
        return intent;
    }

    private static void putMaterialPalette(Intent intent, MaterialColorMapUtils.MaterialPalette materialPalette) {
        if (materialPalette != null) {
            intent.putExtra(ContactEditorBaseFragment.INTENT_EXTRA_MATERIAL_PALETTE_PRIMARY_COLOR,
                    materialPalette.mPrimaryColor);
            intent.putExtra(ContactEditorBaseFragment.INTENT_EXTRA_MATERIAL_PALETTE_SECONDARY_COLOR,
                    materialPalette.mSecondaryColor);
        }
    }

    private static void putPhotoId(Intent intent, long photoId) {
        if (photoId >= 0) {
            intent.putExtra(ContactEditorBaseFragment.INTENT_EXTRA_PHOTO_ID, photoId);
        }
    }

    private static void putNameId(Intent intent, long nameId) {
        if (nameId >= 0) {
            intent.putExtra(ContactEditorBaseFragment.INTENT_EXTRA_NAME_ID, nameId);
        }
    }

}
