package com.florianmski.tracktoid.ui.activities;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.calendar.PagerCalendarFragment;

public class CalendarActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		return PagerCalendarFragment.newInstance(getIntent().getExtras());
	}
}
