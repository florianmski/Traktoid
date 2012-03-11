package com.florianmski.tracktoid.ui.fragments.pagers;

import android.os.Bundle;

import com.florianmski.tracktoid.adapters.pagers.PagerLoginAdapter;

public class LoginPagerFragment extends PagerFragment
{
	public static LoginPagerFragment newInstance(Bundle args)
	{
		LoginPagerFragment f = new LoginPagerFragment();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setPageIndicatorType(PagerFragment.IT_TAB);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		initPagerFragment(new PagerLoginAdapter(getFragmentManager()));
	}
}
