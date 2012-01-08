package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;

import com.florianmski.tracktoid.adapters.pagers.PagerLoginAdapter;

public class LoginFragment extends PagerFragment
{
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
