package com.chebyr.vcardrealm.contacts.html.datasource.data;

public class GroupData
{
    public long contactID;
    public String groupTitle;

    static String separator = ", ";

    public void addGroup(String groupTitle)
    {
        if ((this.groupTitle.length() > 0) && (this.groupTitle.length() > 0))
            this.groupTitle += separator + groupTitle;
        else
            this.groupTitle = groupTitle;

    }
}
