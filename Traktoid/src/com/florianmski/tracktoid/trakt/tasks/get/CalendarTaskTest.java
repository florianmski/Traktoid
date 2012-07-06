package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.florianmski.tracktoid.ApiCache;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.BackgroundTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.CalendarDate;

public class CalendarTaskTest extends BackgroundTask<List<CalendarDate>>
{
	protected final static int TOAST = -1;
	protected final static int EVENT = -2;
	protected final static int ERROR = -3;
	
	public final static int PREMIERES = 0, USER = 1, SHOWS = 2;  

	private int type;
	private Activity context;
	private CalendarListener listener;

	public CalendarTaskTest(Activity context, int type, CalendarListener listener) 
	{
		super();

		this.context = context;
		this.type = type;
		this.listener = listener;
		
		Log.e("test","start a new calendar task : " + type);
	}

	public void detach()
	{
		context = null;
	}
	
	public void attach(Activity a)
	{
		context = a;
	}
	
	@Override
	protected List<CalendarDate> doWorkInBackground() throws Exception 
	{
		TraktApiBuilder<List<CalendarDate>> builder = null;
		switch(type)
		{
		case PREMIERES:
			builder = TraktManager.getInstance().calendarService().premieres();
			break;
		case USER:
			builder = TraktManager.getInstance().userService().calendarShows(TraktManager.getUsername());
			break;
		case SHOWS:
			builder = TraktManager.getInstance().calendarService().shows();
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
	protected void onCompleted(List<CalendarDate> result) 
	{
		sendEvent(result);
		
		Log.e("test","calendar task finish : " + type);
	}
	
	@Override
	protected void onProgressPublished(int progress, List<CalendarDate> tmpResult, String... values)
	{
		if(progress == TOAST)
			Toast.makeText(context, values[1], Integer.parseInt(values[0])).show();
		else if(progress == ERROR)
		{
			//TODO something smart
		}
		else if(progress == EVENT)
		{
			sendEvent(tmpResult);
		}
	}

	@Override
	protected void onFailed(Exception e) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPreExecute() 
	{
		// TODO Auto-generated method stub

	}

	protected void sendEvent(List<CalendarDate> result) 
	{
		if(listener != null && result != null && context != null)
			listener.onCalendar(result);
	}

	public interface CalendarListener
	{
		public void onCalendar(List<CalendarDate> calendar);
	}

}
