package com.florianmski.tracktoid.ui.activities.phone;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.season.PagerSeasonFragment;

public class SeasonActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return PagerSeasonFragment.newInstance(getIntent().getExtras());
	}
}
