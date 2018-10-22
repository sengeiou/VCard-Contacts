package com.chebyr.vcardrealm.contacts.viewmodel;

public class VCardFieldFormat
{
    protected String ID;
    protected String contentDescription;
    protected long textColor;       // RGB
    protected float textSize;       // sp
    protected int textStyle;        // Typeface.BOLD, Typeface.ITALIC
    protected int visibility;       // View.GONE, View.VISIBLE

    public VCardFieldFormat(String ID, String contentDescription, long textColor, float textSize, int textStyle, int visibility)
    {
        this.ID = ID;
        this.contentDescription = contentDescription;
        this.textColor = textColor;
        this.textSize = textSize;
        this.textStyle = textStyle;
        this.visibility = visibility;
    }
}
