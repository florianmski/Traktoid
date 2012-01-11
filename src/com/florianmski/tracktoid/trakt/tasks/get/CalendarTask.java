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

package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;
import com.jakewharton.trakt.entities.TvShow;

public class CalendarTask extends TraktTask
{	
	ArrayList<ArrayList<CalendarDate>> calendars = new ArrayList<ArrayList<CalendarDate>>();
	
	private CalendarListener listener;

	public CalendarTask(TraktManager tm, Fragment fragment, CalendarListener listener) 
	{
		super(tm, fragment);
		
		this.listener = listener;
	}
	
	//instead of doing 3 requests (user shows, premieres and all), we do only "all" and then sorts
	@Override
	protected boolean doTraktStuffInBackground()
	{	
		showToast("Retrieving calendar...", Toast.LENGTH_SHORT);
		
		ArrayList<CalendarDate> calendarListShows;
		ArrayList<CalendarDate> calendarListPremieres = new ArrayList<CalendarDate>();
		ArrayList<CalendarDate> calendarListMyShows = new ArrayList<CalendarDate>();
		
		calendarListShows = (ArrayList<CalendarDate>) tm.calendarService().shows().fire();
		
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		dbw.open();
		
		List<TvShow> shows = dbw.getShows();
			
		dbw.close();
		
		for(CalendarDate cd : calendarListShows)
		{
			CalendarDate calendarPremieres = new CalendarDate();
			CalendarDate calendarMyShows = new CalendarDate();
			
			calendarPremieres.date = cd.date;
			calendarMyShows.date = cd.date;
			
			List<CalendarTvShowEpisode> episodesPremieres = new ArrayList<CalendarTvShowEpisode>();
			List<CalendarTvShowEpisode> episodesMyShows = new ArrayList<CalendarTvShowEpisode>();
			
			for(CalendarTvShowEpisode e : cd.episodes)
			{				
				int index = Collections.binarySearch(shows, e.show);
				
				if(e.episode.number == 1)
					episodesPremieres.add(e);
				if(index != -1 && index >= 0 && index < shows.size())
					episodesMyShows.add(e);
			}
			
			if(!episodesPremieres.isEmpty())
			{
				calendarPremieres.episodes = episodesPremieres;
				calendarListPremieres.add(calendarPremieres);
			}
			
			if(!episodesMyShows.isEmpty())
			{
				calendarMyShows.episodes = episodesMyShows;
				calendarListMyShows.add(calendarMyShows);
			}
		}		
		
		calendars.add(calendarListPremieres);
		calendars.add(calendarListMyShows);
		calendars.add(calendarListShows);
				
		return true;
	}
	
	@Override
	protected boolean doOfflineTraktStuff()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		dbw.open();
		
		ArrayList<CalendarDate> episodes = dbw.getFutureEpisodes();
			
		dbw.close();
		
		calendars.add(episodes);
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success && !Utils.isActivityFinished(fragment.getActivity()))
			listener.onCalendar(calendars);
	}
	
	public interface CalendarListener
	{
		public void onCalendar(ArrayList<ArrayList<CalendarDate>> calendars);
	}

}
