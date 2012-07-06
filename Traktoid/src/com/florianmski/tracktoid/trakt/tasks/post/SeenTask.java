package com.florianmski.tracktoid.trakt.tasks.post;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.services.MovieService;
import com.jakewharton.trakt.services.MovieService.UnseenBuilder;
import com.jakewharton.trakt.services.ShowService;
import com.jakewharton.trakt.services.ShowService.EpisodeSeenBuilder;
import com.jakewharton.trakt.services.ShowService.EpisodeUnseenBuilder;

public abstract class SeenTask<T extends TraktoidInterface<T>> extends PostTask
{
	protected List<T> traktItems;
	protected boolean seen;

	public SeenTask(Activity context, List<T> traktItems, boolean seen, PostListener pListener) 
	{
		super(context, null, pListener);

		this.traktItems = traktItems;
		this.seen = seen;
	}

	public static <T extends TraktoidInterface<T>> SeenTask<?> createTask(Activity context, T traktItem, boolean seen, PostListener pListener)
	{
		List<T> traktItems = new ArrayList<T>();
		traktItems.add(traktItem);
		return createTask(context, traktItems, seen, pListener);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TraktoidInterface<T>> SeenTask<?> createTask(Activity context, List<T> traktItems, boolean seen, PostListener pListener)
	{
		if(traktItems.get(0) instanceof TvShow)
			return new SeenShowTask(context, (List<TvShow>) traktItems, seen, pListener);
		else if(traktItems.get(0) instanceof Movie)
			return new SeenMovieTask(context, (List<Movie>) traktItems, seen, pListener);
		else if(traktItems.get(0) instanceof TvShowEpisode)
			return new SeenEpisodeTask(context, (List<TvShowEpisode>) traktItems, seen, pListener);
		else
			return null;
	}

	protected abstract List<TraktApiBuilder<?>> createSeenBuilder(List<T> traktItems);
	protected abstract List<TraktApiBuilder<?>> createUnseenBuilder(List<T> traktItems);
	protected abstract void insertInDb(List<T> traktItems, DatabaseWrapper dbw);

	@Override
	protected void doPrePostStuff() 
	{
		if(seen)
			builders.addAll(createSeenBuilder(traktItems));
		else
			builders.addAll(createUnseenBuilder(traktItems));
	}

	@Override
	protected void doAfterPostStuff()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		insertInDb(traktItems, dbw);
		dbw.close();
	}

	@Override
	protected void sendEvent(Response result) 
	{
		TraktTask.traktItemsUpdated(traktItems);
	}

	public static final class SeenShowTask extends SeenTask<TvShow>
	{
		public SeenShowTask(Activity context, List<TvShow> traktItems, boolean seen, PostListener pListener) 
		{
			super(context, traktItems, seen, pListener);
		}

		@Override
		protected List<TraktApiBuilder<?>> createSeenBuilder(List<TvShow> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			for (TvShow show : traktItems)
			{
				ShowService.SeenBuilder seenBuilder = tm.showService().seen();
				seenBuilder.show(Integer.valueOf(show.getId()));
				builderList.add(seenBuilder);
			}

			return builderList;
		}

		@Override
		protected List<TraktApiBuilder<?>> createUnseenBuilder(List<TvShow> traktItems) 
		{
			//TODO this method does not exists in API
			return null;
		}

		@Override
		protected void insertInDb(List<TvShow> traktItems, DatabaseWrapper dbw) 
		{
			for(TvShow traktItem : traktItems)
				dbw.markShowAsSeen(traktItem.tvdbId, seen);
		}
	}

	public static final class SeenMovieTask extends SeenTask<Movie>
	{
		public SeenMovieTask(Activity context, List<Movie> traktItems, boolean seen, PostListener pListener) 
		{
			super(context, traktItems, seen, pListener);
		}

		@Override
		protected List<TraktApiBuilder<?>> createSeenBuilder(List<Movie> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			MovieService.SeenBuilder seenBuilder = tm.movieService().seen();
			for (Movie movie : traktItems)
				seenBuilder.movie(movie.getId());

			builderList.add(seenBuilder);
			return builderList;
		}

		@Override
		protected List<TraktApiBuilder<?>> createUnseenBuilder(List<Movie> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			UnseenBuilder unseenBuilder = tm.movieService().unseen();
			for (Movie movie : traktItems)
				unseenBuilder.movie(movie.getId());

			builderList.add(unseenBuilder);
			return builderList;
		}

		@Override
		protected void insertInDb(List<Movie> traktItems, DatabaseWrapper dbw) 
		{
			for(Movie traktItem : traktItems)
			{
				traktItem.watched = seen;
				dbw.insertOrUpdateMovie(traktItem);
			}
		}
	}

	public static final class SeenEpisodeTask extends SeenTask<TvShowEpisode>
	{
		public SeenEpisodeTask(Activity context, List<TvShowEpisode> traktItems, boolean seen, PostListener pListener) 
		{
			super(context, traktItems, seen, pListener);
		}

		public static SeenEpisodeTask createSeasonTask(Activity context, List<TvShowSeason> traktItems, boolean seen, PostListener pListener)
		{
			List<TvShowEpisode> episodes = new ArrayList<TvShowEpisode>();
			for(TvShowSeason s : traktItems)
			{
				for(TvShowEpisode e : s.episodes.episodes)
					episodes.add(e);
			}

			return new SeenEpisodeTask(context, episodes, seen, pListener);
		}

		@Override
		protected List<TraktApiBuilder<?>> createSeenBuilder(List<TvShowEpisode> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			EpisodeSeenBuilder seenBuilder = null;
			for(TvShowEpisode episode : traktItems)
			{
				if(seenBuilder == null)
					seenBuilder = tm.showService().episodeSeen(Integer.valueOf(episode.tvdbId));
				seenBuilder.episode(episode.season, episode.number);
			}
			builderList.add(seenBuilder);
			return builderList;
		}

		@Override
		protected List<TraktApiBuilder<?>> createUnseenBuilder(List<TvShowEpisode> traktItems) 
		{
			List<TraktApiBuilder<?>> builderList = new ArrayList<TraktApiBuilder<?>>();
			EpisodeUnseenBuilder unseenBuilder = null;
			for(TvShowEpisode episode : traktItems)
			{
				if(unseenBuilder == null)
					unseenBuilder = tm.showService().episodeUnseen(Integer.valueOf(episode.tvdbId));
				unseenBuilder.episode(episode.season, episode.number);
			}
			builderList.add(unseenBuilder);
			return builderList;
		}

		@Override
		protected void insertInDb(List<TvShowEpisode> traktItems, DatabaseWrapper dbw) 
		{
			for(TvShowEpisode traktItem : traktItems)
			{
				traktItem.watched = seen;
				dbw.insertOrUpdateEpisode(traktItem);
			}
		}
	}
}