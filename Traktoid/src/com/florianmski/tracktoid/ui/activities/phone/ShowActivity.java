package com.florianmski.tracktoid.ui.activities.phone;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.show.PagerShowFragment;

public class ShowActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return PagerShowFragment.newInstance(getIntent().getExtras());
	}
}