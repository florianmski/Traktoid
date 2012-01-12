package com.florianmski.tracktoid.ui.fragments.pagers;

import java.util.ArrayList;

import android.os.Bundle;
import com.florianmski.tracktoid.adapters.pagers.PagerCalendarAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask.CalendarListener;
import com.jakewharton.trakt.entities.CalendarDate;

public class CalendarPagerFragment extends PagerFragment
{
	ArrayList<ArrayList<CalendarDate>> calendars;
	
	public static CalendarPagerFragment newInstance(Bundle args)
	{
		CalendarPagerFragment f = new CalendarPagerFragment();
		f.setArguments(args);
		return f;
	}
	
	public CalendarPagerFragment() {}
	
	public CalendarPagerFragment(FragmentListener listener) 
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

		getStatusView().show().text("Retrieving calendar,\nPlease wait...");

		commonTask = new CalendarTask(tm, this, new CalendarListener() 
		{
			@Override
			public void onCalendar(ArrayList<ArrayList<CalendarDate>> calendars) 
			{
				CalendarPagerFragment.this.calendars = calendars;
				createAdapter();
			}
		});
		
		if(savedInstanceState == null)
			commonTask.execute();
		else
		{
			//create empty arraylist to create a pageradapter that will be filled by calendarfragment (they saved their states)
			this.calendars = new ArrayList<ArrayList<CalendarDate>>();
			for(int i = 0; i < savedInstanceState.getInt("size"); i++)
				calendars.add(new ArrayList<CalendarDate>());
			
			createAdapter();
		}
	}
	
	public void createAdapter()
	{
		getStatusView().hide().text(null);
		adapter = new PagerCalendarAdapter(calendars, getFragmentManager());
		
		if(((PagerCalendarAdapter)adapter).isEmpty())
			getStatusView().hide().text("No calendar, this is strange...");
		else
			getStatusView().hide().text(null);
		
		initPagerFragment(adapter);
	}
	
	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		//create empty arraylist to create a pageradapter that will be filled by calendarfragment (they saved their states)
//		this.calendars = new ArrayList<ArrayList<CalendarDate>>();
//		for(int i = 0; i < savedInstanceState.getInt("size"); i++)
//			calendars.add(new ArrayList<CalendarDate>());
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
//		toSave.putInt("size", calendars.size());
	}
}
