package com.florianmski.tracktoid.ui.fragments.login;

import android.os.Bundle;

import com.florianmski.tracktoid.adapters.pagers.PagerLoginAdapter;
import com.florianmski.tracktoid.ui.fragments.PagerFragment;

public class PagerLoginFragment extends PagerFragment
{
	public static PagerLoginFragment newInstance(Bundle args)
	{
		PagerLoginFragment f = new PagerLoginFragment();
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
