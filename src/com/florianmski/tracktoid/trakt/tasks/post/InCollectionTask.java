package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public abstract class InCollectionTask<T extends TraktoidInterface> extends PostTask
{
	protected T traktItem;
	protected boolean addToCollection;

	public InCollectionTask(TraktManager tm, Fragment fragment, T traktItem, boolean addToCollection, PostListener pListener) 
	{
		super(tm, fragment, null, pListener);

		this.traktItem = traktItem;
		this.addToCollection = addToCollection;
	}

	public static <T extends TraktoidInterface> InCollectionTask<?> createTask(TraktManager tm, Fragment fragment, T traktItem, boolean addToCollection, PostListener pListener)
	{
		if(traktItem instanceof TvShow)
			return new InCollectionShowTask(tm, fragment, (TvShow) traktItem, addToCollection, pListener);
		else if(traktItem instanceof Movie)
			return new InCollectionMovieTask(tm, fragment, (Movie) traktItem, addToCollection, pListener);
		else if(traktItem instanceof TvShowEpisode)
			return new InCollectionEpisodeTask(tm, fragment, (TvShowEpisode) traktItem, addToCollection, pListener);
		else
			return null;
	}

	protected abstract TraktApiBuilder<?> createLibraryBuilder(T traktItem);
	protected abstract TraktApiBuilder<?> createUnlibraryBuilder(T traktItem);
	protected abstract void insertInDb(T traktItem, boolean addToCollection, DatabaseWrapper dbw);
	protected abstract void sendEvent(T traktItem);

	@Override
	protected void doPrePostStuff() 
	{
		if(addToCollection)
			builders.add(createLibraryBuilder(traktItem));
		else
			builders.add(createUnlibraryBuilder(traktItem));
	}

	@Override
	protected void doAfterPostStuff()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		insertInDb(traktItem, addToCollection, dbw);
		dbw.close();
	}

	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(success)
			sendEvent(traktItem);			
	}

	public static final class InCollectionShowTask extends InCollectionTask<TvShow>
	{
		public InCollectionShowTask(TraktManager tm, Fragment fragment,	TvShow traktItem, boolean addToCollection, PostListener pListener) 
		{
			super(tm, fragment, traktItem, addToCollection, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createLibraryBuilder(TvShow traktItem) 
		{
			return tm
					.showService()
					.library()
					.show(Integer.valueOf(traktItem.tvdbId));
		}

		@Override
		protected TraktApiBuilder<?> createUnlibraryBuilder(TvShow traktItem) 
		{
			return tm
					.showService()
					.unlibrary(Integer.valueOf(traktItem.tvdbId));
		}

		@Override
		protected void sendEvent(TvShow traktItem) 
		{
			tm.onShowUpdated(traktItem);
		}

		@Override
		protected void insertInDb(TvShow traktItem, boolean addToCollection, DatabaseWrapper dbw) 
		{
			dbw.addOrRemoveShowInCollection(traktItem.tvdbId, addToCollection);
		}

	}

	public static final class InCollectionMovieTask extends InCollectionTask<Movie>
	{
		public InCollectionMovieTask(TraktManager tm, Fragment fragment, Movie traktItem, boolean addToCollection, PostListener pListener) 
		{
			super(tm, fragment, traktItem, addToCollection, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createLibraryBuilder(Movie traktItem) 
		{
			return tm
					.movieService()
					.library()
					.movie(traktItem.imdbId);
		}

		@Override
		protected TraktApiBuilder<?> createUnlibraryBuilder(Movie traktItem) 
		{
			return tm
					.movieService()
					.unlibrary()
					.movie(traktItem.imdbId);
		}

		@Override
		protected void sendEvent(Movie traktItem) 
		{
			tm.onMovieUpdated(traktItem);
		}

		@Override
		protected void insertInDb(Movie traktItem, boolean addToCollection, DatabaseWrapper dbw) 
		{
			traktItem.inCollection = addToCollection;
			dbw.insertOrUpdateMovie(traktItem);
		}

	}

	public static final class InCollectionEpisodeTask extends InCollectionTask<TvShowEpisode>
	{
		public InCollectionEpisodeTask(TraktManager tm, Fragment fragment, TvShowEpisode traktItem, boolean addToCollection, PostListener pListener) 
		{
			super(tm, fragment, traktItem, addToCollection, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createLibraryBuilder(TvShowEpisode traktItem) 
		{
			return tm
					.showService()
					.episodeLibrary(Integer.valueOf((traktItem).tvdbId))
					.episode(traktItem.season, traktItem.number);
		}

		@Override
		protected TraktApiBuilder<?> createUnlibraryBuilder(TvShowEpisode traktItem) 
		{
			return tm
					.showService()
					.episodeUnlibrary(Integer.valueOf(traktItem.tvdbId))
					.episode(traktItem.season, traktItem.number);
		}

		@Override
		protected void sendEvent(TvShowEpisode traktItem)
		{
			// TODO Auto-generated method stub
		}

		@Override
		protected void insertInDb(TvShowEpisode traktItem, boolean addToCollection, DatabaseWrapper dbw) 
		{
			traktItem.inCollection = addToCollection;
			dbw.insertOrUpdateEpisode(traktItem);
		}
	}
}