package com.chebyr.vcardrealm.contacts.html.view;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.chebyr.vcardrealm.contacts.html.viewmodel.TemplateParser;

/* VCardView is the View displayed to the user during editing and runtime callerID. It generates VCard display
based on the format defined in VCardSettings and contact details from the contact ContactAccessor */

public class VCardView extends LinearLayout implements View.OnLongClickListener
{
    private static final String TAG = "VCardView";
    public static boolean runtimeMode;

    private TemplateParser templateParser;

    public WindowManager.LayoutParams mLayoutParams;

    public VCardView(Context context, AttributeSet attr)
    {
        super(context, attr);
        Class contextClass = context.getClass();

        Log.d(TAG, "Parent context = " + contextClass);

        setOnLongClickListener(this);

        //WindowManager.LayoutParams (int w, int h, int xpos, int ypos, int _type, int _flags, int _format)
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);

        // TYPE_PRIORITY_PHONE priority phone UI, which needs to be displayed even if the keyguard is active.
        // TYPE_SYSTEM_OVERLAY system overlay windows, which need to be displayed on top of everything else.
    }

    public void setVCardDocument(TemplateParser templateParser)
    {
        Log.d(TAG, "Set new templateParser for:"+this);
        this.templateParser = templateParser;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        Log.d(TAG, "onFinishInflate");

        //updateLayout();
    }

    @Override
    public boolean onLongClick(View view)
    {
        Log.d(TAG, "Long Click");

        // create it from the object's tag
        ClipData.Item item = new ClipData.Item(TAG);

        String[] mimeTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN };
        ClipData clipData = new ClipData(TAG, mimeTypes, item);
        DragShadowBuilder shadowBuilder = new DragShadowBuilder(view);

        view.startDrag(clipData, //clipData to be dragged
                shadowBuilder, //drag shadow
                view, //local clipData about the drag and drop operation
                0   //no needed flags
        );

        setVisibility(View.INVISIBLE);
        return true;
    }

    public void moveYDelta(float dragYPosition, float dropYPosition)
    {
        float originalYPosition = getY();
        float deltaYPosition = dropYPosition - dragYPosition;
        float newYPosition = originalYPosition + deltaYPosition;

        Log.d(TAG, "dragYPosition:" + dragYPosition + ", dropYPosition" + dropYPosition + ", deltaYPosition:" + deltaYPosition);
        Log.d(TAG, "originalYPosition:" + originalYPosition + ", newYPosition:" + newYPosition);
        setY(newYPosition);
        setVisibility(View.VISIBLE);

        templateParser.setLayoutTopMargin(newYPosition);
    }



    public void updateLayout()
    {
        if(isInEditMode() || !templateParser.isVCardAssigned)
            return;

        // Ideally, VCard would be dynamically inflated at runtime from user selected layout template
        // For performance reasons, view inflation relies heavily on pre-processing of XML files that is done at build time.
        // Therefore, it is not currently possible to use LayoutInflater with an XmlPullParser over a plain XML file at runtime.
        // Till the feature is provided, use template files identical to layout files and parse manually.

        mLayoutParams.x = 0;
        mLayoutParams.y = templateParser.getLayoutTopMargin();
        mLayoutParams.gravity = Gravity.TOP;

        int backgroundColor = (int) templateParser.getBackgroundColor();
        int backgroundAlpha = (int) templateParser.getBackgroundAlpha(); //0 means fully transparent, and 255 means fully opaque.

        ColorDrawable backgroundDrawable = (ColorDrawable)getBackground();
        //backgroundDrawable.setAlpha(backgroundAlpha);
        backgroundColor = (backgroundAlpha << 24) | (backgroundColor & 0x00FFFFFF);
        backgroundDrawable.setColor(backgroundColor);
        //setBackgroundDrawable(backgroundDrawable);

        Log.d(TAG, "Layout Top:" + mLayoutParams.y + " backgroundAlpha:" + backgroundAlpha + " backgroundColor:" + backgroundColor);

        templateParser.applyFormat();
    }
}
