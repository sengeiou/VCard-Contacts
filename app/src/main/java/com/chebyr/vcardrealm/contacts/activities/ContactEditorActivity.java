/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.chebyr.vcardrealm.contacts.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.android.contacts.common.activity.RequestPermissionsActivity;
import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.editor.ContactEditorFragment;
import com.chebyr.vcardrealm.contacts.util.DialogManager;

/**
 * Contact editor with all fields displayed.
 */
public class ContactEditorActivity extends ContactEditorBaseActivity
        implements DialogManager.DialogShowingViewActivity {

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (RequestPermissionsActivity.startPermissionActivity(this)) {
            return;
        }

        setContentView(R.layout.contact_editor_activity);

        mFragment = (ContactEditorFragment) getFragmentManager().findFragmentById(
                R.id.contact_editor_fragment);
        mFragment.setListener(mFragmentListener);

        final String action = getIntent().getAction();
        final Uri uri = ACTION_EDIT.equals(action)
                || Intent.ACTION_EDIT.equals(action) ? getIntent().getData() : null;
        mFragment.load(action, uri, getIntent().getExtras());
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null) {
            mFragment.save(ContactEditor.SaveMode.COMPACT, /* backPressed =*/ true);
        }
    }
}
