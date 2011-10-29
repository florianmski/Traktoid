/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.trakt.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.florianmski.tracktoid.adapters.ListCalendarAdapter;
import com.florianmski.tracktoid.adapters.PagerCalendarAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;

public class CalendarTask extends TraktTask
{	
	private PagerCalendarAdapter adapter;
	
	private CalendarListener listener;

	public CalendarTask(TraktManager tm, Context context, CalendarListener listener) 
	{
		super(tm, context);
		
		this.listener = listener;
	}
	
	//instead of doing 3 requests (user shows, premieres and all), we do only "all" and then sorts
	@Override
	protected void doTraktStuffInBackground()
	{	
		this.publishProgress("toast", "0", "Retrieving calendar...");
		
		List<CalendarDate> calendarListShows;
		List<CalendarDate> calendarListPremieres = new ArrayList<CalendarDate>();
		List<CalendarDate> calendarListMyShows = new ArrayList<CalendarDate>();
		
		calendarListShows = tm.calendarService().shows().fire();
		
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		dbw.open();
		
		List<TvShow> shows = dbw.getShows();
			
		dbw.close();
		
		for(CalendarDate cd : calendarListShows)
		{
			CalendarDate calendarPremieres = new CalendarDate();
			CalendarDate calendarMyShows = new CalendarDate();
			
			calendarPremieres.setDate(cd.getDate());
			calendarMyShows.setDate(cd.getDate());
			
			List<CalendarTvShowEpisode> episodesPremieres = new ArrayList<CalendarTvShowEpisode>();
			List<CalendarTvShowEpisode> episodesMyShows = new ArrayList<CalendarTvShowEpisode>();
			
			for(CalendarTvShowEpisode e : cd.getEpisodes())
			{				
				int index = Collections.binarySearch(shows, e.getShow());
				
				if(e.getEpisode().getNumber() == 1)
					episodesPremieres.add(e);
				if(index != -1 && index >= 0 && index < shows.size())
					episodesMyShows.add(e);
			}
			
			if(!episodesPremieres.isEmpty())
			{
				calendarPremieres.setEpisodes(episodesPremieres);
				calendarListPremieres.add(calendarPremieres);
			}
			
			if(!episodesMyShows.isEmpty())
			{
				calendarMyShows.setEpisodes(episodesMyShows);
				calendarListMyShows.add(calendarMyShows);
			}
		}
		
		ArrayList<ListCalendarAdapter> adapters = new ArrayList<ListCalendarAdapter>();

		adapters.add(new ListCalendarAdapter(calendarListPremieres, context));
		adapters.add(new ListCalendarAdapter(calendarListMyShows, context));
		adapters.add(new ListCalendarAdapter(calendarListShows, context));
		
		adapter = new PagerCalendarAdapter(context, adapters);
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success)
			listener.onCalendar(adapter);
	}
	
	public interface CalendarListener
	{
		public void onCalendar(PagerCalendarAdapter adapter);
	}

}
