package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.entities.Movie;

public class DBMoviesTask extends DBTask
{
	private List<Movie> movies;
	
	public DBMoviesTask(Context context, DBListener listener) 
	{
		super(context, listener);
	}
	
	@Override
	protected void doDBStuff() 
	{
		movies = dbw.getMovies();		
	}
	
	@Override
	protected void onPostExecute (Boolean success)
	{
		listener.onDBMovies(movies);
	}
	
}
