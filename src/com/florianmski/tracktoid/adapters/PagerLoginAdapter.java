package com.florianmski.tracktoid.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.florianmski.tracktoid.ui.fragments.JoinFragment;
import com.florianmski.tracktoid.ui.fragments.SignInFragment;
import com.viewpagerindicator.TitleProvider;

public class PagerLoginAdapter extends PagerFragmentAdapter implements TitleProvider
{
	private final static String titles[] = {"Sign In", "Join Trakt!"};
	
	public PagerLoginAdapter(FragmentManager fm, Fragment... fragments) 
	{
		super(fm, fragments);
	}
	
	public PagerLoginAdapter(FragmentManager fm) 
	{
		this(fm, new SignInFragment(), new JoinFragment());
	}

	@Override
	public String getTitle(int position) 
	{
		return titles[position];
	}

}
