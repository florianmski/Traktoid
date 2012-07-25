package com.florianmski.tracktoid.ui.activities;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.traktitems.PI_TraktItemMovieFragment;

public class MovieActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return PI_TraktItemMovieFragment.newInstance(getIntent().getExtras());
	}
}