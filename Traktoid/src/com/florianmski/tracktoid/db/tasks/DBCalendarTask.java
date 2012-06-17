package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.entities.CalendarDate;

public class DBCalendarTask extends DBTask
{
	private List<CalendarDate> calendar;
	
	public DBCalendarTask(Context context, DBListener listener) 
	{
		super(context, listener);
	}
	
	@Override
	protected void doDBStuff() 
	{
		calendar = dbw.getFutureEpisodes();		
	}
	
	@Override
	protected void onPostExecute (Boolean success)
	{
		listener.onDBCalendar(calendar);
	}
	
}