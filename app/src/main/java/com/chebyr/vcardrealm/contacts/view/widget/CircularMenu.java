package com.chebyr.vcardrealm.contacts.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.chebyr.vcardrealm.contacts.R;
import com.chebyr.vcardrealm.contacts.util.IndentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CircularMenu
 */
public class CircularMenu extends FrameLayout implements View.OnClickListener
{
    private static String TAG = CircularMenu.class.getSimpleName();

    private Context context;

    private static final int DEFAULT_BUTTON_SIZE = 56;
    private static final float DEFAULT_DISTANCE = DEFAULT_BUTTON_SIZE * 1.5f;
    private static final float DEFAULT_RING_SCALE_RATIO = 1.3f;
    private static final float DEFAULT_CLOSE_ICON_ALPHA = 0.3f;
    private static final float DEFAULT_OPEN_SCALE = 0.7f;
    private static final float DEFAULT_CLOSE_SCALE = 1.0f;

    private final List<View> mButtons = new ArrayList<>();

    private FloatingActionButton mMenuButton;
    private RingEffectView mRingView;

    private boolean mClosedState = true;
    private boolean mIsAnimating = false;

    private int mDurationRing;
    private int mDurationOpen;
    private int mDurationClose;
    private int mDesiredSize;

    private float mDistance;

    private Callback callback;

    public CircularMenu(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }

    /**
     * Constructor for creation CircleMenuView in code, not in xml-layout.
     * @param context current context, will be used to access resources.
     * @param icons   buttons icons resource ids array. Items must be @DrawableRes.
     */
    public CircularMenu(@NonNull Context context, @NonNull List<Integer> icons)
    {
        super(context);
        this.context = context;
    }

    /**
     * See {@link Callback }
     * @param callback new event callback or null.
     */
    public void initialize(Menu menu, @Nullable Callback callback)
    {
        this.callback = callback;

        mDurationRing = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mDurationOpen = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mDurationClose = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        final float density = context.getResources().getDisplayMetrics().density;
        mDistance = DEFAULT_DISTANCE * density;

        LayoutInflater.from(context).inflate(R.layout.circular_menu, this, true);

        setWillNotDraw(true);
        setClipChildren(false);
        setClipToPadding(false);

        final float buttonSize = DEFAULT_BUTTON_SIZE * density;

        int ringRadius = (int) (buttonSize + (mDistance - buttonSize / 2));
        mDesiredSize = (int) (ringRadius * 2 * DEFAULT_RING_SCALE_RATIO);

        mRingView = findViewById(R.id.ring_view);

        mMenuButton = findViewById(R.id.circle_menu_main_button);
        mMenuButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        mMenuButton.setOnClickListener((View view) -> toggleMenu());

        final int colorAccent = getResources().getColor(R.color.colorAccent, null);

        MenuItem menuItem;

        for (int i = 0; i < menu.size(); i++)
        {
            menuItem = menu.getItem(i);
            if(menuItem.getGroupId() == R.id.circular_menu_group)
            {
                Log.d(TAG, "Menu Title: " + menuItem.getTitle());

                final FloatingActionButton button = new FloatingActionButton(context);
                int itemId = menuItem.getItemId();
                button.setId(itemId);

                Drawable icon = menuItem.getIcon();

                icon = getApplicationIcon(itemId);

                button.setImageDrawable(icon);

                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setStroke(0, Color.WHITE);
                gradientDrawable.setAlpha(50);
                gradientDrawable.setShape(GradientDrawable.OVAL);
                gradientDrawable.setColor(Color.WHITE);
                //button.setScaleType(ImageView.ScaleType.FIT_XY);
                button.setBackgroundTintList(null);
                button.setBackground(gradientDrawable);
                button.setClickable(true);
                button.setOnClickListener(this);
                button.setScaleX(0);
                button.setScaleY(0);
                button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

                addView(button);
                mButtons.add(button);
            }
        }
    }

