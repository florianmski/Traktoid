package com.florianmski.tracktoid.adapters.pagers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.ui.fragments.login.LoginFragment;

public class PagerLoginAdapter extends FragmentPagerAdapter
{
	private final static String titles[] = {"Sign In", "Join Trakt!"};
	
	public PagerLoginAdapter(FragmentManager fm) 
	{
		super(fm);
	}

	@Override
    public CharSequence getPageTitle(int position)
	{
		return titles[position];
	}

	@Override
	public Fragment getItem(int position) 
	{
		Bundle b = new Bundle();
		b.putBoolean(TraktoidConstants.BUNDLE_JOIN, position != 0);
		return LoginFragment.newInstance(b);
	}

	@Override
	public int getCount() 
	{
		return titles.length;
	}
}
