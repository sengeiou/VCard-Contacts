package com.chebyr.vcardrealm.contacts.view;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.data.Contact;

public class ContactCardListViewAdapter extends PagedListAdapter<Contact, ContactCardListViewAdapter.ContactCardsViewHolder>
{
    private static String TAG = ContactCardListViewAdapter.class.getSimpleName();

    private OnItemClickCallBack callBack;

    public ContactCardListViewAdapter()
    {
        super(DIFF_CALLBACK);
    }

    private static DiffUtil.ItemCallback<Contact> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Contact>() {
                // Contact details may have changed if reloaded from the database,
                // but ID is fixed.
                @Override
                public boolean areItemsTheSame(Contact oldContact, Contact newContact) {
                    return oldContact.getId() == newContact.getId();
                }

                @Override
                public boolean areContentsTheSame(Contact oldContact,
                                                  Contact newContact) {
                    return oldContact.equals(newContact);
                }
            };
    
    public void setCallback(OnItemClickCallBack callBack)
    {
        this.callBack        = callBack;
    }

    @Override
    public ContactCardsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
//        Log.d(TAG, "onCreateViewHolder");
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.contact_card, viewGroup, false);
        ContactCardsViewHolder contactCardsViewHolder = new ContactCardsViewHolder(itemView);
        return contactCardsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactCardsViewHolder viewHolder, int position)
    {

        Contact contact = getItem(position);
        if(contact != null)
            viewHolder.setContact(contact);
    }

    public void onScrollStateChanged(int newState)
    {
//        if (newState != RecyclerView.SCROLL_STATE_IDLE)
//            contactImageLoader.setPauseWork(true);
//        else
//            contactImageLoader.setPauseWork(false);
    }


    public interface OnItemClickCallBack
    {
        public void onSelectionCleared(int type, String extra);
    }

    public static class ContactCardsViewHolder extends RecyclerView.ViewHolder
    {
        ContactCardView contactCardView;

        public ContactCardsViewHolder(View itemView)
        {
            super(itemView);
            contactCardView = itemView.findViewById(R.id.contact_card_view);
            contactCardView.initialize();
        }

        public void setContact(Contact contact)
        {
            contactCardView.setContact(contact);
        }

    }
}
