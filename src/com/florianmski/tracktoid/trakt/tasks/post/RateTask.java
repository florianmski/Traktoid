package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.enumerations.Rating;

public abstract class RateTask<T extends TraktoidInterface> extends PostTask
{
	//TODO new 10 rating
	
	protected T traktItem;
	protected Rating rating;

	public RateTask(TraktManager tm, Fragment fragment, T traktItem, Rating rating, PostListener pListener) 
	{
		super(tm, fragment, null, pListener);

		this.traktItem = traktItem;
		this.rating = rating;
	}

	public static <T extends TraktoidInterface> RateTask<?> createTask(TraktManager tm, Fragment fragment, T traktItem, Rating rating, PostListener pListener)
	{
		if(traktItem instanceof TvShow)
			return new RateShowTask(tm, fragment, (TvShow) traktItem, rating, pListener);
		else if(traktItem instanceof Movie)
			return new RateMovieTask(tm, fragment, (Movie) traktItem, rating, pListener);
		else if(traktItem instanceof TvShowEpisode)
			return new RateEpisodeTask(tm, fragment, (TvShowEpisode) traktItem, rating, pListener);
		else
			return null;
	}

	protected abstract TraktApiBuilder<?> createRateBuilder(T traktItem);
	protected abstract void insertInDb(T traktItem, Rating rating);
	protected abstract void sendEvent(T traktItem);

	@Override
	protected void doPrePostStuff() 
	{
		builders.add(createRateBuilder(traktItem));
	}

	@Override
	protected void doAfterPostStuff()
	{
		insertInDb(traktItem, rating);
	}

	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(success)
			sendEvent(traktItem);
	}
	
	public static final class RateShowTask extends RateTask<TvShow>
	{
		public RateShowTask(TraktManager tm, Fragment fragment,	TvShow traktItem, Rating rating, PostListener pListener) 
		{
			super(tm, fragment, traktItem, rating, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createRateBuilder(TvShow traktItem) 
		{
			return tm
					.rateService()
					.show(Integer.valueOf(traktItem.tvdbId))
					.rating(rating);
		}

		@Override
		protected void sendEvent(TvShow traktItem) 
		{
			tm.onShowUpdated(traktItem);
		}

		@Override
		protected void insertInDb(TvShow traktItem, Rating rating) 
		{
			traktItem.rating = rating;
			
			DatabaseWrapper dbw = new DatabaseWrapper(context);
			dbw.insertOrUpdateShow(traktItem);
			dbw.close();
		}
	}
	
	public static final class RateMovieTask extends RateTask<Movie>
	{
		public RateMovieTask(TraktManager tm, Fragment fragment, Movie traktItem, Rating rating, PostListener pListener) 
		{
			super(tm, fragment, traktItem, rating, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createRateBuilder(Movie traktItem) 
		{
			return tm
					.rateService()
					.movie(traktItem.imdbId)
					.rating(rating);
		}

		@Override
		protected void sendEvent(Movie traktItem) 
		{
			tm.onMovieUpdated(traktItem);
		}

		@Override
		protected void insertInDb(Movie traktItem, Rating rating) 
		{
			traktItem.rating = rating;
			
			DatabaseWrapper dbw = new DatabaseWrapper(context);
			dbw.insertOrUpdateMovie(traktItem);
			dbw.close();
		}
	}
	
	public static final class RateEpisodeTask extends RateTask<TvShowEpisode>
	{
		public RateEpisodeTask(TraktManager tm, Fragment fragment, TvShowEpisode traktItem, Rating rating, PostListener pListener) 
		{
			super(tm, fragment, traktItem, rating, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createRateBuilder(TvShowEpisode traktItem) 
		{
			return tm
					.rateService()
					.episode(Integer.valueOf(traktItem.tvdbId))
					.episode(traktItem.number)
					.season(traktItem.season)
					.rating(rating);
		}

		@Override
		protected void sendEvent(TvShowEpisode traktItem) 
		{
			//TODO
//			tm.onMovieUpdated(traktItem);
		}

		@Override
		protected void insertInDb(TvShowEpisode traktItem, Rating rating) 
		{
			traktItem.rating = rating;
			
			DatabaseWrapper dbw = new DatabaseWrapper(context);
			dbw.insertOrUpdateEpisode(traktItem);
			dbw.close();
		}
	}

}
