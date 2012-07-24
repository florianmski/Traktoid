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
import com.jakewharton.trakt.enumerations.Rating;
import com.jakewharton.trakt.services.RateService;

public abstract class RateTask<T extends TraktoidInterface<T>> extends PostTask
{
	protected List<T> traktItems;
	protected Rating rating;

	public RateTask(Activity context, List<T> traktItems, Rating rating, PostListener pListener) 
	{
		super(context, null, pListener);

		this.traktItems = traktItems;
		this.rating = rating;
	}

	public static <T extends TraktoidInterface<T>> RateTask<?> createTask(Activity context, T traktItem, Rating r, PostListener pListener)
	{
		List<T> traktItems = new ArrayList<T>();
		traktItems.add(traktItem);
		return createTask(context, traktItems, r, pListener);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TraktoidInterface<T>> RateTask<?> createTask(Activity context, List<T> traktItems, Rating r, PostListener pListener)
	{
		if(traktItems.get(0) instanceof TvShow)
			return new RateShowTask(context, (List<TvShow>) traktItems, r, pListener);
		else if(traktItems.get(0) instanceof Movie)
			return new RateMovieTask(context, (List<Movie>) traktItems, r, pListener);
		else if(traktItems.get(0) instanceof TvShowEpisode)
			return new RateEpisodeTask(context, (List<TvShowEpisode>) traktItems, r, pListener);
		else
			return null;
	}

	protected abstract List<TraktApiBuilder<?>> createRateBuilder(List<T> traktItems);
	protected abstract void insertInDb(List<T> traktItems, Rating rating, DatabaseWrapper dbw);

	@Override
	protected void doPrePostStuff() 
	{
		builders.addAll(createRateBuilder(traktItems));
	}

	@Override
	protected void doAfterPostStuff()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		insertInDb(traktItems, rating, dbw);
		dbw.close();
	}

	@Override
	protected void sendEvent(Response result) 
	{
		TraktBus.getInstance().post(new TraktItemsUpdatedEvent<T>(traktItems));
	}

	public static final class RateShowTask extends RateTask<TvShow>
	{
		public RateShowTask(Activity context, List<TvShow> traktItem, Rating rating, PostListener pListener) 
		{
			super(context, traktItem, rating, pListener);
		}

		@Override
		protected List<TraktApiBuilder<?>> createRateBuilder(List<TvShow> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			for(TvShow traktItem : traktItems)
			{
				RateService.ShowBuilder builder = tm.rateService().show(Integer.valueOf(traktItem.tvdbId)).rating(rating);
				builderList.add(builder);
			}
			return builderList;
		}

		@Override
		protected void insertInDb(List<TvShow> traktItems, Rating rating, DatabaseWrapper dbw) 
		{
			for(TvShow traktItem : traktItems)
			{
				traktItem.rating = rating;
				dbw.insertOrUpdateShow(traktItem);
			}
		}
	}

	public static final class RateMovieTask extends RateTask<Movie>
	{
		public RateMovieTask(Activity context, List<Movie> traktItems, Rating rating, PostListener pListener) 
		{
			super(context, traktItems, rating, pListener);
		}

		@Override
		protected List<TraktApiBuilder<?>> createRateBuilder(List<Movie> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			for(Movie traktItem : traktItems)
			{
				RateService.MovieBuilder builder = tm.rateService().movie(traktItem.imdbId).rating(rating);
				builderList.add(builder);
			}
			return builderList;
		}

		@Override
		protected void insertInDb(List<Movie> traktItems, Rating rating, DatabaseWrapper dbw) 
		{
			for(Movie traktItem : traktItems)
			{
				traktItem.rating = rating;
				dbw.insertOrUpdateMovie(traktItem);
			}
		}
	}

	public static final class RateEpisodeTask extends RateTask<TvShowEpisode>
	{
		public RateEpisodeTask(Activity context, List<TvShowEpisode> traktItems, Rating rating, PostListener pListener) 
		{
			super(context, traktItems, rating, pListener);
		}

		@Override
		protected List<TraktApiBuilder<?>> createRateBuilder(List<TvShowEpisode> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			for(TvShowEpisode traktItem : traktItems)
			{
				RateService.EpisodeBuilder builder = 
						tm
						.rateService()
						.episode(Integer.valueOf(traktItem.tvdbId))
						.episode(traktItem.number)
						.season(traktItem.season)
						.rating(rating);
				builderList.add(builder);
			}
			return builderList;
		}

		@Override
		protected void insertInDb(List<TvShowEpisode> traktItems, Rating rating, DatabaseWrapper dbw) 
		{
			for(TvShowEpisode traktItem : traktItems)
			{
				traktItem.rating = rating;
				dbw.insertOrUpdateEpisode(traktItem);
			}
		}
	}

}
