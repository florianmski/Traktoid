package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.entities.TvShow;

public class DBShowsTask extends DBTask<List<TvShow>>
{	
	public DBShowsTask(Context context, DBListener listener) 
	{
		super(context, listener);
	}
	
	@Override
	protected List<TvShow> doDBStuff() 
	{
		return dbw.getShows();		
	}
	
	@Override
	protected void onCompleted(List<TvShow> result)
	{
		listener.onDBShows(result);
	}
	
}
