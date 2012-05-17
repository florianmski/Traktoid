package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListCalendarAdapter;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBCalendarTask;
import com.jakewharton.trakt.entities.CalendarDate;

public class PI_CalendarFragment extends PI_Fragment
{
	private ArrayList<CalendarDate> calendar;
	private ListView lvEpisodes;
	
	public static PI_CalendarFragment newInstance(Bundle args)
	{
		PI_CalendarFragment f = new PI_CalendarFragment();
		f.setArguments(args);
		return f;
	}
	
	public PI_CalendarFragment() 
	{
		//retain fragment when rotation occurs
		this.setRetainInstance(true);
	}
	
	public PI_CalendarFragment(ArrayList<CalendarDate> calendar) 
	{
		this();
		this.calendar = calendar;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		calendar = (ArrayList<CalendarDate>) (getArguments() == null ? null : getArguments().getSerializable(TraktoidConstants.BUNDLE_CALENDAR));
		
		getStatusView().show().text("Creating calendar,\nPlease wait...");
		
		//offline calendar
		if(calendar == null)
		{
			new DBCalendarTask(getActivity(), new DBAdapter() 
			{
				@Override
				public void onDBCalendar(List<CalendarDate> calendar) 
				{					
					PI_CalendarFragment.this.calendar = (ArrayList<CalendarDate>) calendar;
					lvEpisodes.setAdapter(new ListCalendarAdapter(calendar, getActivity()));
					
					if(lvEpisodes.getAdapter().isEmpty())
						getStatusView().hide().text("Nothing,\nTry some new show!");
					else
						getStatusView().hide().text(null);
				}
			}).fire();
		}
		//online calendar
		else
		{
			lvEpisodes.setAdapter(new ListCalendarAdapter(calendar, getActivity()));
			getStatusView().hide().text(null);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_calendar, null);
		lvEpisodes = (ListView)v.findViewById(R.id.listViewCalendar);
		return v;
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
