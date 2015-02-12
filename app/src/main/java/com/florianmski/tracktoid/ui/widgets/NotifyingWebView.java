package com.florianmski.tracktoid.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class NotifyingWebView extends WebView
{
    public interface OnScrollChangedListener
    {
        void onScrollChanged(NotifyingWebView who, int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener listener;
    private boolean mIsOverScrollEnabled = true;

    public NotifyingWebView(Context context)
    {
        super(context);
    }

    public NotifyingWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NotifyingWebView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        if(listener != null)
            listener.onScrollChanged(this, l, t, oldl, oldt);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener)
    {
        this.listener = listener;
    }

    public void setOverScrollEnabled(boolean enabled)
    {
        mIsOverScrollEnabled = enabled;
    }

    public boolean isOverScrollEnabled()
    {
        return mIsOverScrollEnabled;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
    {
        return super.overScrollBy(
                deltaX,
                deltaY,
                scrollX,
                scrollY,
                scrollRangeX,
                scrollRangeY,
                mIsOverScrollEnabled ? maxOverScrollX : 0,
                mIsOverScrollEnabled ? maxOverScrollY : 0,
                isTouchEvent);
    }

}