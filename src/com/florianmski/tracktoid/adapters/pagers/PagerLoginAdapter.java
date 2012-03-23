package com.florianmski.tracktoid.adapters.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.florianmski.tracktoid.ui.fragments.JoinFragment;
import com.florianmski.tracktoid.ui.fragments.SignInFragment;
import com.viewpagerindicator.TitleProvider;

public class PagerLoginAdapter extends FragmentPagerAdapter implements TitleProvider
{
	private final static String titles[] = {"Sign In", "Join Trakt!"};
	
	public PagerLoginAdapter(FragmentManager fm) 
	{
		super(fm);
	}

	@Override
	public String getTitle(int position) 
	{
		return titles[position];
	}

	@Override
	public Fragment getItem(int position) 
	{
		if(position == 0)
			return SignInFragment.newInstance(null);
		else
			return JoinFragment.newInstance(null);
	}

	@Override
	public int getCount() 
	{
		return titles.length;
	}
}
