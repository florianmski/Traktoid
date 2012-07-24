package com.florianmski.tracktoid.trakt.tasks.post;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.florianmski.tracktoid.TraktBus;
import com.florianmski.tracktoid.TraktItemsUpdatedEvent;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.services.MovieService;
import com.jakewharton.trakt.services.ShowService;
import com.jakewharton.trakt.services.ShowService.EpisodeUnwatchlistBuilder;
import com.jakewharton.trakt.services.ShowService.EpisodeWatchlistBuilder;

public abstract class InWatchlistTask<T extends TraktoidInterface<T>> extends PostTask
{
	protected List<T> traktItems;
	protected boolean addToWatchlist;

	public InWatchlistTask(Activity context, List<T> traktItems, boolean addToWatchlist, PostListener pListener) 
	{
		super(context, null, pListener);

		this.traktItems = traktItems;
		this.addToWatchlist = addToWatchlist;
	}

	public static <T extends TraktoidInterface<T>> InWatchlistTask<?> createTask(Activity context, T traktItem, boolean addToWatchlist, PostListener pListener)
	{
		List<T> traktItems = new ArrayList<T>();
		traktItems.add(traktItem);
		return createTask(context, traktItems, addToWatchlist, pListener);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TraktoidInterface<T>> InWatchlistTask<?> createTask(Activity context, List<T> traktItems, boolean addToWatchlist, PostListener pListener)
	{
		if(traktItems.get(0) instanceof TvShow)
			return new InWatchlistShowTask(context, (List<TvShow>) traktItems, addToWatchlist, pListener);
		else if(traktItems.get(0) instanceof Movie)
			return new InWatchlistMovieTask(context, (List<Movie>) traktItems, addToWatchlist, pListener);
		else if(traktItems.get(0) instanceof TvShowEpisode)
			return new InWatchlistEpisodeTask(context, (List<TvShowEpisode>) traktItems, addToWatchlist, pListener);
		else
			return null;
	}

	protected abstract TraktApiBuilder<?> createWatchlistBuilder(List<T> traktItems);
	protected abstract TraktApiBuilder<?> createUnwatchlistBuilder(List<T> traktItems);
	protected abstract void insertInDb(List<T> traktItems, boolean addToWatchlist, DatabaseWrapper dbw);

	@Override
	protected void doPrePostStuff() 
	{
		if(addToWatchlist)
			builders.add(createWatchlistBuilder(traktItems));
		else
			builders.add(createUnwatchlistBuilder(traktItems));
	}

	@Override
	protected void doAfterPostStuff()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		insertInDb(traktItems, addToWatchlist, dbw);
		dbw.close();
	}

	@Override
	protected void sendEvent(Response result) 
	{
		TraktBus.getInstance().post(new TraktItemsUpdatedEvent<T>(traktItems));
	}

	public static final class InWatchlistShowTask extends InWatchlistTask<TvShow>
	{
		public InWatchlistShowTask(Activity context, List<TvShow> traktItems, boolean addToWatchlist, PostListener pListener) 
		{
			super(context, traktItems, addToWatchlist, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createWatchlistBuilder(List<TvShow> traktItems) 
		{
			ShowService.WatchlistBuilder builder = tm.showService().watchlist();
			for(TvShow traktItem : traktItems)
				builder.tvdbId(Integer.valueOf(traktItem.getId()));
			return builder;
		}

		@Override
		protected TraktApiBuilder<?> createUnwatchlistBuilder(List<TvShow> traktItems) 
		{
			ShowService.UnwatchlistBuilder builder = tm.showService().unwatchlist();
			for(TvShow traktItem : traktItems)
				builder.tvdbId(Integer.valueOf(traktItem.getId()));
			return builder;
		}

		@Override
		protected void insertInDb(List<TvShow> traktItems, boolean addToWatchlist, DatabaseWrapper dbw) 
		{
			for(TvShow traktItem : traktItems)
			{
				traktItem.inWatchlist = addToWatchlist;
				dbw.insertOrUpdateShow(traktItem);
			}
		}

	}

	public static final class InWatchlistMovieTask extends InWatchlistTask<Movie>
	{
		public InWatchlistMovieTask(Activity context, List<Movie> traktItems, boolean addToWatchlist, PostListener pListener) 
		{
			super(context, traktItems, addToWatchlist, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createWatchlistBuilder(List<Movie> traktItems) 
		{
			MovieService.WatchlistBuilder builder = tm.movieService().watchlist();
			for(Movie traktItem : traktItems)
				builder.movie(traktItem.getId());
			return builder;
		}

		@Override
		protected TraktApiBuilder<?> createUnwatchlistBuilder(List<Movie> traktItems) 
		{
			MovieService.UnwatchlistBuilder builder = tm.movieService().unwatchlist();
			for(Movie traktItem : traktItems)
				builder.movie(traktItem.getId());
			return builder;
		}

		@Override
		protected void insertInDb(List<Movie> traktItems, boolean addToWatchlist, DatabaseWrapper dbw) 
		{
			for(Movie traktItem : traktItems)
			{
				traktItem.inWatchlist = addToWatchlist;
				dbw.insertOrUpdateMovie(traktItem);
			}
		}

	}

	public static final class InWatchlistEpisodeTask extends InWatchlistTask<TvShowEpisode>
	{
		public InWatchlistEpisodeTask(Activity context, List<TvShowEpisode> traktItem, boolean addToWatchlist, PostListener pListener) 
		{
			super(context, traktItem, addToWatchlist, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createWatchlistBuilder(List<TvShowEpisode> traktItems) 
		{
			EpisodeWatchlistBuilder builder = null;
			for(TvShowEpisode traktItem : traktItems)
			{
				if(builder == null)
					builder = tm.showService().episodeWatchlist(Integer.valueOf(traktItem.tvdbId));
				builder.episode(traktItem.season, traktItem.number);
			}
			return builder;
		}

		@Override
		protected TraktApiBuilder<?> createUnwatchlistBuilder(List<TvShowEpisode> traktItems) 
		{
			EpisodeUnwatchlistBuilder builder = null;
			for(TvShowEpisode traktItem : traktItems)
			{
				if(builder == null)
					builder = tm.showService().episodeUnwatchlist(Integer.valueOf(traktItem.tvdbId));
				builder.episode(traktItem.season, traktItem.number);
			}
			return builder;
		}

		@Override
		protected void insertInDb(List<TvShowEpisode> traktItems, boolean addToWatchlist, DatabaseWrapper dbw) 
		{
			for(TvShowEpisode traktItem : traktItems)
			{
				traktItem.inWatchlist = addToWatchlist;
				dbw.insertOrUpdateEpisode(traktItem);
			}
		}
	}
}