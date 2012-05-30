package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Genre;

public class GenresTask extends TraktTask<List<Genre>>
{
	private List<Genre> genres = new ArrayList<Genre>();
	private GenresListener listener;
	private TraktApiBuilder<List<Genre>> builder;
	
	public <T extends TraktoidInterface<T>> GenresTask(Fragment fragment, GenresListener listener, TraktApiBuilder<List<Genre>> builder) 
	{
		super(fragment);
		
		this.listener = listener;
		this.builder = builder;
	}

	@Override
	protected List<Genre> doTraktStuffInBackground() 
	{
		genres = builder.fire();
		
		return genres;
	}
	
	@Override
	protected void onCompleted(List<Genre> genres)
	{		
		if(genres != null && getRef() != null)
			listener.onGenres(genres);
	}
	
	public interface GenresListener
	{
		public void onGenres(List<Genre> genres);
	}

}
