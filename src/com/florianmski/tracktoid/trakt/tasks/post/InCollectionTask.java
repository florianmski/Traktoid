package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public abstract class InCollectionTask<T extends TraktoidInterface<T>> extends PostTask
{
	protected T traktItem;
	protected boolean addToCollection;

	public InCollectionTask(Fragment fragment, T traktItem, boolean addToCollection, PostListener pListener) 
	{
		super(fragment, null, pListener);

		this.traktItem = traktItem;
		this.addToCollection = addToCollection;
	}

	public static <T extends TraktoidInterface<T>> InCollectionTask<?> createTask(Fragment fragment, T traktItem, boolean addToCollection, PostListener pListener)
	{
		if(traktItem instanceof TvShow)
			return new InCollectionShowTask(fragment, (TvShow) traktItem, addToCollection, pListener);
		else if(traktItem instanceof Movie)
			return new InCollectionMovieTask(fragment, (Movie) traktItem, addToCollection, pListener);
		else if(traktItem instanceof TvShowEpisode)
			return new InCollectionEpisodeTask(fragment, (TvShowEpisode) traktItem, addToCollection, pListener);
		else
			return null;
	}

	protected abstract TraktApiBuilder<?> createLibraryBuilder(T traktItem);
	protected abstract TraktApiBuilder<?> createUnlibraryBuilder(T traktItem);
	protected abstract void insertInDb(T traktItem, boolean addToCollection, DatabaseWrapper dbw);

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
	protected void sendEvent(Response result) 
	{
		TraktTask.traktItemUpdated(traktItem);
	}

	public static final class InCollectionShowTask extends InCollectionTask<TvShow>
	{
		public InCollectionShowTask(Fragment fragment,	TvShow traktItem, boolean addToCollection, PostListener pListener) 
		{
			super(fragment, traktItem, addToCollection, pListener);
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
		protected void insertInDb(TvShow traktItem, boolean addToCollection, DatabaseWrapper dbw) 
		{
			dbw.addOrRemoveShowInCollection(traktItem.tvdbId, addToCollection);
		}

	}

	public static final class InCollectionMovieTask extends InCollectionTask<Movie>
	{
		public InCollectionMovieTask(Fragment fragment, Movie traktItem, boolean addToCollection, PostListener pListener) 
		{
			super(fragment, traktItem, addToCollection, pListener);
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
		protected void insertInDb(Movie traktItem, boolean addToCollection, DatabaseWrapper dbw) 
		{
			traktItem.inCollection = addToCollection;
			dbw.insertOrUpdateMovie(traktItem);
		}

	}

	public static final class InCollectionEpisodeTask extends InCollectionTask<TvShowEpisode>
	{
		public InCollectionEpisodeTask(Fragment fragment, TvShowEpisode traktItem, boolean addToCollection, PostListener pListener) 
		{
			super(fragment, traktItem, addToCollection, pListener);
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
		protected void insertInDb(TvShowEpisode traktItem, boolean addToCollection, DatabaseWrapper dbw) 
		{
			traktItem.inCollection = addToCollection;
			dbw.insertOrUpdateEpisode(traktItem);
		}
	}
}