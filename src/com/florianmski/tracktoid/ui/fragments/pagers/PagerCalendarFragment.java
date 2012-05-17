package com.florianmski.tracktoid.ui.fragments.pagers;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask;
import com.florianmski.tracktoid.ui.fragments.pagers.items.PI_CalendarFragment;
import com.jakewharton.trakt.entities.CalendarDate;

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
		getStatusView().show().text("Retrieving calendar,\nPlease wait...");

		commonTask = new CalendarTask(tm, this);
		
		if(savedInstanceState == null)
			commonTask.fire();
		else
			addTabs(null);
		
		super.onActivityCreated(savedInstanceState);
	}
	
	public void addTabs(ArrayList<ArrayList<CalendarDate>> calendars) 
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
			
			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, calendars == null ? null : calendars.get(0));
			mTabsAdapter.addTab("Premieres", PI_CalendarFragment.class, args);
			args = new Bundle();
			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, calendars == null ? null : calendars.get(1));
			mTabsAdapter.addTab("My shows", PI_CalendarFragment.class, args);
			args = new Bundle();
			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, calendars == null ? null : calendars.get(2));
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
	public void onCalendar(ArrayList<ArrayList<CalendarDate>> calendars) 
	{
		addTabs(calendars);
	}
	
	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
