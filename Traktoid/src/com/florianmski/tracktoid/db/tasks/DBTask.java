package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.BackgroundTask;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public abstract class DBTask<TResult> extends BackgroundTask<TResult>
{
	protected DBListener listener;
	protected DatabaseWrapper dbw;
	protected Context context;

	public DBTask(Context context, DBListener listener)
	{
		super();
		
		this.context = context;
		this.listener = listener;
	}
	
	@Override
	protected TResult doWorkInBackground() 
	{		
		dbw = new DatabaseWrapper(context);
		TResult result = null;
		
		try
		{
			result = doDBStuff();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		dbw.close();
		return result;
	}
	
	protected abstract TResult doDBStuff();
	
	public interface DBListener
	{
		public void onDBShows(List<TvShow> shows);
		public void onDBMovies(List<Movie> movies);
		public void onDBSeasons(List<TvShowSeason> seasons);
		public void onDBSeason(List<TvShowEpisode> episodes);
		public void onDBEpisodes(List<TvShowEpisode> episodes);
		public void onDBCalendar(List<CalendarDate> calendar);
	}

	@Override
	protected void onFailed(Exception e) {}

	@Override
	protected void onPreExecute() {}

	@Override
	protected void onProgressPublished(int progress, TResult tmpResult,	String... values) {}

}
