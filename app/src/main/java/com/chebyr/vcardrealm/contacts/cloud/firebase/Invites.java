package com.chebyr.vcardrealm.contacts.cloud.firebase;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.appinvite.AppInviteInvitation;

import static android.app.Activity.RESULT_OK;

public class Invites
{
    private static String TAG = Invites.class.getSimpleName();
    private static int REQUEST_INVITE = 12345;

    public Invites()
    {
    }

    private Intent getInviteIntent(String title, String message, Uri deepLink, Uri customImage, String callToActionText)
    {
        Intent intent = new AppInviteInvitation.IntentBuilder(title)
                .setMessage(message)
                .setDeepLink(deepLink)
                .setCustomImage(customImage)
                .setCallToActionText(callToActionText)
                .build();

        return intent;
        // call startActivityForResult(intent, Invites.REQUEST_INVITE);
    }

    //@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == Invites.REQUEST_INVITE)
        {
            if (resultCode == RESULT_OK)
            {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            }
            else
            {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }
}
