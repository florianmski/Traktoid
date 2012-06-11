package com.florianmski.tracktoid.ui.fragments.show;

import android.os.Bundle;

import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.adapters.pagers.PagerShowAdapter;
import com.florianmski.tracktoid.ui.fragments.PagerFragment;

public class PagerShowFragment extends PagerFragment
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setPageIndicatorType(PagerFragment.IT_TITLE);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		initPagerFragment(new PagerShowAdapter(getFragmentManager(), getArguments()));
	}
	
	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);
		ListCheckerManager.finish();
	}
}
