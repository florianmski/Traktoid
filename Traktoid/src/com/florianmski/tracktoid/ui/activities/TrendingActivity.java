package com.florianmski.tracktoid.ui.activities;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.trending.PagerTrendingFragment;

public class TrendingActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return PagerTrendingFragment.newInstance(getIntent().getExtras());
	}
}
