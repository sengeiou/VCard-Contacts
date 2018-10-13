package com.chebyr.vcardrealm.contacts.html.viewmodel;

/* TemplateParser manages in memory HTML DOM representation of the format of the VCard Template */

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TemplateParser
{
    private static final String TAG = TemplateParser.class.getSimpleName();

    private Context context;

    private static String photoID = "contact_photo";
    private static String incomingNumberID = "incoming_number";
    private static String displayNameID = "display_name";
    private static String nickNameID = "nick_name";
    private static String jobTitleID = "job_title";
    private static String organizationID = "organization";
    private static String addressID = "address";
    private static String groupID = "groups";
    private static String phoneNumbersID = "phone_numbers";
    private static String eMailsID = "emails";
    private static String IMsID = "instant_messengers";
    private static String webSiteID = "website";
    private static String notesID = "notes";

    private Document document;
    private Element documentElement;
    private Element photoView;
    private Element incomingNumberView;
    private Element displayNameView;
    private Element nickNameView;
    private Element jobTitleView;
    private Element organizationView;
    private Element addressView;
    private Element groupView;
    private Element phoneNumbersView;
    private Element eMailsView;
    private Element IMsView;
    private Element webSiteView;
    private Element notesView;

    public boolean isVCardAssigned = false;

    public TemplateParser(Context context)
    {
    }

    public String generateVCardHtml(Contact contact)
    {
        Log.d(TAG, "Generate VCard HTML: " + contact);
        if((contact.template == null) || (contact.data == null))
            return null;

        Log.d(TAG, "contact.template.htmlStream: " + contact.template.htmlStream);

        parseInputStream(contact.template.htmlStream);
        updateContactDetails(contact);
        return getVCardHTML();
    }

    public boolean parseInputStream(InputStream inputStream)
    {
        try
        {
            document = Jsoup.parse(inputStream, "UTF-8", "");
            Log.d(TAG, "Parsing html VCard: " + document);

            photoView = document.getElementById(photoID);
            incomingNumberView = document.getElementById(incomingNumberID);
            displayNameView = document.getElementById(displayNameID);
            nickNameView = document.getElementById(nickNameID);
            jobTitleView = document.getElementById(jobTitleID);
            organizationView = document.getElementById(organizationID);
            addressView = document.getElementById(addressID);
            groupView = document.getElementById(groupID);
            phoneNumbersView = document.getElementById(phoneNumbersID);
            eMailsView = document.getElementById(eMailsID);
            IMsView = document.getElementById(IMsID);
            webSiteView = document.getElementById(webSiteID);
            notesView = document.getElementById(notesID);

            isVCardAssigned = true;
//            Log.d(TAG, "Loaded new document. documentElement.name: " + documentElement.getTagName());

            return true;//
        }
        catch(Exception exception)
        {
            Log.d(TAG, exception.toString());
            return false;
        }
    }

    public String getVCardHTML()
    {
        return document.outerHtml();
    }

    public void setVCardHTML(String vCardHTML)
    {
        document = Jsoup.parse(vCardHTML);

        try
        {
            getBackgroundColor();
            isVCardAssigned = true;
        }
        catch (Exception e)
        {
            isVCardAssigned = false;
        }
    }

    public VCardFieldFormat getVCardFieldFormat(String ID)
    {
//            Log.d(TAG, "documentElement:" + documentElement);
//            Log.d(TAG, "get format for ID:" + ID);

        Element element = document.getElementById(ID);

//            Log.d(TAG, "Element:" + element);

        String contentDescription = getContentDescription(element);

        long textColor = getTextColor(element);
        textColor |= 0xFF000000;    // Full Alpha

        float textSize = getTextSize(element);
        int textStyle = getTextStyle(element);
        int visibility = getVisibility(element);

        return new VCardFieldFormat(ID, contentDescription, textColor, textSize, textStyle, visibility);
    }

    enum VCardAttributeType {eTextColor, eTextSize, eTextStyleBold, eTextStyleItalic, eVisibility, eBackgroundColor, eBackgroundAlpha, eLayoutTopMargin};

    public boolean updateFieldAttribute(String ID, VCardAttributeType vCardAttribute, Number value)
    {
        Element element = document.getElementById(ID);
        if(element == null)
        {
//            Log.d(TAG, "Field:" + ID +" not found in XML document");
            return false;
        }

//        Log.d(TAG, "Update Field:" + ID + " Value=" + value);

        switch (vCardAttribute)
        {
            case eTextColor:
            {
                setTextColor(element, value.intValue());
                break;
            }

            case eTextSize:
            {
                setTextSize(element, value.floatValue());
                break;
            }

            case eTextStyleBold:
            {
                int textStyle = getTextStyle(element);
                textStyle = (textStyle & ~Typeface.BOLD) | (Integer)value; // Update Bold flag
                setTextStyle(element, textStyle);
                break;
            }

            case eTextStyleItalic:
            {
                int textStyle = getTextStyle(element);
                textStyle = (textStyle & ~Typeface.ITALIC) | value.intValue(); // Update Italic flag
                setTextStyle(element, textStyle);
                break;
            }

            case eVisibility:
            {
                setVisibility(element, value.intValue());
                break;
            }

            case eBackgroundColor:
            {
                setBackgroundColor(value.longValue());
                break;
            }

            case eBackgroundAlpha:
            {
                setBackgroundAlpha(value.longValue());
                break;
            }

            case eLayoutTopMargin:
            {
                setLayoutTopMargin(value.longValue());
            }
        }
        return true;
    }

    public long getBackgroundColor()
    {
        long background = getBackground(documentElement);
        return background | 0xFF000000;
    }

    public void setBackgroundColor(long color)
    {
        long background = getBackground(documentElement);
        background = (background & 0xFF000000) | (color & 0x00FFFFFF);
        setBackground(documentElement, background);
    }

    public long getBackgroundAlpha()
    {
        long background = getBackground(documentElement);
        background &= 0xFF000000;
        background = background >> 24;

        return background;
    }

    public void setBackgroundAlpha(long alpha)
    {
        long background = getBackground(documentElement);
        alpha = alpha << 24;
        background = (background & 0x00FFFFFF) | (alpha & 0xFF000000);
        setBackground(documentElement, background);
    }

    private long getBackground(Element element)
    {
        String backgroundStr = element.attributes().get("android:background");
        long background = 0;
        if(backgroundStr.startsWith("#"))
        {
            backgroundStr = backgroundStr.substring(1);
            background = Long.parseLong(backgroundStr, 16);
        }
        else
        {
            background = Long.parseLong(backgroundStr, 10);
        }
//        Log.d(TAG, "background:" + backgroundStr + " long value:" + background);
        return background;
    }

    private void setBackground(Element element, long background)
    {
        String backgroundStr = "#" + Long.toHexString(background);
        element.attr("android:background", backgroundStr);
//        Log.d(TAG, "Updated android:background to " + backgroundStr);
    }

    private float getTextSize(Element element)
    {
        String textSizeStr = element.attributes().get("android:textSize");
        if (textSizeStr.equals(""))
            textSizeStr = "12sp";

        textSizeStr = textSizeStr.substring(0, textSizeStr.length() - 2);
        return Float.parseFloat(textSizeStr);
    }

    private void setTextSize(Element element, float textSize)
    {
        String textSizeStr = Float.toString(textSize) + "sp";
        element.attr("android:textSize", textSizeStr);
//        Log.d(TAG, "Updated android:textSize to " + textSizeStr);
    }

    private int getVisibility(Element element)
    {
        String visibilityStr = element.attributes().get("android:visibility");
        if (visibilityStr.equals(""))
            visibilityStr = "visible";

        int visibility = 0;
        if (visibilityStr.equals("visible"))
            visibility |= View.VISIBLE;
        else
            visibility |= View.GONE;

        return visibility;
    }

    private void setVisibility(Element element, Integer visibility)
    {
        String visibilityStr;

        if((visibility == View.VISIBLE))
            visibilityStr = "visible";
        else
            visibilityStr = "gone";

        element.attr("android:visibility", visibilityStr);
//        Log.d(TAG, "Updated android:visibility to " + visibilityStr);
    }

    private long getTextColor(Element element)
    {
        String textColorStr = element.attributes().get("android:textColor");
  //      Log.d(TAG, "Text Color Attribute:" + textColorStr);

        if (textColorStr.equals(""))
            textColorStr = "#FFFFFF";

        long textColor = 0;
        if (textColorStr.startsWith("#"))
        {
            textColorStr = textColorStr.substring(1);
//            Log.d(TAG, "Text Color:" + textColorStr);
            textColor = Long.parseLong(textColorStr, 16);
        } else
        {
            textColor = Long.parseLong(textColorStr, 10);
        }
        return textColor;
    }

    private void setTextColor(Element element, int textColor)
    {
        String textColorStr = "#" + Integer.toHexString(textColor);
        element.attr("android:textColor", textColorStr);
//        Log.d(TAG, "Updated android:textColor to" + textColorStr);
    }

    private int getTextStyle(Element element)
    {
        String textStyleStr = element.attributes().get("android:textStyle");
        if (textStyleStr.equals(""))
            textStyleStr = "normal";

        int textStyle = 0;
        if (textStyleStr.contains("bold"))
            textStyle |= Typeface.BOLD;

        if (textStyleStr.contains("italic"))
            textStyle |= Typeface.ITALIC;

        return textStyle;
    }

    private void setTextStyle(Element element, int textStyle)
    {
        String textStyleStr = "normal";

        if((textStyle & Typeface.BOLD) != 0)
            textStyleStr = textStyleStr.concat("|bold");

        if((textStyle & Typeface.ITALIC) != 0)
            textStyleStr = textStyleStr.concat("|italic");

        element.attr("android:textStyle", textStyleStr);
//        Log.d(TAG, "Updated android:textStyle to " + textStyleStr);
    }

    public int getLayoutTopMargin()
    {
        try
        {
            String layoutTopMarginStr = documentElement.attributes().get("android:layoutTopMargin");

            if (layoutTopMarginStr.equals(""))
                layoutTopMarginStr = "0dp";

            layoutTopMarginStr = layoutTopMarginStr.substring(0, layoutTopMarginStr.length() - 2);

            return (int) Float.parseFloat(layoutTopMarginStr);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public void setLayoutTopMargin(float layoutTopMargin)
    {
        String layoutTopMarginStr = Float.toString(layoutTopMargin) + "sp";
        documentElement.attr("android:layoutTopMargin", layoutTopMarginStr);
//        Log.d(TAG, "Updated android:layoutTopMargin to " + layoutTopMarginStr);
    }

    private String getContentDescription(Element element)
    {
        return element.attributes().get("android:contentDescription");
    }

    public void applyFormat(Element textView, VCardFieldFormat vCardFieldFormat)
    {
        // TODO: Will implement CSS parser functionality for editor
        if(vCardFieldFormat == null)
        {
            Log.d(TAG, "Unable to style VCard Field: Null VCard Format supplied");
            return;
        }
        /*
        if(textView.getVisibility() == GONE)
            return;

        textView.setTextColor((int) vCardFieldFormat.textColor);
        textView.setTextSize(vCardFieldFormat.textSize);
        textView.setTypeface(null, vCardFieldFormat.textStyle);
        textView.setVisibility(vCardFieldFormat.visibility);
        */
    }

    private void updateContactDetails(Contact contact)
    {
        if(contact.data != null) {
            if (contact.data.photoStream != null)
            {
                // TODO: photoView.setImageBitmap(contact.photo);
                //photoView.setVisibility(VISIBLE);
                photoView.attr("visibility", "hidden");
            }
            else if (contact.data.photoURI != null)
            {
                photoView.attr("src", contact.data.photoURI.getPath());
                //photoView.setVisibility(VISIBLE);
                photoView.attr("display", "block");
            }
            else
                {
                //photoView.setVisibility(INVISIBLE);
                photoView.attr("visibility", "hidden");
            }
            setContactField(incomingNumberView, contact.data.incomingNumber);
            setContactField(displayNameView, contact.data.displayName);
        }

        if(contact.details != null)
        {
            setContactField(nickNameView, contact.details.nickName);
            setContactField(jobTitleView, contact.details.jobTitle);
            setContactField(organizationView, contact.details.organization);
            setContactField(addressView, contact.details.address);
            setContactField(phoneNumbersView, contact.details.phoneNumbers);
            setContactField(eMailsView, contact.details.eMails);
            setContactField(IMsView, contact.details.IMs);
            setContactField(webSiteView, contact.details.website);
            setContactField(notesView, contact.details.notes);
        }

        if(contact.groups != null)
        {
            setContactField(groupView, contact.groups.groupTitle);
        }
    }

    private void setContactField(Element element, String value)
    {
        if(element == null)
            return;

        if(value != null)
        {
            element.text(value);
        }
        else
        {
            element.text("");
        }
    }

    public void applyFormat()
    {
        VCardFieldFormat vCardFieldFormat;

        vCardFieldFormat = getVCardFieldFormat(incomingNumberID);
        applyFormat(incomingNumberView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(displayNameID);
        applyFormat(displayNameView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(nickNameID);
        applyFormat(nickNameView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(jobTitleID);
        applyFormat(jobTitleView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(organizationID);
        applyFormat(organizationView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(addressID);
        applyFormat(addressView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(phoneNumbersID);
        applyFormat(phoneNumbersView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(groupID);
        applyFormat(groupView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(eMailsID);
        applyFormat(eMailsView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(IMsID);
        applyFormat(IMsView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(webSiteID);
        applyFormat(webSiteView, vCardFieldFormat);

        vCardFieldFormat = getVCardFieldFormat(notesID);
        applyFormat(notesView, vCardFieldFormat);

        //vCardFieldFormat = mVCardDocument.getVCardFieldFormat("contact_photo");
        //photoView.setVisibility(vCardFieldFormat.visibility);


    }
}
