package com.florianmski.tracktoid.trakt.tasks.post;

import android.content.Context;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.enumerations.Rating;

public abstract class RateTask<T extends TraktoidInterface<T>> extends PostTask
{
	//TODO new 10 rating
	
	protected T traktItem;
	protected Rating rating;

	public RateTask(Context context, T traktItem, Rating rating, PostListener pListener) 
	{
		super(context, null, pListener);

		this.traktItem = traktItem;
		this.rating = rating;
	}

	public static <T extends TraktoidInterface<T>> RateTask<?> createTask(Context context, T traktItem, Rating rating, PostListener pListener)
	{
		if(traktItem instanceof TvShow)
			return new RateShowTask(context, (TvShow) traktItem, rating, pListener);
		else if(traktItem instanceof Movie)
			return new RateMovieTask(context, (Movie) traktItem, rating, pListener);
		else if(traktItem instanceof TvShowEpisode)
			return new RateEpisodeTask(context, (TvShowEpisode) traktItem, rating, pListener);
		else
			return null;
	}

	protected abstract TraktApiBuilder<?> createRateBuilder(T traktItem);
	protected abstract void insertInDb(T traktItem, Rating rating);

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
	protected void sendEvent(Response result) 
	{
		TraktTask.traktItemUpdated(traktItem);
	}
	
	public static final class RateShowTask extends RateTask<TvShow>
	{
		public RateShowTask(Context context,	TvShow traktItem, Rating rating, PostListener pListener) 
		{
			super(context, traktItem, rating, pListener);
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
		public RateMovieTask(Context context, Movie traktItem, Rating rating, PostListener pListener) 
		{
			super(context, traktItem, rating, pListener);
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
		public RateEpisodeTask(Context context, TvShowEpisode traktItem, Rating rating, PostListener pListener) 
		{
			super(context, traktItem, rating, pListener);
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
		protected void insertInDb(TvShowEpisode traktItem, Rating rating) 
		{
			traktItem.rating = rating;
			
			DatabaseWrapper dbw = new DatabaseWrapper(context);
			dbw.insertOrUpdateEpisode(traktItem);
			dbw.close();
		}
	}

}
