package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.List;

import android.app.Activity;

import com.florianmski.tracktoid.ApiCache;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.CalendarDate;

public class CalendarTask extends BaseTask<List<CalendarDate>>
{		
	public final static int PREMIERES = 0, USER = 1, SHOWS = 2;  
	
	private int type;
	private CalendarListener listener;

	public CalendarTask(Activity context, int type, CalendarListener listener) 
	{
		super(context);
		
		this.type = type;
		this.listener = listener;
	}

	@Override
	protected List<CalendarDate> doTraktStuffInBackground()
	{			
		TraktApiBuilder<List<CalendarDate>> builder = null;
		switch(type)
		{
		case PREMIERES:
			builder = tm.calendarService().premieres();
			break;
		case USER:
			builder = tm.userService().calendarShows(TraktManager.getUsername());
			break;
		case SHOWS:
			builder = tm.calendarService().shows();
			break;
		}
		
		List<CalendarDate> calendarListShows = ApiCache.read(builder, context);
		if(calendarListShows != null)
			publishProgress(EVENT, calendarListShows);
		
		calendarListShows = builder.fire();

		ApiCache.save(builder, calendarListShows, context);
		
		return calendarListShows;
	}

	@Override
	protected void sendEvent(List<CalendarDate> result) 
	{
		if(context != null && listener != null)
			listener.onCalendar(result);
	}

	public interface CalendarListener
	{
		public void onCalendar(List<CalendarDate> calendar);
	}
}
