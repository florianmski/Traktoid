package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.PagerCalendarAdapter;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.CalendarTask;
import com.florianmski.tracktoid.trakt.tasks.CalendarTask.CalendarListener;
import com.florianmski.tracktoid.ui.fragments.TraktFragment.FragmentListener;

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

		new CalendarTask(tm, this, new CalendarListener() 
		{
			@Override
			public void onCalendar(PagerCalendarAdapter adapter) 
			{
				Utils.removeLoading();
				initPagerFragment(adapter);
			}
		}).execute();
	}
}
