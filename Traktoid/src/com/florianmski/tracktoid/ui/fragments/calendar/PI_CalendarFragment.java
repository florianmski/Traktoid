package com.florianmski.tracktoid.ui.fragments.calendar;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListCalendarAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask;
import com.florianmski.tracktoid.trakt.tasks.get.CalendarTask.CalendarListener;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.florianmski.tracktoid.ui.fragments.BaseFragment.TaskListener;
import com.jakewharton.trakt.entities.CalendarDate;

public class PI_CalendarFragment extends TraktFragment implements TaskListener
{
	private List<CalendarDate> calendar;
	private int type;
	private ListView lvEpisodes;

	public static PI_CalendarFragment newInstance(Bundle args)
	{
		PI_CalendarFragment f = new PI_CalendarFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setTaskListener(this);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		type = getArguments().getInt(TraktoidConstants.BUNDLE_CALENDAR);
		getStatusView().show().text("Retrieving calendar,\nPlease wait...");
		
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_calendar, null);
		lvEpisodes = (ListView)v.findViewById(R.id.listViewCalendar);
		return v;
	}

	@Override
	public void onCreateTask() 
	{
		task = new CalendarTask(getActivity(), type, new CalendarListener() 
		{	
			@Override
			public void onCalendar(List<CalendarDate> calendar) 
			{
				PI_CalendarFragment.this.calendar = calendar;
				
				lvEpisodes.setAdapter(new ListCalendarAdapter(calendar, getActivity()));

				if(lvEpisodes.getAdapter().isEmpty() && type == CalendarTask.USER)
					getStatusView().hide().text("Nothing,\nTry some new show!");
				else
					getStatusView().hide().text(null);
			}
		});

		task.execute();
	}

	@Override
	public void onTaskIsDone() 
	{
		lvEpisodes.setAdapter(new ListCalendarAdapter(calendar, getActivity()));

		if(lvEpisodes.getAdapter().isEmpty() && type == CalendarTask.USER)
			getStatusView().hide().text("Nothing,\nTry some new show!");
		else
			getStatusView().hide().text(null);
	}

	@Override
	public void onTaskIsRunning() {}
}
