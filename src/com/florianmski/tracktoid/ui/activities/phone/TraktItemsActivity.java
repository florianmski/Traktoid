package com.florianmski.tracktoid.ui.activities.phone;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.traktitems.PagerTraktItemFragment;

public class TraktItemsActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return PagerTraktItemFragment.newInstance(getIntent().getExtras());
	}
}
