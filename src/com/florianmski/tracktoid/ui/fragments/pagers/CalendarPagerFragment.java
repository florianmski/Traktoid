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
import com.florianmski.tracktoid.ui.fragments.pagers.items.CalendarFragment;
import com.jakewharton.trakt.entities.CalendarDate;

public class CalendarPagerFragment extends TabsPagerFragment
{	
	public static CalendarPagerFragment newInstance(Bundle args)
	{
		CalendarPagerFragment f = new CalendarPagerFragment();
		f.setArguments(args);
		return f;
	}
	
	public CalendarPagerFragment() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		getStatusView().show().text("Retrieving calendar,\nPlease wait...");

		commonTask = new CalendarTask(tm, this);
		
		if(savedInstanceState == null)
			commonTask.fire();
		else
			addTabs(null);
	}
	
	public void addTabs(ArrayList<ArrayList<CalendarDate>> calendars) 
	{
		Bundle args = new Bundle();

		if(Utils.isOnline(getActivity()))
		{
			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, calendars == null ? null : calendars.get(0));
			mTabsAdapter.addTab(mTabHost.newTabSpec("premieres").setIndicator("Premieres"), CalendarFragment.class, args);
			args = new Bundle();
			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, calendars == null ? null : calendars.get(1));
			mTabsAdapter.addTab(mTabHost.newTabSpec("my_shows").setIndicator("My shows"), CalendarFragment.class, args);
			args = new Bundle();
			args.putSerializable(TraktoidConstants.BUNDLE_CALENDAR, calendars == null ? null : calendars.get(2));
			mTabsAdapter.addTab(mTabHost.newTabSpec("shows").setIndicator("Shows"), CalendarFragment.class, args);
			
		}
		else
			mTabsAdapter.addTab(mTabHost.newTabSpec("my_shows").setIndicator("My shows"), CalendarFragment.class, null);
		
		mTabHost.setCurrentTab(1);
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
