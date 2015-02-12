package com.getbase.floatingactionbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.florianmski.tracktoid.*;
import com.florianmski.tracktoid.R;

public class FloatingActionButton2 extends FloatingActionButton
{
    private boolean visible = true;

    public FloatingActionButton2(Context context)
    {
        super(context);
    }

    public FloatingActionButton2(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FloatingActionButton2(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public FloatingActionButton2 addToLayout(RelativeLayout rl)
    {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rl.addView(this, lp);
        return this;
    }

    public FloatingActionButton2 addToLayout(FrameLayout fl)
    {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        fl.addView(this, 0, lp);
        return this;
    }

    public void reactToScroll(int dy)
    {
        if(dy > 0)
            show(false);
        else if(dy < 0)
            show(true);
    }

    public void show(boolean show)
    {
        show(show, null);
    }

    public void show(boolean show, Animator.AnimatorListener listener)
    {
        if(show == visible)
            return;

        visible = show;
        animate().translationY(show ? 0 : getHeight())
                .alpha(show ? 1 : 0)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(listener);
    }

    public void setTheme(final TraktoidTheme theme)
    {
        this.mColorNormal = theme.getColor(getContext());
        this.mColorPressed = theme.getColorDark(getContext());

        show(false, new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                setImageDrawable(getResources().getDrawable(theme == TraktoidTheme.MOVIE ? R.drawable.ic_movie_white_24dp : R.drawable.ic_tv_white_24dp));
                updateBackground();
                show(true);
            }
        });

        // cool animation but need api >= 19
//        animate().rotationYBy(180)
//                .setInterpolator(new DecelerateInterpolator())
//                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener()
//                {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation)
//                    {
//                        if (animation.getAnimatedFraction() >= 0.5f)
//                        {
//                            updateBackground();
//                            animation.removeUpdateListener(this);
//                        }
//                    }
//                });
    }
}
