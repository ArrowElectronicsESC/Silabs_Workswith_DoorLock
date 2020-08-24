package com.siliconlabs.eic.doorlock.models;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.siliconlabs.eic.doorlock.R;

/**
 * Custom Swipe button UI
 *
 * @author leandroferreira on 07/03/17.
 * https://github.com/ebanx/swipe-button/
 */
@SuppressWarnings("FieldCanBeLocal")
public class DoorLockSwipeButton extends RelativeLayout {
    private static final int ENABLED = 0;
    private static final int DISABLED = 1;
    private float initialX;
    private boolean active;
    private int initialButtonWidth;
    private TextView centerText;
    private Drawable disabledDrawable;
    private Drawable enabledDrawable;
    private OnStateChangeListener mListener;
    private ImageView swipeButtonInner;

    private ViewGroup background;


    private int collapsedWidth;
    private int collapsedHeight;

    private LinearLayout layer;
    private boolean trailEnabled = false;
    private boolean hasActivationState;

    private float buttonLeftPadding;
    private float buttonTopPadding;
    private float buttonRightPadding;
    private float buttonBottomPadding;


    public DoorLockSwipeButton(Context context) {
        super(context);

        init(context, null, -1, -1);
    }

    public DoorLockSwipeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, -1, -1);
    }

    public DoorLockSwipeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, -1);
    }


    public DoorLockSwipeButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    static boolean isTouchOutsideInitialPosition(MotionEvent event, View view) {
        return event.getX() > view.getX() + view.getWidth();
    }

    static float convertPixelsToSp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        hasActivationState = true;

        background = new RelativeLayout(context);

        LayoutParams layoutParamsView = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(background, layoutParamsView);

        final TextView centerText = new TextView(context);
        this.centerText = centerText;
        centerText.setGravity(Gravity.CENTER);

        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        background.addView(centerText, layoutParams);

        this.swipeButtonInner = new ImageView(context);

        if (attrs != null && defStyleAttr == -1 && defStyleRes == -1) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DoorLockSwipeButton);//,
            //defStyleAttr, defStyleRes);

            collapsedWidth = (int) typedArray.getDimension(R.styleable.DoorLockSwipeButton_button_image_width,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            collapsedHeight = (int) typedArray.getDimension(R.styleable.DoorLockSwipeButton_button_image_height,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            trailEnabled = typedArray.getBoolean(R.styleable.DoorLockSwipeButton_button_trail_enabled, false);
            Drawable trailingDrawable = typedArray.getDrawable(R.styleable.DoorLockSwipeButton_button_trail_drawable);

            Drawable backgroundDrawable = typedArray.getDrawable(R.styleable.DoorLockSwipeButton_inner_text_background);

            if (backgroundDrawable != null) {
                background.setBackground(backgroundDrawable);
            } else {
                background.setBackground(ContextCompat.getDrawable(context, R.drawable.swipe_button_background));
            }
            background.setBackground(ContextCompat.getDrawable(context, R.drawable.swipe_button_background));

            if (trailEnabled) {
                layer = new LinearLayout(context);

                if (trailingDrawable != null) {
                    layer.setBackground(trailingDrawable);
                } else {
                    layer.setBackground(typedArray.getDrawable(R.styleable.DoorLockSwipeButton_button_background));
                }

                layer.setGravity(Gravity.START);
                layer.setVisibility(View.GONE);
                background.addView(layer, layoutParamsView);
            }

            centerText.setText(typedArray.getText(R.styleable.DoorLockSwipeButton_inner_text));
            centerText.setTextColor(typedArray.getColor(R.styleable.DoorLockSwipeButton_inner_text_color,
                    Color.WHITE));

            float textSize = convertPixelsToSp(
                    typedArray.getDimension(R.styleable.DoorLockSwipeButton_inner_text_size, 36), context);

            if (textSize != 0) {
                centerText.setTextSize(textSize);
            } else {
                centerText.setTextSize(14);
            }

            disabledDrawable = typedArray.getDrawable(R.styleable.DoorLockSwipeButton_button_image_disabled);
            enabledDrawable = typedArray.getDrawable(R.styleable.DoorLockSwipeButton_button_image_enabled);

            float innerTextLeftPadding = typedArray.getDimension(
                    R.styleable.DoorLockSwipeButton_inner_text_left_padding, 0);
            float innerTextTopPadding = typedArray.getDimension(
                    R.styleable.DoorLockSwipeButton_inner_text_top_padding, 0);
            float innerTextRightPadding = typedArray.getDimension(
                    R.styleable.DoorLockSwipeButton_inner_text_right_padding, 0);
            float innerTextBottomPadding = typedArray.getDimension(
                    R.styleable.DoorLockSwipeButton_inner_text_bottom_padding, 0);

            int initialState = typedArray.getInt(R.styleable.DoorLockSwipeButton_initial_state, DISABLED);

            if (initialState == ENABLED) {
                LayoutParams layoutParamsButton = new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                swipeButtonInner.setImageDrawable(enabledDrawable);

                addView(swipeButtonInner, layoutParamsButton);

                active = true;
            } else {
                LayoutParams layoutParamsButton = new LayoutParams(collapsedWidth, collapsedHeight);

                layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                swipeButtonInner.setImageDrawable(disabledDrawable);

                addView(swipeButtonInner, layoutParamsButton);

                active = false;
            }

            centerText.setPadding((int) innerTextLeftPadding,
                    (int) innerTextTopPadding,
                    (int) innerTextRightPadding,
                    (int) innerTextBottomPadding);

            Drawable buttonBackground = typedArray.getDrawable(R.styleable.DoorLockSwipeButton_button_background);

            if (buttonBackground != null) {
                swipeButtonInner.setBackground(buttonBackground);
            } else {
                swipeButtonInner.setBackground(ContextCompat.getDrawable(context, R.drawable.swipe_button));
            }

            buttonLeftPadding = typedArray.getDimension(R.styleable.DoorLockSwipeButton_button_left_padding, 0);
            buttonTopPadding = typedArray.getDimension(R.styleable.DoorLockSwipeButton_button_top_padding, 0);
            buttonRightPadding = typedArray.getDimension(R.styleable.DoorLockSwipeButton_button_right_padding, 0);
            buttonBottomPadding = typedArray.getDimension(R.styleable.DoorLockSwipeButton_button_bottom_padding, 0);

            swipeButtonInner.setPadding((int) buttonLeftPadding,
                    (int) buttonTopPadding,
                    (int) buttonRightPadding,
                    (int) buttonBottomPadding);

            hasActivationState = typedArray.getBoolean(R.styleable.DoorLockSwipeButton_has_activate_state, true);

            typedArray.recycle();
        }

        setOnTouchListener(getButtonTouchListener());

    }

    private void expandButton() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(swipeButtonInner.getX(), 0);
        positionAnimator.addUpdateListener(animation -> {
            float x = (Float) positionAnimator.getAnimatedValue();
            swipeButtonInner.setX(x);
        });


        final ValueAnimator widthAnimator = ValueAnimator.ofInt(
                swipeButtonInner.getWidth(),
                getWidth());

        widthAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams params = swipeButtonInner.getLayoutParams();
            params.width = (Integer) widthAnimator.getAnimatedValue();
            swipeButtonInner.setLayoutParams(params);
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                active = true;
                swipeButtonInner.setImageDrawable(enabledDrawable);

                if (mListener != null) {
                    mListener.onStateChange(active);
                }

                if (mListener != null) {
                    mListener.onActive();
                }
            }
        });

        animatorSet.playTogether(positionAnimator, widthAnimator);
        animatorSet.start();
    }

    private void moveButtonBack() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(swipeButtonInner.getX(), 0);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(animation -> {
            float x = (Float) positionAnimator.getAnimatedValue();
            swipeButtonInner.setX(x);
            setTrailingEffect();
        });

        positionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (layer != null) {
                    layer.setVisibility(View.GONE);
                }
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                centerText, "alpha", 1);

        positionAnimator.setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, positionAnimator);
        animatorSet.start();
    }

    private void collapseButton() {
        final ValueAnimator widthAnimator = ValueAnimator.ofInt(swipeButtonInner.getWidth(), initialButtonWidth);

        widthAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams params = swipeButtonInner.getLayoutParams();
            params.width = (Integer) widthAnimator.getAnimatedValue();
            swipeButtonInner.setLayoutParams(params);
            setTrailingEffect();
        });

        widthAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                active = false;
                swipeButtonInner.setImageDrawable(disabledDrawable);
                swipeButtonInner.setPadding((int) buttonLeftPadding, (int) buttonTopPadding, (int) buttonRightPadding, (int) buttonBottomPadding);
                if (mListener != null) {
                    mListener.onStateChange(active);
                }
                if (layer != null) {
                    layer.setVisibility(View.GONE);
                }
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                centerText, "alpha", 1);

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(objectAnimator, widthAnimator);
        animatorSet.start();
    }

    public void setEnabledStateNotAnimated() {
        swipeButtonInner.setX(0);

        ViewGroup.LayoutParams params = swipeButtonInner.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        swipeButtonInner.setLayoutParams(params);

        swipeButtonInner.setImageDrawable(enabledDrawable);
        active = true;
        centerText.setAlpha(0);
    }

    public void setDisabledStateNotAnimated() {
        ViewGroup.LayoutParams params = swipeButtonInner.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        collapsedWidth = ViewGroup.LayoutParams.WRAP_CONTENT;

        swipeButtonInner.setImageDrawable(disabledDrawable);
        active = false;
        centerText.setAlpha(1);

        swipeButtonInner.setPadding((int) buttonLeftPadding,
                (int) buttonTopPadding,
                (int) buttonRightPadding,
                (int) buttonBottomPadding);

        swipeButtonInner.setLayoutParams(params);
    }

    private void setTrailingEffect() {
        if (trailEnabled) {
            layer.setVisibility(View.VISIBLE);
            layer.setLayoutParams(new RelativeLayout.LayoutParams(
                    (int) (swipeButtonInner.getX() + swipeButtonInner.getWidth() / 3), centerText.getHeight()));
        }
    }

    public void setTrailBackground(@NonNull Drawable trailingDrawable) {
        if (trailEnabled) {
            layer.setBackground(trailingDrawable);
        }
    }

    public void toggleState() {
        if (isActive()) {
            collapseButton();
        } else {
            expandButton();
        }
    }

    public void setCenterTextColor(Context context, int color) {
        centerText.setTextColor(context.getResources().getColor(color, null));
    }

    private OnTouchListener getButtonTouchListener() {
        return (v, event) -> {
            v.performClick();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return !isTouchOutsideInitialPosition(event, swipeButtonInner);

                case MotionEvent.ACTION_MOVE:

                    //Movement logic here
                    if (initialX == 0) {
                        initialX = swipeButtonInner.getX();
                    }

                    if (event.getX() > initialX + (float) swipeButtonInner.getWidth() / 2 &&
                            event.getX() + (float) swipeButtonInner.getWidth() / 2 < getWidth()) {
                        swipeButtonInner.setX(event.getX() - (float) swipeButtonInner.getWidth() / 2);
                        centerText.setAlpha(1 - 1.3f * (swipeButtonInner.getX() + swipeButtonInner.getWidth()) / getWidth());
                        setTrailingEffect();
                    }

                    if (event.getX() + (float) swipeButtonInner.getWidth() / 2 > getWidth() &&
                            swipeButtonInner.getX() + (float) swipeButtonInner.getWidth() / 2 < getWidth()) {
                        swipeButtonInner.setX(getWidth() - swipeButtonInner.getWidth());
                    }

                    if (event.getX() < (float) swipeButtonInner.getWidth() / 2 &&
                            swipeButtonInner.getX() > 0) {
                        swipeButtonInner.setX(0);
                    }

                    return true;
                case MotionEvent.ACTION_UP:
                    //Release logic here
                    if (active) {
                        collapseButton();
                    } else {
                        initialButtonWidth = swipeButtonInner.getWidth();
                        if (swipeButtonInner.getX() + swipeButtonInner.getWidth() > getWidth() * 0.85) {
                            if (hasActivationState) {
                                expandButton();
                            } else if (mListener != null) {
                                mListener.onActive();
                                moveButtonBack();
                            }
                        } else {
                            moveButtonBack();
                        }
                    }
                    return true;
                default:
                    break;
            }

            return false;
        };
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.mListener = listener;
    }

    public boolean isActive() {
        return active;
    }

    public void setText(String text) {
        centerText.setText(text);
    }

    public void setBackground(Drawable drawable) {
        background.setBackground(drawable);
    }

    public void setSlidingButtonBackground(Drawable drawable) {
        background.setBackground(drawable);
    }

    public void setDisabledDrawable(Drawable drawable) {
        disabledDrawable = drawable;

        if (!active) {
            swipeButtonInner.setImageDrawable(drawable);
        }
    }

    public void setButtonBackground(Drawable buttonBackground) {
        if (buttonBackground != null) {
            swipeButtonInner.setBackground(buttonBackground);
        }
    }

    public void setEnabledDrawable(Drawable drawable) {
        enabledDrawable = drawable;
        if (active) {
            swipeButtonInner.setImageDrawable(drawable);
        }
    }

    public void setInnerTextPadding(int left, int top, int right, int bottom) {
        centerText.setPadding(left, top, right, bottom);
    }

    public void setSwipeButtonPadding(int left, int top, int right, int bottom) {
        buttonLeftPadding = left;
        buttonBottomPadding = bottom;
        buttonRightPadding = right;
        buttonTopPadding = top;
        swipeButtonInner.setPadding(left, top, right, bottom);
    }

    public void setHasActivationState(boolean hasActivationState) {
        this.hasActivationState = hasActivationState;
    }

    public interface OnStateChangeListener {
        void onStateChange(boolean active);

        void onActive();
    }
}