package com.florianmski.tracktoid.ui.activities.phone;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.activities.phone.SinglePaneActivity;
import com.florianmski.tracktoid.ui.fragments.library.PagerLibraryFragment;

public class LibraryActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return PagerLibraryFragment.newInstance(getIntent().getExtras());
	}
}
