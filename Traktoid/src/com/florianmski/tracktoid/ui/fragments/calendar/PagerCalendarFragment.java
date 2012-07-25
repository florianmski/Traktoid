package com.florianmski.tracktoid.ui.fragments.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask;
import com.florianmski.tracktoid.ui.fragments.PagerTabsFragment;

public class PagerCalendarFragment extends PagerTabsFragment
{	
	public static PagerCalendarFragment newInstance(Bundle args)
	{
		PagerCalendarFragment f = new PagerCalendarFragment();
		f.setArguments(args);
		return f;
	}

	public PagerCalendarFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{		
		addTabs();

		super.onActivityCreated(savedInstanceState);
	}

	public void addTabs() 
	{
		Bundle args = new Bundle();

		args.putInt(TraktoidConstants.BUNDLE_CALENDAR, CalendarTask.PREMIERES);
		mTabsAdapter.addTab("Premieres", PI_CalendarFragment.class, args);
		args = new Bundle();
		args.putInt(TraktoidConstants.BUNDLE_CALENDAR, CalendarTask.USER);
		mTabsAdapter.addTab("My shows", PI_CalendarFragment.class, args);
		args = new Bundle();
		args.putInt(TraktoidConstants.BUNDLE_CALENDAR, CalendarTask.SHOWS);
		mTabsAdapter.addTab("Shows", PI_CalendarFragment.class, args);

		getActionBar().setSelectedNavigationItem(1);
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.fragment_calendar, null);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
