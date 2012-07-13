package com.florianmski.tracktoid.trakt.tasks.post;

import android.app.Activity;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShowEpisode;

public abstract class CheckinPostTask<T extends TraktoidInterface<T>> extends PostTask
{
	//TODO add social networks

	protected T traktItem;
	protected boolean checkin;
	protected boolean facebook = false;
	protected boolean twitter = false; 
	protected boolean tumblr = false; 
	protected boolean foursquare = false;
	protected String message = "";

	public CheckinPostTask(Activity context, T traktItem, boolean checkin, PostListener pListener) 
	{
		super(context, null, pListener);

		this.traktItem = traktItem;
		this.checkin = checkin;
	}

	public static <T extends TraktoidInterface<T>> CheckinPostTask<?> createTask(Activity context, T traktItem, boolean checkin, PostListener pListener)
	{
		if(traktItem instanceof Movie)
			return new CheckinMovieTask(context, (Movie) traktItem, checkin, pListener);
		else if(traktItem instanceof TvShowEpisode)
			return new CheckinEpisodeTask(context, (TvShowEpisode) traktItem, checkin, pListener);
		else
			return null;
	}

	public CheckinPostTask<T> share(boolean facebook, boolean twitter, boolean tumblr, boolean foursquare)
	{
		this.facebook = facebook;
		this.twitter = twitter;
		this.tumblr = tumblr;
		this.foursquare = foursquare;
		return this;
	}
	
	public CheckinPostTask<T> message(String message)
	{
		this.message = message;
		return this;
	}

	protected abstract TraktApiBuilder<?> createCheckinBuilder(T traktItem);
	protected abstract TraktApiBuilder<?> createUncheckinBuilder(T traktItem);
	protected abstract void insertInDb(T traktItem, boolean addToCollection, DatabaseWrapper dbw);

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
	protected void sendEvent(Response result) 
	{
		TraktTask.traktItemUpdated(traktItem);
	}

	public static final class CheckinMovieTask extends CheckinPostTask<Movie>
	{
		public CheckinMovieTask(Activity context, Movie traktItem, boolean checkin, PostListener pListener) 
		{
			super(context, traktItem, checkin, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createCheckinBuilder(Movie traktItem) 
		{
			return tm
					.movieService()
					.checkin(traktItem.getId())
					.share(facebook, twitter, tumblr, foursquare)
					.message(message);
		}

		@Override
		protected TraktApiBuilder<?> createUncheckinBuilder(Movie traktItem) 
		{
			return tm
					.movieService()
					.cancelCheckin();
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
		public CheckinEpisodeTask(Activity context, TvShowEpisode traktItem, boolean checkin, PostListener pListener) 
		{
			super(context, traktItem, checkin, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createCheckinBuilder(TvShowEpisode traktItem) 
		{
			return tm
					.showService()
					.checkin(Integer.valueOf(traktItem.tvdbId))
					.episode(traktItem.number)
					.season(traktItem.season)
					.share(facebook, twitter, tumblr, foursquare)
					.message(message);
		}

		@Override
		protected TraktApiBuilder<?> createUncheckinBuilder(TvShowEpisode traktItem) 
		{
			return tm
					.showService()
					.cancelCheckin();
		}

		@Override
		protected void insertInDb(TvShowEpisode traktItem, boolean checkin, DatabaseWrapper dbw) 
		{
			traktItem.watched = checkin;

			dbw.insertOrUpdateEpisode(traktItem);
		}
	}
}
