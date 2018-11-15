package com.chebyr.vcardrealm.contacts.view;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.common.widget.CompositeCursorAdapter;
import com.android.contacts.common.ContactPhotoManager;
import com.android.contacts.common.list.ContactListFilter;
import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.data.Contact;

import java.util.TreeSet;

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
        void onContactCardClick(int position, Contact contact);
    }

    public class ContactCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ContactCardView contactCardView;

        public ContactCardsViewHolder(View itemView)
        {
            super(itemView);
            contactCardView = itemView.findViewById(R.id.contact_card_view);
            contactCardView.initialize();
            contactCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            int adapterPosition = getAdapterPosition();
            Contact contact = contactCardView.getContact();
            callBack.onContactCardClick(adapterPosition, contact);
        }

        public void setContact(Contact contact)
        {
            contactCardView.setContact(contact);
        }

    }



    // Dummp for now
    public TreeSet<Long> getSelectedContactIds()
    {
        return null;
    }

    public void setSelectedContactIds(TreeSet<Long> selectedContactIds)
    {

    }

    public void setSelectedContactsListener(Fragment fragment)
    {

    }

    public void setDisplayCheckBoxes(boolean displayCheckBoxes)
    {
    }

    public Uri getContactUri(int position)
    {
        return null;
    }

    public boolean hasProfile()
    {
        return true;
    }

    public void toggleSelectionOfContactId(long contactID)
    {

    }

    public boolean isDisplayingCheckBoxes()
    {
        return false;
    }

    public boolean areAllPartitionsEmpty()
    {
        return true;
    }

    public boolean isLoading()
    {
        return false;
    }

    public void setFilter(ContactListFilter mFilter)
    {

    }

    public void setIncludeProfile(boolean searchMode)
    {

    }

    public int getPartitionCount()
    {
        return 0;
    }

    public CompositeCursorAdapter.Partition getPartition(int i)
    {
        return null;
    }

    public void setSelectedContact(long mSelectedContactDirectoryId, String mSelectedContactLookupKey, long mSelectedContactId)
    {

    }

    public int getSelectedContactPosition()
    {
        return 0;
    }

    public int getCount()
    {
        return 0;
    }

    public Uri getFirstContactUri()
    {
        return null;
    }

    public void setPhotoLoader(ContactPhotoManager photoLoader)
    {

    }

    public void clearPartitions()
    {

    }

    public void onDataReload()
    {

    }

    public void setSectionHeaderDisplayEnabled(boolean flag)
    {

    }

    public void setSearchMode(boolean flag)
    {

    }

    public void removeDirectoriesAfterDefault()
    {

    }

    public void configureDefaultPartition(boolean value, boolean flag)
    {

    }

    public void setQueryString(String queryString)
    {

    }

    public void setContactNameDisplayOrder(int displayOrder){}

    public void setSortOrder(int sortOrder){}

    public void setFragmentRootView(View view){}
    
    public void setQuickContactEnabled(boolean mQuickContactEnabled){}
    public void setAdjustSelectionBoundsEnabled(boolean mAdjustSelectionBoundsEnabled){}
    public void setDirectorySearchMode(int mDirectorySearchMode){}
    public void setPinnedPartitionHeadersEnabled(boolean value){}
    public void setSelectionVisible(boolean mSelectionVisible){}
    public void setDirectoryResultLimit(int mDirectoryResultLimit){}
    public void setDarkTheme(boolean mDarkTheme){}

}
