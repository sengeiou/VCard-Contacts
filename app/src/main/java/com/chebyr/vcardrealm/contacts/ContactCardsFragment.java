package com.chebyr.vcardrealm.contacts;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactCardsFragment extends Fragment implements ContactCardsView.OnItemClickCallBack
{
    private ContactCardsView mContactCardsView;
    private ContactCardsView.OnItemClickCallBack callBack;

    public ContactCardsFragment()
    {
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Activity activity = getActivity();

        View rootView = inflater.inflate(R.layout.contact_list_fragment, container, false);
        mContactCardsView = rootView.findViewById(R.id.contact_cards_view);
        mContactCardsView.initialize(activity);

        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");
        mContactCardsView.addData("file:///android_asset/business_card.html");

        return rootView;
    }

    @Override
    public void onSelectionCleared(int type, String extra)
    {
        callBack.onSelectionCleared(type,extra);
    }
}
