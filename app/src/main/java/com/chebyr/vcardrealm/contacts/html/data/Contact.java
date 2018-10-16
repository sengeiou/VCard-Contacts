package com.chebyr.vcardrealm.contacts.html.data;

public class Contact
{
    public long contactID;

    public ContactData data;
    public ContactDetailsData details;
    public GroupData groups;
    public TemplateData template;

    public String vcardHtml;

    public String incomingNumber;

    public Contact()
    {

    }

    public long getId()
    {
        return contactID;
    }

    public void addContactData(ContactData contactData)
    {
        this.data = contactData;
    }

    public void addContactDetailsData(ContactDetailsData contactDetails)
    {
        this.details = contactDetails;
    }

    public void addGroupData(GroupData groupData)
    {
        this.groups = groupData;
    }


    @Override
    public boolean equals(Object object)
    {
        if(object.getClass() == Contact.class)
        {
            Contact newContact = (Contact)object;
            return contactID == newContact.contactID;
        }
        return false;
    }
}
