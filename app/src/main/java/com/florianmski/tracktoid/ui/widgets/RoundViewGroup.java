package com.florianmski.tracktoid.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class RoundViewGroup extends ViewGroup
{
    public RoundViewGroup(Context context)
    {
        super(context);
    }

    public RoundViewGroup(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoundViewGroup(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop;

        //get the available size of child view
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childBottom = getMeasuredHeight() - getPaddingBottom();
        int childHeight = childBottom - childTop;
        int childWidth = childHeight;

        curLeft = childLeft;
        curTop = childTop;

        //walk through each child, and arrange it from left to right
        for(int i = count-1; i >= 0; i--)
        {
            View child = getChildAt(i);
            if(child.getVisibility() != GONE)
            {
                //Get the maximum size of the child
                child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
                curWidth = child.getMeasuredWidth();
                curHeight = child.getMeasuredHeight();

                //do the layout
                child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
                //store the max height
                curLeft += curWidth / 2;
            }
        }
    }
}
