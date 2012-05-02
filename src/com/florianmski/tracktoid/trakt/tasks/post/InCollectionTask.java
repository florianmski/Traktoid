package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class InCollectionTask<T extends TraktoidInterface<T>> extends PostTask
{
	//TODO send show, movie, episode to update other views
	
	private T traktItem;
	private boolean addToCollection;

	public InCollectionTask(TraktManager tm, Fragment fragment, T traktItem, boolean addToCollection, PostListener pListener) 
	{
		super(tm, fragment, null, pListener);

		this.traktItem = traktItem;
		this.addToCollection = addToCollection;
	}

	@Override
	protected void doPrePostStuff() 
	{
		if(addToCollection)
		{
			//TODO strange stuff here, have to modify trakt-java
			//TODO add season support
			//		if(traktItem instanceof TvShow)
			//			builder = tm
			//			.showService()
			//			.library()
			//			.tvdbId(Integer.valueOf(traktItem.getId()));
			//		else if(traktItem instanceof Movie)
			//			builder = tm
			//			.movieService()
			//			.library()
			//			.movie(traktItem.getId());
			if(traktItem instanceof TvShowEpisode)
				builder = tm
				.showService()
				.episodeLibrary(Integer.valueOf(traktItem.getId()))
				.episode(((TvShowEpisode)traktItem).season, ((TvShowEpisode)traktItem).number);
		}
		else
		{
//			if(traktItem instanceof TvShow)
//				builder = tm
//				.showService()
//				.unlibrary()
//				.tvdbId(Integer.valueOf(traktItem.getId()));
			if(traktItem instanceof Movie)
				builder = tm
				.movieService()
				.unlibrary()
				.movie(traktItem.getId());
			else if(traktItem instanceof TvShowEpisode)
				builder = tm
				.showService()
				.episodeUnlibrary(Integer.valueOf(traktItem.getId()))
				.episode(((TvShowEpisode)traktItem).season, ((TvShowEpisode)traktItem).number);
		}
	}
}