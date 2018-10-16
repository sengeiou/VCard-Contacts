package com.chebyr.vcardrealm.contacts.html.view;

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
import com.chebyr.vcardrealm.contacts.html.data.Contact;

public class ContactCardsListViewAdapter extends PagedListAdapter<Contact, ContactCardsListViewAdapter.ContactCardsViewHolder>
{
    private static String TAG = ContactCardsListViewAdapter.class.getSimpleName();

    private PagedList<Contact> contactList;

    private OnItemClickCallBack callBack;

    public ContactCardsListViewAdapter()
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

    public void setContactList(PagedList<Contact> contactPagedList)
    {
        this.contactList = contactPagedList;
        notifyDataSetChanged();
    }

    @Override
    public ContactCardsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        Log.d(TAG, "onCreateViewHolder");
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.contact_card, viewGroup, false);
        ContactCardsViewHolder contactCardsViewHolder = new ContactCardsViewHolder(itemView);
        return contactCardsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactCardsViewHolder viewHolder, int position)
    {
        Contact contact = contactList.get(position);
        viewHolder.setContact(contact);
    }

    @Override
    public int getItemCount()
    {
        if (contactList != null)
            return contactList.size();

        return 0;
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
            contactCardView = itemView.findViewById(R.id.contact_card);
            contactCardView.initialize();
        }

        public void setContact(Contact contact)
        {
            contactCardView.setContact(contact);
        }

    }
}
