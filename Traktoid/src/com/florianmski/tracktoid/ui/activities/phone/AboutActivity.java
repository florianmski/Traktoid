package com.florianmski.tracktoid.ui.activities.phone;

import com.florianmski.tracktoid.ui.fragments.AboutFragment;

import android.support.v4.app.Fragment;

public class AboutActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return AboutFragment.newInstance(getIntent().getExtras());
	}	
	
}
