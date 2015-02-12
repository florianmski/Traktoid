package com.florianmski.tracktoid.ui.fragments.base.pager;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.utils.ScrollHeaderHelper;

public abstract class PagerHeaderFragment extends PagerFragment implements ScrollHeaderHelper.OnHeaderListener, ScrollHeaderHelper.Provider
{
    protected FrameLayout flHeader;
    protected ScrollHeaderHelper shh;

    @Override
    public void onResume()
    {
        super.onResume();

        getScrollHeaderHelper().addHeaderListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        getScrollHeaderHelper().removeHeaderListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_pager_header, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        vp = (ViewPager)view.findViewById(R.id.viewPager);
        tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
        flHeader = (FrameLayout)view.findViewById(R.id.frameLayoutHeader);

        shh = new ScrollHeaderHelper(flHeader);
    }

    public ScrollHeaderHelper getScrollHeaderHelper()
    {
        return shh;
    }

    @Override
    public void onHeaderLayout()
    {
        int height = getActionBar().getHeight() + tabs.getMeasuredHeight();
        // TODO I think there is a better way...
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            height += getResources().getDimensionPixelSize(resourceId);
        }
        shh.setHeightToKeep(height);
    }

    @Override
    public void onHeaderTranslate(int key, float translationY) {}

    @Override
    public void onPageSelected(int position)
    {
        super.onPageSelected(position);
        getScrollHeaderHelper().setActiveScrollView(position);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageScrollStateChanged(int i) {}
}
