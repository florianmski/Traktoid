package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.entities.TvShow;

public class DBShowsTask extends DBTask
{
	private List<TvShow> shows;
	
	public DBShowsTask(Context context, DBListener listener) 
	{
		super(context, listener);
	}
	
	@Override
	protected void doDBStuff() 
	{
		shows = dbw.getShows();		
	}
	
	@Override
	protected void onPostExecute (Boolean success)
	{
		listener.onDBShows(shows);
	}
	
}
