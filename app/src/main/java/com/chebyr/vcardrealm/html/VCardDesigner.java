package com.chebyr.vcardrealm.html;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class VCardDesigner extends Fragment implements View.OnClickListener, View.OnDragListener
{
    public static final String TAG = "VCardDesigner";
    StudioActivity mActivity;

    private BackgroundCanvas backgroundCanvas;
    VCardView mVCardView;
    View mIntroductionSplash;

    public VCardDesigner()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");

        mActivity = (StudioActivity)getActivity();
        setHasOptionsMenu(true);

        Log.d(TAG, "mVCardView:" + mVCardView + "mActivity.mVCardView:" + mActivity.mVCardView);
        mVCardView = mActivity.mVCardView;
        mVCardView.invalidate();
        mVCardView.setOnDragListener(this);

        backgroundCanvas = new BackgroundCanvas(mActivity);
        backgroundCanvas.setOnClickListener(this);
        backgroundCanvas.setOnDragListener(this);

        if(!mActivity.mAppInitialized)
            mIntroductionSplash = inflater.inflate(R.layout.introduction, null);

        return backgroundCanvas;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        mActivity.setTitle(R.string.title_section1);
        mVCardView.updateLayout();
        backgroundCanvas.addView(mVCardView);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
        backgroundCanvas.removeView(mVCardView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if(!mActivity.mAppInitialized)
        {
            mIntroductionSplash.setOnClickListener(this);
            backgroundCanvas.addView(mIntroductionSplash);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.designer, menu);

        Log.d(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId())
        {
            case R.id.action_set_active_vcard:
            {
                mActivity.onUpdateCallerIDVCard();
                return true;
            }
            case R.id.action_edit_vcard:
            {
                mActivity.onEditVCard();
                return true;
            }
            case R.id.action_preview_callerid:
            {
                if(mActivity.onPreviewCallerID(mPreviewCompletedRunnable))
                    mVCardView.setVisibility(View.INVISIBLE);

                return true;
            }
            case R.id.action_share_vcard:
            {
                mActivity.onShareVCard();
                return true;
            }
            case R.id.action_open_vcard:
            {
                mActivity.onOpenVCardFile();                   // Display file selector dialog
                return true;
            }
            case R.id.action_save_vcard:
            {
                if(mActivity.mActiveFileName != null)
                    mActivity.saveVCardFile(mActivity.mActiveFileName);
                else
                    mActivity.onSaveVCardFile();

                return true;
            }
            case R.id.action_save_vcard_as:
            {
                mActivity.onSaveVCardFile();
                return true;
            }
            case R.id.action_save_vcard_snapshot:
            {
                Bitmap vCardBitmap = mVCardView.getDrawingCache();
                mActivity.onSaveVCardSnapshot(vCardBitmap);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Runnable mPreviewCompletedRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mVCardView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.introduction_splash)
        {
            backgroundCanvas.removeView(mIntroductionSplash);
            mActivity.onClickIntroductionSplash();
            return;
        }

        mActivity.toggleSystemUIVisibility();
    }

    @Override
    public boolean onDrag(View view, DragEvent event)
    {
        Log.d(TAG, "onDrag:" + view.getClass() + ", " + event.getAction());
        return false;
    }

    private class BackgroundCanvas extends FrameLayout
    {
        private static final String TAG = "BackgroundCanvas";

        private float vCardDragYPosition;

        public BackgroundCanvas(Context context)
        {
            super(context);
            setWillNotDraw(false);
        }

        public BackgroundCanvas(Context context, AttributeSet attr)
        {
            super(context, attr);
            setWillNotDraw(false);
        }

        @Override
        public boolean onDragEvent(DragEvent dragEvent)
        {
            // Handles each of the expected events
            switch (dragEvent.getAction())
            {
                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d(TAG,"ACTION_DRAG_STARTED");
                    vCardDragYPosition = dragEvent.getY();
                    break;

                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d(TAG,"ACTION_DRAG_ENTERED");
                    break;

                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d(TAG,"ACTION_DRAG_EXITED");
                    break;

                //drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP:
                    float vCardDropYPosition = dragEvent.getY();
                    mVCardView.moveYDelta(vCardDragYPosition, vCardDropYPosition);
                    Log.d(TAG,"ACTION_DROP");
                    break;

                //the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d(TAG,"ACTION_DRAG_ENDED");
                    break;
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.LTGRAY);

            float gridSize = 50;

            float canvasWidth = canvas.getWidth();
            float canvasHeight = canvas.getHeight();

            for(int rowY = 0; rowY < canvasHeight; rowY+= gridSize)
            {
                for(int colX = 0; colX < canvasWidth; colX+= gridSize)
                {
                    canvas.drawCircle(colX, rowY, 2, paint);
                }
            }
        }
    }

}