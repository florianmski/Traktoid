package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShowEpisode;

public abstract class CheckinPostTask<T extends TraktoidInterface> extends PostTask
{
	protected T traktItem;
	protected boolean checkin;

	public CheckinPostTask(TraktManager tm, Fragment fragment, T traktItem, boolean checkin, PostListener pListener) 
	{
		super(tm, fragment, null, pListener);

		this.traktItem = traktItem;
		this.checkin = checkin;
	}
	
	public static <T extends TraktoidInterface> CheckinPostTask<?> createTask(TraktManager tm, Fragment fragment, T traktItem, boolean checkin, PostListener pListener)
	{
		if(traktItem instanceof Movie)
			return new CheckinMovieTask(tm, fragment, (Movie) traktItem, checkin, pListener);
		else if(traktItem instanceof TvShowEpisode)
			return new CheckinEpisodeTask(tm, fragment, (TvShowEpisode) traktItem, checkin, pListener);
		else
			return null;
	}

	protected abstract TraktApiBuilder<?> createCheckinBuilder(T traktItem);
	protected abstract TraktApiBuilder<?> createUncheckinBuilder(T traktItem);
	protected abstract void insertInDb(T traktItem, boolean addToCollection, DatabaseWrapper dbw);
	protected abstract void sendEvent(T traktItem);
	
	@Override
	protected void doPrePostStuff() 
	{
		if(checkin)
			builders.add(createCheckinBuilder(traktItem));
		else
			builders.add(createUncheckinBuilder(traktItem));
	}
	
	@Override
	protected void doAfterPostStuff()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		insertInDb(traktItem, checkin, dbw);
		dbw.close();
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(success)
			sendEvent(traktItem);			
	}
	
	public static final class CheckinMovieTask extends CheckinPostTask<Movie>
	{
		public CheckinMovieTask(TraktManager tm, Fragment fragment, Movie traktItem, boolean checkin, PostListener pListener) 
		{
			super(tm, fragment, traktItem, checkin, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createCheckinBuilder(Movie traktItem) 
		{
			return tm
					.movieService()
					.checking(traktItem.getId());
		}

		@Override
		protected TraktApiBuilder<?> createUncheckinBuilder(Movie traktItem) 
		{
			return tm
					.movieService()
					.cancelCheckin();
		}

		@Override
		protected void sendEvent(Movie traktItem) 
		{
			tm.onMovieUpdated(traktItem);
		}
		
		@Override
		protected void insertInDb(Movie traktItem, boolean checkin, DatabaseWrapper dbw) 
		{
			traktItem.watched = checkin;
			
			dbw.insertOrUpdateMovie(traktItem);
		}
	}
	
	public static final class CheckinEpisodeTask extends CheckinPostTask<TvShowEpisode>
	{
		public CheckinEpisodeTask(TraktManager tm, Fragment fragment, TvShowEpisode traktItem, boolean checkin, PostListener pListener) 
		{
			super(tm, fragment, traktItem, checkin, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createCheckinBuilder(TvShowEpisode traktItem) 
		{
			return tm
					.showService()
					.checkin(Integer.valueOf(traktItem.tvdbId))
					.episode(traktItem.number)
					.season(traktItem.season);
		}

		@Override
		protected TraktApiBuilder<?> createUncheckinBuilder(TvShowEpisode traktItem) 
		{
			return tm
					.showService()
					.cancelCheckin();
		}

		@Override
		protected void sendEvent(TvShowEpisode traktItem) 
		{
			//TODO
//			tm.onMovieUpdated(traktItem);
		}
		
		@Override
		protected void insertInDb(TvShowEpisode traktItem, boolean checkin, DatabaseWrapper dbw) 
		{
			traktItem.watched = checkin;
			
			dbw.insertOrUpdateEpisode(traktItem);
		}
	}
}