    private Drawable getApplicationIcon(int itemId)
    {
        try
        {
            IndentUtil indentUtil = new IndentUtil(context);
            Drawable icon = null;
            switch (itemId)
            {
                case R.id.send_sms:
                    icon = indentUtil.getApplicationIcon(IndentUtil.ANDROID_SMS);
                    break;

                case R.id.send_email:
                    icon = indentUtil.getApplicationIcon(IndentUtil.GMAIL);
                    break;

                case R.id.open_website:
                    break;

                case R.id.open_facebook:
                    icon = indentUtil.getApplicationIcon(IndentUtil.FACEBOOK_APP);
                    break;

                case R.id.open_map:
                    icon = indentUtil.getApplicationIcon(IndentUtil.GOOGLE_MAPS);
                    break;

                case R.id.open_whatsapp:
                    icon = indentUtil.getApplicationIcon(IndentUtil.WHATSAPP);
                    break;

                case R.id.call_phone:
                    icon = indentUtil.getApplicationIcon(IndentUtil.ANDROID_DIALER);
            }
            return icon;
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int w = resolveSizeAndState(mDesiredSize, widthMeasureSpec, 0);
        final int h = resolveSizeAndState(mDesiredSize, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        if (!changed && mIsAnimating) {
            return;
        }
    }

    @Override
    public void onClick(final View view)
    {
        if (mIsAnimating)
            return;

        final Animator click = getButtonClickAnimation((FloatingActionButton) view);
        click.setDuration(mDurationRing);
        click.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mClosedState = true;

                if (callback != null)
                    callback.onMenuButtonClick(view.getId());
            }
        });

