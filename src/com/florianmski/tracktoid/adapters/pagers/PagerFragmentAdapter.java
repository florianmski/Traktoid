package com.florianmski.tracktoid.adapters.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerFragmentAdapter extends FragmentPagerAdapter
{
	private Fragment[] fragments;
	
	public PagerFragmentAdapter(FragmentManager fm, Fragment... fragments) 
	{
		super(fm);
		this.fragments = fragments;
	}
	
	@Override
    public int getCount() 
	{
        return fragments.length;
    }

    @Override
    public Fragment getItem(int position) 
    {
        return fragments[position];
    }


}
