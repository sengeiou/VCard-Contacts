package com.chebyr.vcardrealm.html;

/* VCardDocument is the in memory XML representation of the format of the VCard used for VCardView.
Also handles the  file read / write operations to internal / external storage media / assets directory as well as bundle*/

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;

import com.chebyr.vcardrealm.contacts.Contact;

import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class VCardDocument
{
    private static final String TAG = "VCardDocument";

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

    Context context;
    Document document;
    Element documentElement;

    public boolean isVCardAssigned = false;

    public VCardDocument(Context context)
    {
    }

    public boolean parseInputStream(InputStream inputStream)
    {
        try
        {
            document = Jsoup.parse(inputStream, "UTF-8", "");

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

        Element element = getElementByID(ID);

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
        Element element = getElementByID(ID);
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

    private Element getElementByID(String ID)
    {
        return document.getElementById(ID);
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

    public void updateContactDetails(Contact contact)
    {
        if(contact.photo != null)
        {
            // TODO: photoView.setImageBitmap(contact.photo);
            //photoView.setVisibility(VISIBLE);
        }
        else if(contact.photoURI != null)
        {
            photoView.attr("src", contact.photoURI.getPath());
            //photoView.setVisibility(VISIBLE);
        }
        else
        {
            //photoView.setVisibility(INVISIBLE);
        }

        setContactField(incomingNumberView, contact.incomingNumber);
        setContactField(displayNameView, contact.displayName);
        setContactField(nickNameView, contact.nickName);
        setContactField(jobTitleView, contact.jobTitle);
        setContactField(organizationView, contact.organization);
        setContactField(addressView, contact.address);
        setContactField(groupView, contact.groups);
        setContactField(phoneNumbersView, contact.phoneNumbers);
        setContactField(eMailsView, contact.eMails);
        setContactField(IMsView, contact.IMs);
        setContactField(webSiteView, contact.webSite);
        setContactField(notesView, contact.notes);
    }

    public void setContactField(Element contactField, String value)
    {
        if(value != null)
        {
            contactField.text(value);
        }
        else
        {
            contactField.text("");
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
