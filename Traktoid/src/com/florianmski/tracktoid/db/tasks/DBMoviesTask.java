package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.entities.Movie;

public class DBMoviesTask extends DBTask<List<Movie>>
{	
	public DBMoviesTask(Context context, DBListener listener) 
	{
		super(context, listener);
	}
	
	@Override
	protected List<Movie> doDBStuff() 
	{
		return dbw.getMovies();		
	}
	
	@Override
	protected void onCompleted(List<Movie> result)
	{
		listener.onDBMovies(result);
	}
	
}
