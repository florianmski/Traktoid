package com.florianmski.tracktoid.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.florianmski.tracktoid.BuildConfig;

public class RateView extends LinearLayout
{
    private final static int NB_RATINGS = 10;

    public RateView(Context context)
    {
        this(context, null);
    }

    public RateView(Context context, AttributeSet attrs)
    {
        this(context, null, 0);
    }

    public RateView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        for(int i = 0; i < NB_RATINGS; i++)
        {
            NumberView nv = new NumberView(getContext());
            int nbRes = i == 0 ? 5 : i*10;
            nv.setColor(getResources().getIdentifier(String.format("red_%d0", nbRes), "color", BuildConfig.APPLICATION_ID));
            nv.setColor(Color.RED);
            nv.setNumber(i+1);
            addView(nv);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize / getChildCount());
    }

    //    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b)
//    {
//        int childCount = getChildCount();
//        int curLeft = getPaddingLeft();
//        int childWidth = getMeasuredWidth() / childCount;
//        for(int i = 0; i < childCount; i++)
//        {
//            View child = getChildAt(i);
//            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
//                    MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST));
//
//            int curWidth = child.getMeasuredWidth();
//            int curHeight = child.getMeasuredHeight();
//
//            child.layout(curLeft, getPaddingTop(), curLeft + curWidth, getPaddingTop() + curHeight);
//            curLeft += curWidth;
//        }
//    }
}
