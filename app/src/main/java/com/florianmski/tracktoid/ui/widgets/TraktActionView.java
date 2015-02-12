package com.florianmski.tracktoid.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import com.florianmski.tracktoid.R;

public class TraktActionView extends Button
{
    private boolean checked = false;
    private boolean working = false;

    private int colorNotChecked;
    private int colorChecked;
    private int colorPressed;

    private WorkingDrawable workingDrawable;

    private OnTAVClickListener userListener;
    private OnClickListener internalListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            toggle();
        }
    };

    public TraktActionView(Context context)
    {
        super(context);
        init(context, null);
    }

    public TraktActionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        colorNotChecked = getColor(R.color.gray);

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.TraktActionView, 0, 0);
        if (attr == null)
            return;

        try
        {
            colorChecked = attr.getColor(R.styleable.TraktActionView_colorChecked, Color.BLACK);
            colorPressed = attr.getColor(R.styleable.TraktActionView_colorPressed, Color.RED);
        }
        finally
        {
            attr.recycle();
        }

        setBackgroundDrawable(createDefaultDrawable());
        workingDrawable = new WorkingDrawable(colorNotChecked, colorChecked);

        setOnClickListener(internalListener);
    }

    private StateListDrawable createDefaultDrawable()
    {
        StateListDrawable drawable = new StateListDrawable();
        Drawable colorNotCheckedDrawable = new ColorDrawable(colorNotChecked);
        Drawable colorCheckedDrawable = new ColorDrawable(colorChecked);
        Drawable colorPressedDrawable = new ColorDrawable(colorPressed);

        drawable.addState(new int[]{android.R.attr.state_pressed}, colorPressedDrawable);
        drawable.addState(new int[]{android.R.attr.state_focused}, colorPressedDrawable);
        drawable.addState(new int[]{android.R.attr.state_selected}, colorPressedDrawable);
        drawable.addState(new int[]{}, checked ? colorCheckedDrawable : colorNotCheckedDrawable);
        return drawable;
    }

    private int getColor(int id)
    {
        return getResources().getColor(id);
    }

    @Override
    public void setOnClickListener(OnClickListener l)
    {
        super.setOnClickListener(l);
    }

    public void setOnTAVClickListener(OnTAVClickListener listener)
    {
        this.userListener = listener;
    }

    public void setChecked(boolean checked)
    {
        if(this.checked == checked)
            return;

        this.checked = checked;
        setBackgroundDrawable(createDefaultDrawable());
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void success()
    {
        workingDrawable.stopAnimation(true);
    }

    public void error()
    {
        workingDrawable.stopAnimation(false);
    }

    public void toggle()
    {
        // not clickable if working
        if(!working)
        {
            goToWork();

            if(userListener != null)
                userListener.onClick(isChecked());
        }
    }

    private void goToWork()
    {
        working = true;
        if(checked)
        {
            setBackgroundDrawable(workingDrawable);
            workingDrawable.startAnimation(true);
        }
        else
        {
            setBackgroundDrawable(workingDrawable);
            workingDrawable.startAnimation(false);
        }
    }

    public interface OnTAVClickListener
    {
        public void onClick(boolean checked);
    }

    public class WorkingDrawable extends Drawable
    {
        private int startColor;
        private int currentColor;
        private int endColor;

        private ValueAnimator colorAnimation;

        public WorkingDrawable(int startColor, int endColor)
        {
            this.startColor = startColor;
            this.endColor = endColor;
            this.currentColor = startColor;
        }

        @Override
        public void draw(Canvas canvas)
        {
            canvas.drawColor(currentColor);
        }

        @Override
        public void setAlpha(int i) {}

        @Override
        public void setColorFilter(ColorFilter colorFilter) {}

        @Override
        public int getOpacity()
        {
            return 0;
        }

        public void startAnimation(boolean reverse)
        {
            if(reverse)
                colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), endColor, startColor, endColor);
            else
                colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor, startColor);

            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animator)
                {
                    currentColor = (Integer) animator.getAnimatedValue();
                    invalidateSelf();
                }
            });

            colorAnimation.setDuration(1500);
            colorAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
            colorAnimation.start();
        }

        public void stopAnimation(final boolean success)
        {
            if (success)
                checked = !checked;

            colorAnimation.removeAllListeners();
            colorAnimation.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    setBackgroundDrawable(createDefaultDrawable());
                    working = false;
                }
            });
            colorAnimation.setRepeatCount(0);
        }
    }
}
