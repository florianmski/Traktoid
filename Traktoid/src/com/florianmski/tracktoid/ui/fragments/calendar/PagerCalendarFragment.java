package com.florianmski.tracktoid.ui.fragments.calendar;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask.CalendarListener;
import com.florianmski.tracktoid.ui.fragments.PagerTabsFragment;
import com.jakewharton.trakt.entities.CalendarDate;

public class PagerCalendarFragment extends PagerTabsFragment implements CalendarListener
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
		
		CalendarTask.addObserver(this);
	}
	
	@Override
	public void onDestroy()
	{
		CalendarTask.removeObserver(this);
		super.onDestroy();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		getStatusView().show().text("Retrieving calendar,\nPlease wait...");
		
		if(savedInstanceState == null)
			new CalendarTask(getActivity()).fire();
		else
			addTabs(null);
		
		super.onActivityCreated(savedInstanceState);
	}
	
	public void addTabs(List<List<CalendarDate>> calendars) 
	{
		Bundle args = new Bundle();

		if(Utils.isOnline(getActivity()))
		{
//			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, calendars == null ? null : calendars.get(0));
//			mTabsAdapter.addTab(mTabHost.newTabSpec("premieres").setIndicator("Premieres"), CalendarFragment.class, args);
//			args = new Bundle();
//			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, calendars == null ? null : calendars.get(1));
//			mTabsAdapter.addTab(mTabHost.newTabSpec("my_shows").setIndicator("My shows"), CalendarFragment.class, args);
//			args = new Bundle();
//			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, calendars == null ? null : calendars.get(2));
//			mTabsAdapter.addTab(mTabHost.newTabSpec("shows").setIndicator("Shows"), CalendarFragment.class, args);
			
			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, (Serializable) (calendars == null ? null : calendars.get(0)));
			mTabsAdapter.addTab("Premieres", PI_CalendarFragment.class, args);
			args = new Bundle();
			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, (Serializable) (calendars == null ? null : calendars.get(1)));
			mTabsAdapter.addTab("My shows", PI_CalendarFragment.class, args);
			args = new Bundle();
			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, (Serializable) (calendars == null ? null : calendars.get(2)));
			mTabsAdapter.addTab("Shows", PI_CalendarFragment.class, args);
			
			getActionBar().setSelectedNavigationItem(1);
		}
		else
			mTabsAdapter.addTab("My shows", PI_CalendarFragment.class, null);
		
		getStatusView().hide().text(null);
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

	@Override
	public void onCalendar(List<List<CalendarDate>> calendars) 
	{
		addTabs(calendars);
	}
}
