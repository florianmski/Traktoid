package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public abstract class InWatchlistTask<T extends TraktoidInterface> extends PostTask
{
	protected T traktItem;
	protected boolean addToWatchlist;

	public InWatchlistTask(TraktManager tm, Fragment fragment, T traktItem, boolean addToWatchlist, PostListener pListener) 
	{
		super(tm, fragment, null, pListener);

		this.traktItem = traktItem;
		this.addToWatchlist = addToWatchlist;
	}
	
	public static <T extends TraktoidInterface> InWatchlistTask<?> createTask(TraktManager tm, Fragment fragment, T traktItem, boolean addToWatchlist, PostListener pListener)
	{
		if(traktItem instanceof TvShow)
			return new InWatchlistShowTask(tm, fragment, (TvShow) traktItem, addToWatchlist, pListener);
		else if(traktItem instanceof Movie)
			return new InWatchlistMovieTask(tm, fragment, (Movie) traktItem, addToWatchlist, pListener);
		else if(traktItem instanceof TvShowEpisode)
			return new InWatchlistEpisodeTask(tm, fragment, (TvShowEpisode) traktItem, addToWatchlist, pListener);
		else
			return null;
	}

	protected abstract TraktApiBuilder<?> createWatchlistBuilder(T traktItem);
	protected abstract TraktApiBuilder<?> createUnwatchlistBuilder(T traktItem);
	protected abstract void insertInDb(T traktItem, boolean addToWatchlist, DatabaseWrapper dbw);
	protected abstract void sendEvent(T traktItem);
	
	@Override
	protected void doPrePostStuff() 
	{
		if(addToWatchlist)
			builders.add(createWatchlistBuilder(traktItem));
		else
			builders.add(createUnwatchlistBuilder(traktItem));
	}
	
	@Override
	protected void doAfterPostStuff()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		insertInDb(traktItem, addToWatchlist, dbw);
		dbw.close();
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(success)
			sendEvent(traktItem);			
	}
	
	public static final class InWatchlistShowTask extends InWatchlistTask<TvShow>
	{
		public InWatchlistShowTask(TraktManager tm, Fragment fragment,	TvShow traktItem, boolean addToWatchlist, PostListener pListener) 
		{
			super(tm, fragment, traktItem, addToWatchlist, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createWatchlistBuilder(TvShow traktItem) 
		{
			return tm
					.showService()
					.watchlist()
					.tvdbId(Integer.valueOf(traktItem.getId()));
		}

		@Override
		protected TraktApiBuilder<?> createUnwatchlistBuilder(TvShow traktItem) 
		{
			return tm
					.showService()
					.unwatchlist()
					.tvdbId(Integer.valueOf(traktItem.getId()));
		}

		@Override
		protected void sendEvent(TvShow traktItem) 
		{
			tm.onShowUpdated(traktItem);
		}

		@Override
		protected void insertInDb(TvShow traktItem, boolean addToWatchlist, DatabaseWrapper dbw) 
		{
			traktItem.inWatchlist = addToWatchlist;
			dbw.insertOrUpdateShow(traktItem);
		}
		
	}
	
	public static final class InWatchlistMovieTask extends InWatchlistTask<Movie>
	{
		public InWatchlistMovieTask(TraktManager tm, Fragment fragment, Movie traktItem, boolean addToWatchlist, PostListener pListener) 
		{
			super(tm, fragment, traktItem, addToWatchlist, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createWatchlistBuilder(Movie traktItem) 
		{
			return tm
					.movieService()
					.watchlist()
					.movie(traktItem.getId());
		}

		@Override
		protected TraktApiBuilder<?> createUnwatchlistBuilder(Movie traktItem) 
		{
			return tm
					.movieService()
					.unwatchlist()
					.movie(traktItem.getId());
		}

		@Override
		protected void sendEvent(Movie traktItem) 
		{
			tm.onMovieUpdated(traktItem);
		}
		
		@Override
		protected void insertInDb(Movie traktItem, boolean addToWatchlist, DatabaseWrapper dbw) 
		{
			traktItem.inWatchlist = addToWatchlist;
			dbw.insertOrUpdateMovie(traktItem);
		}
		
	}
	
	public static final class InWatchlistEpisodeTask extends InWatchlistTask<TvShowEpisode>
	{
		public InWatchlistEpisodeTask(TraktManager tm, Fragment fragment, TvShowEpisode traktItem, boolean addToWatchlist, PostListener pListener) 
		{
			super(tm, fragment, traktItem, addToWatchlist, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createWatchlistBuilder(TvShowEpisode traktItem) 
		{
			return tm
					.showService()
					.episodeWatchlist(Integer.valueOf(traktItem.tvdbId))
					.episode(traktItem.season, traktItem.number);
		}

		@Override
		protected TraktApiBuilder<?> createUnwatchlistBuilder(TvShowEpisode traktItem) 
		{
			return tm
					.showService()
					.episodeUnwatchlist(Integer.valueOf(traktItem.tvdbId))
					.episode(traktItem.season, traktItem.number);
		}

		@Override
		protected void sendEvent(TvShowEpisode traktItem)
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		protected void insertInDb(TvShowEpisode traktItem, boolean addToWatchlist, DatabaseWrapper dbw) 
		{
			traktItem.inWatchlist = addToWatchlist;
			dbw.insertOrUpdateEpisode(traktItem);
		}
	}
}