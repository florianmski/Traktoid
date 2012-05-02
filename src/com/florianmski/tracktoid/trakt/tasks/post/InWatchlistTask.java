package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class InWatchlistTask<T extends TraktoidInterface<T>> extends PostTask
{
	//TODO send show, movie, episode to update other views
	
	private T traktItem;
	private boolean addToWatchlist;

	public InWatchlistTask(TraktManager tm, Fragment fragment, T traktItem, boolean addToWatchlist, PostListener pListener) 
	{
		super(tm, fragment, null, pListener);

		this.traktItem = traktItem;
		this.addToWatchlist = addToWatchlist;
	}

	@Override
	protected void doPrePostStuff() 
	{
		if(addToWatchlist)
		{
			if(traktItem instanceof TvShow)
				builder = tm
				.showService()
				.watchlist()
				.tvdbId(Integer.valueOf(traktItem.getId()));
			else if(traktItem instanceof Movie)
				builder = tm
				.movieService()
				.watchlist()
				.movie(traktItem.getId());
			else if(traktItem instanceof TvShowEpisode)
				builder = tm
				.showService()
				.episodeWatchlist(Integer.valueOf(traktItem.getId()))
				.episode(((TvShowEpisode)traktItem).season, ((TvShowEpisode)traktItem).number);
		}
		else
		{
			if(traktItem instanceof TvShow)
				builder = tm
				.showService()
				.unwatchlist()
				.tvdbId(Integer.valueOf(traktItem.getId()));
			else if(traktItem instanceof Movie)
				builder = tm
				.movieService()
				.unwatchlist()
				.movie(traktItem.getId());
			else if(traktItem instanceof TvShowEpisode)
				builder = tm
				.showService()
				.episodeUnwatchlist(Integer.valueOf(traktItem.getId()))
				.episode(((TvShowEpisode)traktItem).season, ((TvShowEpisode)traktItem).number);
		}
	}
}