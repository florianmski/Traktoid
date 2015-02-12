package com.florianmski.tracktoid.ui.fragments.base.pager;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.ui.fragments.BaseFragment;

public abstract class PagerFragment extends BaseFragment implements ViewPager.OnPageChangeListener
{
    protected ViewPager vp;
    protected PagerSlidingTabStrip tabs;
    protected String id;
    protected int position;

    public static Bundle getBundle(String id, int position)
    {
        Bundle args = new Bundle();
        args.putString(TraktoidConstants.BUNDLE_ID, id);
        args.putInt(TraktoidConstants.BUNDLE_POSITION, position);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() != null)
        {
            id = getArguments().getString(TraktoidConstants.BUNDLE_ID);
            position = getArguments().getInt(TraktoidConstants.BUNDLE_POSITION, 0);
        }
    }

    protected abstract PagerAdapter getPagerAdapter();

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        tabs.setOnPageChangeListener(this);
        vp.setAdapter(getPagerAdapter());
        tabs.setViewPager(vp);
        vp.setCurrentItem(position);

        // if position == 0, onPageSelected will not be called the first time
        if(position == 0)
            onPageSelected(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        vp = (ViewPager)view.findViewById(R.id.viewPager);
        tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
    }

    @Override
    public void onPageSelected(int position)
    {
        this.position = position;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageScrollStateChanged(int i) {}
}
