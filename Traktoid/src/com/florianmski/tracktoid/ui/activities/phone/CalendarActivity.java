package com.florianmski.tracktoid.ui.activities.phone;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.ui.fragments.calendar.PI_CalendarFragment;
import com.florianmski.tracktoid.ui.fragments.calendar.PagerCalendarFragment;

public class CalendarActivity extends SinglePaneActivity
{
	@Override
	public Fragment getFragment() 
	{
		if(Utils.isOnline(this))
			return PagerCalendarFragment.newInstance(getIntent().getExtras());
		else
			return new PI_CalendarFragment();
	}
}
