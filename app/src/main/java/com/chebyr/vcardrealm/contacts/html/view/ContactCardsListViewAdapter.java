package com.chebyr.vcardrealm.contacts.html.view;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.html.Contact;

import java.util.List;

public class ContactCardsListViewAdapter extends PagedListAdapter<Contact, RecyclerView.ViewHolder> {

    private static String TAG = ContactCardsListViewAdapter.class.getSimpleName();

    private List<Contact> contactPagedList;

    private OnItemClickCallBack callBack;

    protected ContactCardsListViewAdapter()
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

    public void setContactPagedList(List<Contact> contactPagedList)
    {
        this.contactPagedList = contactPagedList;
    }

    public void addData(String path)
    {
        Contact contact = new Contact(path);
        contactPagedList.add(contact);

        notifyItemInserted(contactPagedList.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.contact_card, viewGroup, false);
        ContactCardsViewHolder contactCardsViewHolder = new ContactCardsViewHolder(itemView);
        return contactCardsViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ContactCardsViewHolder viewHolder = (ContactCardsViewHolder) holder;
        Contact contact = contactPagedList.get(position);
        viewHolder.setContact(contact);
        viewHolder.setWebViewClickListener();
    }

    @Override
    public int getItemCount()
    {
        if (contactPagedList!= null)
            return contactPagedList.size();
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
        }

        public void setContact(Contact contact)
        {
            contactCardView.setContactPhoto(contact.photoStream);
            contactCardView.setBackgroundPhoto(contact.backgroundPhotoStream);
            contactCardView.setLogoPhoto(contact.logoPhotoStream);

            String html = contact.getHtml();
            contactCardView.loadUrl(html);
        }

        public void setWebViewClickListener()
        {

        }
    }
}
