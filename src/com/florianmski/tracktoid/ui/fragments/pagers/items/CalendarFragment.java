package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.lists.ListCalendarAdapter;
import com.jakewharton.trakt.entities.CalendarDate;

public class CalendarFragment extends PagerItemFragment
{
	private ArrayList<CalendarDate> calendar;

	public CalendarFragment() {}
	
	public CalendarFragment(ArrayList<CalendarDate> calendar) 
	{
		this.calendar = calendar;
		//retain fragment when rotation occurs
		this.setRetainInstance(true);
	}

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_season, null);
		ListView lvEpisodes = (ListView)v.findViewById(R.id.listViewEpisodes);

		lvEpisodes.setAdapter(new ListCalendarAdapter(calendar, getActivity()));
		
		return v;
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
