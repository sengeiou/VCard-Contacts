package com.chebyr.vcardrealm.contacts.data;

public class GroupData
{
    public String groupTitle;

    static String separator = ", ";

    public void addGroup(String groupTitle)
    {
        if(this.groupTitle == null)
            this.groupTitle = groupTitle;
        else if((this.groupTitle.length() > 0) && (this.groupTitle.length() > 0))
            this.groupTitle += separator + groupTitle;
        else
            this.groupTitle = groupTitle;

    }
}
