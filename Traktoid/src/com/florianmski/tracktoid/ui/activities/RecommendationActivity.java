package com.florianmski.tracktoid.ui.activities;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.recommendations.PagerRecommendationFragment;

public class RecommendationActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return PagerRecommendationFragment.newInstance(getIntent().getExtras());
	}
}