        click.start();
    }

    public void setMidCoordinate(float midCoordinate)
    {
        float yPosition = midCoordinate - getHeight() / 2;
        setY(yPosition);
    }

    public void toggleMenu()
    {
        if(mClosedState)
            openMenu();
        else
            closeMenu();
    }

    public void openMenu()
    {
        if (mIsAnimating)
            return;

        final Animator animation = getOpenMenuAnimation();

        animation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mClosedState = false;
            }
        });
        animation.start();
    }

    public void closeMenu()
    {
        if (mIsAnimating)
            return;

        final Animator animation = getCloseMenuAnimation();

        animation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mClosedState = true;
            }
        });
        animation.start();
    }

    private void offsetAndScaleButtons(float centerX, float centerY, float angleStep, float offset, float scale) {

        for (int i = 0, cnt = mButtons.size(); i < cnt; i++) {
            final float angle = angleStep * i - 90;
            final float x = (float) Math.cos(Math.toRadians(angle)) * offset;
            final float y = (float) Math.sin(Math.toRadians(angle)) * offset;

            final View button = mButtons.get(i);
            button.setX(centerX + x);
            button.setY(centerY + y);
            button.setScaleX(1.0f * scale);
            button.setScaleY(1.0f * scale);
        }
    }

    private Animator getButtonClickAnimation(final @NonNull FloatingActionButton button) {

        final int buttonNumber = mButtons.indexOf(button) + 1;

        final float stepAngle = 360f / mButtons.size();
        final float rOStartAngle = (270 - stepAngle + stepAngle * buttonNumber);
        final float rStartAngle = rOStartAngle > 360 ? rOStartAngle % 360 : rOStartAngle;
        final float x = (float) Math.cos(Math.toRadians(rStartAngle)) * mDistance;
        final float y = (float) Math.sin(Math.toRadians(rStartAngle)) * mDistance;
        final float pivotX = button.getPivotX();
        final float pivotY = button.getPivotY();

        button.setPivotX(pivotX - x);
        button.setPivotY(pivotY - y);

        final ObjectAnimator rotateButton = ObjectAnimator.ofFloat(button, "rotation", 0f, 360f);
        rotateButton.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                button.setPivotX(pivotX);
                button.setPivotY(pivotY);
            }
        });

        final float elevation = mMenuButton.getCompatElevation();

        mRingView.setVisibility(View.INVISIBLE);

        final ObjectAnimator ring = ObjectAnimator.ofFloat(mRingView, "angle", 360);
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(mRingView, "scaleX", 1f, DEFAULT_RING_SCALE_RATIO);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(mRingView, "scaleY", 1f, DEFAULT_RING_SCALE_RATIO);
        final ObjectAnimator visible = ObjectAnimator.ofFloat(mRingView, "alpha", 1f, 0f);

        final AnimatorSet lastSet = new AnimatorSet();

        lastSet.playTogether(scaleX, scaleY, visible, getCloseMenuAnimation());

        final AnimatorSet firstSet = new AnimatorSet();

        firstSet.playTogether(rotateButton, ring);

        final AnimatorSet result = new AnimatorSet();

        result.play(firstSet).before(lastSet);

        result.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mIsAnimating = true;

                button.setCompatElevation(elevation + 1);
                ViewCompat.setZ(mRingView, elevation + 1);

                for (View b : mButtons) {
                    if (b != button) {
                        ((FloatingActionButton) b).setCompatElevation(0);
                    }
                }

                mRingView.setScaleX(1f);
                mRingView.setScaleY(1f);
                mRingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mIsAnimating = false;

                    for (View b : mButtons)
                    {
                        ((FloatingActionButton) b).setCompatElevation(elevation);
                    }
                    ViewCompat.setZ(mRingView, elevation);
            }
        });
        return result;
    }

    private Animator getOpenMenuAnimation()
    {
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(mMenuButton, "scaleX", DEFAULT_OPEN_SCALE);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(mMenuButton, "scaleY", DEFAULT_OPEN_SCALE);

        final ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mMenuButton, "alpha", DEFAULT_CLOSE_ICON_ALPHA);

        final Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
        final Keyframe kf1 = Keyframe.ofFloat(0.5f, 60f);
        final Keyframe kf2 = Keyframe.ofFloat(1f, 0f);

        final PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1, kf2);
        final ObjectAnimator rotateAnimation = ObjectAnimator.ofPropertyValuesHolder(mMenuButton, pvhRotation);

        rotateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            private boolean iconChanged = false;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                final float fraction = valueAnimator.getAnimatedFraction();

                if (fraction >= 0.5f && !iconChanged)
                {
                    iconChanged = true;
                }
            }
        });

        final float centerX = mMenuButton.getX();
        final float centerY = mMenuButton.getY();

        final int buttonsCount = mButtons.size();

        final float angleStep = 360f / buttonsCount;

        final ValueAnimator buttonsAppear = ValueAnimator.ofFloat(0f, mDistance);

        buttonsAppear.setInterpolator(new OvershootInterpolator());

        buttonsAppear.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                for (View view : mButtons) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonsAppear.addUpdateListener((ValueAnimator valueAnimator) ->
        {
                final float fraction = valueAnimator.getAnimatedFraction();
                final float value = (float) valueAnimator.getAnimatedValue();
                offsetAndScaleButtons(centerX, centerY, angleStep, value, fraction);
        });

        final AnimatorSet result = new AnimatorSet();

        result.playTogether(scaleX, scaleY, alphaAnimation, rotateAnimation, buttonsAppear);

        result.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
            }
        });

        result.setDuration(mDurationOpen);
        return result;
    }

    private Animator getCloseMenuAnimation()
    {
        final ObjectAnimator angle = ObjectAnimator.ofFloat(mMenuButton, "rotation", 0);
        final ObjectAnimator alpha2 = ObjectAnimator.ofFloat(mMenuButton, "alpha", DEFAULT_CLOSE_ICON_ALPHA);
        final ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(mMenuButton, "scaleX", DEFAULT_CLOSE_SCALE);
        final ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(mMenuButton, "scaleY", DEFAULT_CLOSE_SCALE);

        final AnimatorSet result = new AnimatorSet();
        result.setInterpolator(new OvershootInterpolator());
        result.playTogether(angle, alpha2, scaleX2, scaleY2);

        result.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
                for (View view : mButtons) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
               mIsAnimating = false;
                mMenuButton.setRotation(60f);
            }
        });
        result.setDuration(mDurationClose);
        return result;
    }

    private void openOrClose(boolean open, boolean animate) {
        if (mIsAnimating)
            return;

        if (open && !mClosedState) {
            return;
        }

        if (!open && mClosedState) {
            return;
        }

        if (animate) {
            mMenuButton.performClick();
        }
        else {
            mClosedState = !open;

            final float centerX = mMenuButton.getX();
            final float centerY = mMenuButton.getY();

            final int buttonsCount = mButtons.size();

            final float angleStep = 360f / buttonsCount;
            final float offset = open ? mDistance : 0f;
            final float scale = open ? 1f : 0f;

            mMenuButton.setAlpha(open ? DEFAULT_CLOSE_ICON_ALPHA : 1f);

            final int visibility = open ? View.VISIBLE : View.INVISIBLE;

            for (View view : mButtons) {
                view.setVisibility(visibility);
            }

            offsetAndScaleButtons(centerX, centerY, angleStep, offset, scale);
        }
    }

    /**
     * Open menu programmatically
     * @param animate open with animation or not
     */
    public void open(boolean animate) {
        openOrClose(true, animate);
    }

    /**
     * Close menu programmatically
     * @param animate close with animation or not
     */
    public void close(boolean animate) {
        openOrClose(false, animate);
    }

    //     CircleMenu event listener.
    public interface Callback
    {
        /* Invoked on button click, after animation end.
         * @param view        - current CircleMenuView instance.
         * @param buttonIndex - clicked button zero-based index.
         */
        void onMenuButtonClick(int buttonIndex);
    }
 }