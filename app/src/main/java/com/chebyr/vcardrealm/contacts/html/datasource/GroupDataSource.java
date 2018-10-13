package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactDetailsData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.GroupData;
import com.chebyr.vcardrealm.contacts.html.datasource.queries.GroupsQuery;

import java.util.ArrayList;
import java.util.List;

public class GroupDataSource extends PositionalDataSource<GroupData>
{
    private static String TAG = GroupDataSource.class.getSimpleName();

    private Context context;
    private ContentResolver contentResolver;
    private List<ContactDetailsData> contactDetailsDataList;
    private String filterState;

    public GroupDataSource(Context context, String filterState, List<ContactDetailsData> contactDetailsDataList)
    {
        this.contactDetailsDataList = contactDetailsDataList;
        this.filterState = filterState;
        contentResolver = context.getContentResolver();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<GroupData> callback)
    {
        List<GroupData> groupDataList = getGroupList();
        callback.onResult(groupDataList, params.requestedStartPosition);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<GroupData> callback)
    {
        List<GroupData> groupDataList = getGroupList();
        callback.onResult(groupDataList);
    }

    private List<GroupData> getGroupList()
    {
        List<GroupData> groupDataList = new ArrayList<>();

        if(contactDetailsDataList == null)
            return groupDataList;

        for(int count = 0; count < contactDetailsDataList.size(); count++)
        {
            ContactDetailsData contactDetailsData = contactDetailsDataList.get(count);
            String groupRowID = contactDetailsData.groupRowID;
            String[] whereParams = new String[]{String.valueOf(groupRowID)};

            // Get Titles from Groups table using groupIDs from groupRowID
            Cursor groupCursor = contentResolver.query(GroupsQuery.URI, GroupsQuery.PROJECTION, GroupsQuery.SELECTION, whereParams, GroupsQuery.SORT_ORDER);
            if(groupCursor != null)
            {
                GroupData groupData = getGroupData(groupCursor);
                groupData.contactID = contactDetailsData.contactID;
                groupDataList.add(groupData);
                groupCursor.close();
            }
        }

        return groupDataList;
    }

    private GroupData getGroupData(Cursor groupCursor)
    {
        GroupData groupData = new GroupData();
        long lastGroupID = 0;
        for(groupCursor.moveToFirst(); !groupCursor.isAfterLast(); groupCursor.moveToNext())
        {
            long groupID = groupCursor.getLong(GroupsQuery.QUERY_ID);
            String groupTitle = groupCursor.getString(groupCursor.getColumnIndex(GroupsQuery.TITLE));

            if(groupID == lastGroupID)
                groupData.addGroup(groupTitle);
            else
            {
                groupData = new GroupData();
                groupData.addGroup(groupTitle);
            }

            Log.d(TAG, "GroupRowID: " + groupID + " Group Title: " + groupTitle);
        }
        return groupData;
    }

    public static class Factory extends DataSource.Factory<Integer, GroupData>
    {
        private Context context;
        private List<ContactDetailsData> contactDetailsDataList;
        private String filterState;

        public Factory(Context context)
        {
            this.context = context;
        }

        public void setFilter(String filterState)
        {
            this.filterState = filterState;
        }

        public void setContactDetailsDataList(List<ContactDetailsData> contactDetailsDataList)
        {
            this.contactDetailsDataList = contactDetailsDataList;
        }

        @Override
        public DataSource<Integer, GroupData> create()
        {
            Log.d(TAG, "Create GroupDataSource");
            return new GroupDataSource(context, filterState, contactDetailsDataList);
        }
    }

}
