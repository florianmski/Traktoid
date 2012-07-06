package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.florianmski.tracktoid.ApiCache;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Genre;

public class GenresTask extends BaseTask<List<Genre>>
{
	private List<Genre> genres = new ArrayList<Genre>();
	private GenresListener listener;
	private TraktApiBuilder<List<Genre>> builder;

	public <T extends TraktoidInterface<T>> GenresTask(Activity context, GenresListener listener, TraktApiBuilder<List<Genre>> builder) 
	{
		super(context);

		this.listener = listener;
		this.builder = builder;
	}

	@Override
	protected List<Genre> doTraktStuffInBackground() 
	{
		genres = ApiCache.read(builder, context);

		// we don't need to retrieve everytime genres
		// they might not change so we always use what we have in cache except the first time
		if(genres == null)
		{
			genres = builder.fire();
			ApiCache.save(builder, genres, context);
		}

		return genres;
	}

	public interface GenresListener
	{
		public void onGenres(List<Genre> genres);
	}

	@Override
	protected void sendEvent(List<Genre> result) 
	{
		if(listener != null && context != null)
			listener.onGenres(genres);
	}

}
