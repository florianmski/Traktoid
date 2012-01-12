package com.florianmski.tracktoid.ui.fragments.pagers;

import android.os.Bundle;

import com.florianmski.tracktoid.adapters.pagers.PagerLoginAdapter;
import com.florianmski.tracktoid.ui.fragments.ShoutsFragment;

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
		setPageIndicatorType(PagerFragment.IT_TITLE);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getSupportActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		initPagerFragment(new PagerLoginAdapter(getSupportFragmentManager()));
	}
}
