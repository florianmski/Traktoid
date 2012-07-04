package com.florianmski.tracktoid.ui.activities.phone;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.SearchFragment;

public class SearchActivity extends SinglePaneActivity
{			
	@Override
	public Fragment getFragment() 
	{
		return SearchFragment.newInstance(getIntent().getExtras());
	}
}
