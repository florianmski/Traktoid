package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public abstract class DBTask extends AsyncTask<Void, String, Boolean>
{
	protected DBListener listener;
	protected Context context;
	protected DatabaseWrapper dbw;

	public DBTask(Context context, DBListener listener)
	{
		this.listener = listener;
		this.context = context;
	}
	
	public void fire()
	{
		if(Build.VERSION.SDK_INT >= 11)
			this.executeOnExecutor(THREAD_POOL_EXECUTOR);
		else
			this.execute();
	}
	
	@Override
	protected Boolean doInBackground(Void... params) 
	{		
		dbw = new DatabaseWrapper(context);
		
		try
		{
			doDBStuff();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		dbw.close();
		return true;
	}
	
	protected abstract void doDBStuff();
	
	public interface DBListener
	{
		public void onDBShows(List<TvShow> shows);
		public void onDBMovies(List<Movie> movies);
		public void onDBSeasons(List<TvShowSeason> seasons);
		public void onDBEpisodes(List<TvShowEpisode> episodes);
		public void onDBCalendar(List<CalendarDate> calendar);
	}

}
