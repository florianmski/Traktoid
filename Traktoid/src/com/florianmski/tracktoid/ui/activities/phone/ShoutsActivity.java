package com.florianmski.tracktoid.ui.activities.phone;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.ShoutsFragment;

public class ShoutsActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return ShoutsFragment.newInstance(getIntent().getExtras());
	}
}
