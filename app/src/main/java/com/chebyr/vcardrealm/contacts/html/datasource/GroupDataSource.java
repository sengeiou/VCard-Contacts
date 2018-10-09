package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;
import com.chebyr.vcardrealm.contacts.html.datasource.queries.GroupsQuery;
import com.chebyr.vcardrealm.contacts.html.repository.ContactRepository;

public class GroupDataSource extends PositionalDataSource<GroupData>
{
    Context context;
    ContentResolver contentResolver;

    public GroupDataSource(Context context, ContactRepository contactRepository)
    {
        contentResolver = context.getContentResolver();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<GroupData> callback) {

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<GroupData> callback) {

    }

    public GroupData getGroups(String groupRowID)
    {
        GroupData groupData = new GroupData();

        // Get Titles from Groups table using groupIDs from groupRowID
        if(groupRowID != null)
        {
            String[] whereParameters = new String[] {groupRowID};

            Cursor groupCursor = contentResolver.query(GroupsQuery.URI, GroupsQuery.PROJECTION, GroupsQuery.SELECTION, whereParameters, null);

            if(groupCursor == null)
                return groupData;

            for(groupCursor.moveToFirst(); !groupCursor.isAfterLast(); groupCursor.moveToNext())
            {
                String group = groupCursor.getString(groupCursor.getColumnIndex(GroupsQuery.TITLE));
                groupData.addGroup(group);
            }
            groupCursor.close();
        }
        return groupData;
    }

    public static class Factory extends DataSource.Factory<Integer, GroupData>
    {
        private Context context;
        private ContactRepository contactRepository;

        public Factory(Context context, ContactRepository contactRepository)
        {
            this.context = context;
            this.contactRepository = contactRepository;
        }

        public void setFilter(String filterState)
        {

        }

        @Override
        public DataSource<Integer, GroupData> create()
        {
            return new GroupDataSource(context, contactRepository);
        }
    }

}
