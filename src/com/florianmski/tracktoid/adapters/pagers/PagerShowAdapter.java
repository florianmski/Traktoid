package com.florianmski.tracktoid.adapters.pagers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.florianmski.tracktoid.ui.fragments.show.ProgressFragment;
import com.florianmski.tracktoid.ui.fragments.traktitems.PI_TraktItemShowFragment;

public class PagerShowAdapter extends FragmentPagerAdapter
{
	private final static String titles[] = {"Progress", "About"};
	
	private Bundle args;
	
	public PagerShowAdapter(FragmentManager fm, Bundle args) 
	{
		super(fm);
		
		this.args = args;
	}

	@Override
    public CharSequence getPageTitle(int position)
	{
		return titles[position];
	}

	@Override
	public Fragment getItem(int position) 
	{
		if(position == 0)
			return ProgressFragment.newInstance(args);
		else
			return PI_TraktItemShowFragment.newInstance(args);
	}

	@Override
	public int getCount() 
	{
		return titles.length;
	}
}
