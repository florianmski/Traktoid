package com.florianmski.tracktoid.trakt.tasks.post;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.florianmski.tracktoid.TraktBus;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.events.TraktItemsUpdatedEvent;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.services.MovieService;
import com.jakewharton.trakt.services.ShowService;
import com.jakewharton.trakt.services.ShowService.EpisodeLibraryBuilder;
import com.jakewharton.trakt.services.ShowService.EpisodeUnlibraryBuilder;

public abstract class InCollectionTask<T extends TraktoidInterface<T>> extends PostTask
{
	protected List<T> traktItems;
	protected boolean addToCollection;

	public InCollectionTask(Context context, List<T> traktItems, boolean addToCollection, PostListener pListener) 
	{
		super(context, null, pListener);

		this.traktItems = traktItems;
		this.addToCollection = addToCollection;
	}
	
	public static <T extends TraktoidInterface<T>> InCollectionTask<?> createTask(Context context, T traktItem, boolean addToCollection, PostListener pListener)
	{
		List<T> traktItems = new ArrayList<T>();
		traktItems.add(traktItem);
		return createTask(context, traktItems, addToCollection, pListener);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TraktoidInterface<T>> InCollectionTask<?> createTask(Context context, List<T> traktItems, boolean addToCollection, PostListener pListener)
	{
		if(traktItems.get(0) instanceof TvShow)
			return new InCollectionShowTask(context, (List<TvShow>) traktItems, addToCollection, pListener);
		else if(traktItems.get(0) instanceof Movie)
			return new InCollectionMovieTask(context, (List<Movie>) traktItems, addToCollection, pListener);
		else if(traktItems.get(0) instanceof TvShowEpisode)
			return new InCollectionEpisodeTask(context, (List<TvShowEpisode>) traktItems, addToCollection, pListener);
		else
			return null;
	}

	protected abstract List<TraktApiBuilder<?>> createLibraryBuilder(List<T> traktItems);
	protected abstract List<TraktApiBuilder<?>> createUnlibraryBuilder(List<T> traktItems);
	protected abstract void insertInDb(List<T> traktItems, boolean addToCollection, DatabaseWrapper dbw);

	@Override
	protected void doPrePostStuff() 
	{
		if(addToCollection)
			builders.addAll(createLibraryBuilder(traktItems));
		else
			builders.addAll(createUnlibraryBuilder(traktItems));
	}

	@Override
	protected void doAfterPostStuff()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		insertInDb(traktItems, addToCollection, dbw);
		dbw.close();
	}

	@Override
	protected void sendEvent(Response result) 
	{
		TraktBus.getInstance().post(new TraktItemsUpdatedEvent<T>(traktItems));
	}

	public static final class InCollectionShowTask extends InCollectionTask<TvShow>
	{
		public InCollectionShowTask(Context context, List<TvShow> traktItems, boolean addToCollection, PostListener pListener) 
		{
			super(context, traktItems, addToCollection, pListener);
		}

		@Override
		protected List<TraktApiBuilder<?>> createLibraryBuilder(List<TvShow> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			for(TvShow traktItem : traktItems)
			{
				ShowService.LibraryBuilder builder = tm.showService().library();
				builder.show(Integer.valueOf(traktItem.getId()));
				builderList.add(builder);
			}
			return builderList;
		}

		@Override
		protected List<TraktApiBuilder<?>> createUnlibraryBuilder(List<TvShow> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			for(TvShow traktItem : traktItems)
			{
				ShowService.UnlibraryBuilder builder = tm.showService().unlibrary(Integer.valueOf(traktItem.getId()));
				builderList.add(builder);
			}
			return builderList;
		}

		@Override
		protected void insertInDb(List<TvShow> traktItems, boolean addToCollection, DatabaseWrapper dbw) 
		{
			for(TvShow traktItem : traktItems)
				dbw.addOrRemoveShowInCollection(traktItem.tvdbId, addToCollection);
		}

	}

	public static final class InCollectionMovieTask extends InCollectionTask<Movie>
	{
		public InCollectionMovieTask(Context context, List<Movie> traktItems, boolean addToCollection, PostListener pListener) 
		{
			super(context, traktItems, addToCollection, pListener);
		}

		@Override
		protected List<TraktApiBuilder<?>> createLibraryBuilder(List<Movie> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			MovieService.LibraryBuilder builder = tm.movieService().library();
			for(Movie traktItem : traktItems)
				builder.movie(traktItem.getId());
			builderList.add(builder);
			return builderList;
		}

		@Override
		protected List<TraktApiBuilder<?>> createUnlibraryBuilder(List<Movie> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			MovieService.UnlibraryBuilder builder = tm.movieService().unlibrary();
			for(Movie traktItem : traktItems)
				builder.movie(traktItem.getId());
			builderList.add(builder);
			return builderList;
		}

		@Override
		protected void insertInDb(List<Movie> traktItems, boolean addToCollection, DatabaseWrapper dbw) 
		{
			for(Movie traktItem : traktItems)
			{
				traktItem.inCollection = addToCollection;
				dbw.insertOrUpdateMovie(traktItem);
			}
		}

	}

	public static final class InCollectionEpisodeTask extends InCollectionTask<TvShowEpisode>
	{
		public InCollectionEpisodeTask(Context context, List<TvShowEpisode> traktItems, boolean addToCollection, PostListener pListener) 
		{
			super(context, traktItems, addToCollection, pListener);
		}
		
		public static InCollectionEpisodeTask createSeasonTask(Context context, List<TvShowSeason> traktItems, boolean seen, PostListener pListener)
		{
			List<TvShowEpisode> episodes = new ArrayList<TvShowEpisode>();
			DatabaseWrapper dbw = new DatabaseWrapper(context);
			for(TvShowSeason s : traktItems)
				episodes.addAll(dbw.getEpisodes(s.url));
			dbw.close();

			return new InCollectionEpisodeTask(context, episodes, seen, pListener);
		}

		@Override
		protected List<TraktApiBuilder<?>> createLibraryBuilder(List<TvShowEpisode> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			EpisodeLibraryBuilder builder = null;
			for(TvShowEpisode traktItem : traktItems)
			{
				if(builder == null)
					builder = tm.showService().episodeLibrary(Integer.valueOf(traktItem.tvdbId));
				builder.episode(traktItem.season, traktItem.number);
			}
			builderList.add(builder);
			return builderList;
		}

		@Override
		protected List<TraktApiBuilder<?>> createUnlibraryBuilder(List<TvShowEpisode> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			EpisodeUnlibraryBuilder builder = null;
			for(TvShowEpisode traktItem : traktItems)
			{
				if(builder == null)
					builder = tm.showService().episodeUnlibrary(Integer.valueOf(traktItem.tvdbId));
				builder.episode(traktItem.season, traktItem.number);
			}
			builderList.add(builder);
			return builderList;
		}

		@Override
		protected void insertInDb(List<TvShowEpisode> traktItems, boolean addToCollection, DatabaseWrapper dbw) 
		{
			for(TvShowEpisode traktItem : traktItems)
			{
				traktItem.inCollection = addToCollection;
				dbw.insertOrUpdateEpisode(traktItem);
			}
		}
	}
}