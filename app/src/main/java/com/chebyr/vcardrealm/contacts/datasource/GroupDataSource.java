package com.chebyr.vcardrealm.contacts.datasource;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.chebyr.vcardrealm.contacts.data.GroupData;
import com.chebyr.vcardrealm.contacts.datasource.queries.GroupsQuery;

public class GroupDataSource
{
    private static String TAG = GroupDataSource.class.getSimpleName();

    private Context context;
    private ContentResolver contentResolver;
    private String filterState;

    public GroupDataSource(Context context, String filterState)
    {
        this.filterState = filterState;
        contentResolver = context.getContentResolver();
    }

    public void setFilterState(String filterState)
    {
        this.filterState = filterState;
    }

    public GroupData getGroupData(String groupRowID)
    {
        GroupData groupData = null;
        String[] whereParams = new String[]{String.valueOf(groupRowID)};

        // Get Titles from Groups table using groupIDs from groupRowID
        Cursor groupCursor = contentResolver.query(GroupsQuery.URI, GroupsQuery.PROJECTION, GroupsQuery.SELECTION, whereParams, GroupsQuery.SORT_ORDER);
        if(groupCursor != null)
        {
            groupData = getGroupCursorData(groupCursor);
            groupCursor.close();
        }
        return groupData;
    }

    private GroupData getGroupCursorData(Cursor groupCursor)
    {
        GroupData groupData = new GroupData();
        long lastGroupID = 0;
        for(groupCursor.moveToFirst(); !groupCursor.isAfterLast(); groupCursor.moveToNext())
        {
            long groupID = groupCursor.getLong(groupCursor.getColumnIndex(GroupsQuery.GROUP_ID));
            String groupTitle = groupCursor.getString(groupCursor.getColumnIndex(GroupsQuery.TITLE));

            if(groupID == lastGroupID)
                groupData.addGroup(groupTitle);
            else
            {
                groupData = new GroupData();
                groupData.addGroup(groupTitle);
            }

//            Log.d(TAG, "GroupRowID: " + groupID + " Group Title: " + groupTitle);
        }
        return groupData;
    }
}
