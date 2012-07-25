package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.entities.CalendarDate;

public class DBCalendarTask extends DBTask<List<CalendarDate>>
{	
	public DBCalendarTask(Context context, DBListener listener) 
	{
		super(context, listener);
	}
	
	@Override
	protected List<CalendarDate> doDBStuff() 
	{
		return dbw.getFutureEpisodes();		
	}
	
	@Override
	protected void onCompleted(List<CalendarDate> result)
	{
		listener.onDBCalendar(result);
	}
	
}