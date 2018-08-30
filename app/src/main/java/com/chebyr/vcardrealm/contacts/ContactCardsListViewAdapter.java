package com.chebyr.vcardrealm.contacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ContactCardsListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = "ContactCardsListViewAdapter";
    private ArrayList<String> contactCardPaths;

    private OnItemClickCallBack callBack;

    public ContactCardsListViewAdapter(OnItemClickCallBack callBack)
    {
        this.callBack        = callBack;
        contactCardPaths = new ArrayList<>();
    }

    public void addData(String path)
    {
        contactCardPaths.add(path);
        notifyItemInserted(contactCardPaths.size() - 1);
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
        viewHolder.setHtml(contactCardPaths.get(position));
        viewHolder.setWebViewClickListener();
    }

    @Override
    public int getItemCount() {
        return contactCardPaths.size();
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
        ContactCard contactCard;

        public ContactCardsViewHolder(View itemView)
        {
            super(itemView);

            contactCard = itemView.findViewById(R.id.contact_card);
        }

        public void setHtml(String html)
        {
            contactCard.setHtml(html);
        }

        public void setWebViewClickListener()
        {

        }
    }
}
