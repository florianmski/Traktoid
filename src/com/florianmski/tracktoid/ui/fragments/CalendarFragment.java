package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.PagerCalendarAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask.CalendarListener;

public class CalendarFragment extends PagerFragment
{
	public CalendarFragment() {}
	
	public CalendarFragment(FragmentListener listener) 
	{
		super(listener);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setPageIndicatorType(PagerFragment.IT_TITLE);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		Utils.showLoading(getActivity());

		commonTask = new CalendarTask(tm, this, new CalendarListener() 
		{
			@Override
			public void onCalendar(PagerCalendarAdapter adapter) 
			{
				Utils.removeLoading();
				initPagerFragment(adapter);
			}
		});
		
		commonTask.execute();
	}
}
