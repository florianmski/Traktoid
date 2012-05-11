package com.florianmski.tracktoid.trakt.tasks.post;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.services.MovieService.UnseenBuilder;
import com.jakewharton.trakt.services.ShowService.EpisodeSeenBuilder;
import com.jakewharton.trakt.services.ShowService.EpisodeUnseenBuilder;

public abstract class SeenTask<T extends TraktoidInterface<T>> extends PostTask
{
	protected Map<T,Boolean> traktItems;

	public SeenTask(TraktManager tm, Fragment fragment, Map<T,Boolean> traktItems, PostListener pListener) 
	{
		super(tm, fragment, null, pListener);

		this.traktItems = traktItems;
	}
	
	public static <T extends TraktoidInterface<T>> SeenTask<?> createTask(TraktManager tm, Fragment fragment, T traktItem, boolean seen, PostListener pListener)
	{
		Map<T,Boolean> traktItems = new HashMap<T, Boolean>();
		traktItems.put(traktItem, seen);
		return createTask(tm, fragment, traktItems, pListener);
	}

	public static <T extends TraktoidInterface<T>> SeenTask<?> createTask(TraktManager tm, Fragment fragment, Map<T,Boolean> traktItems, PostListener pListener)
	{
		//TODO
		if(traktItems.keySet().iterator().next() instanceof TvShow)
			return new SeenShowTask(tm, fragment, (Map<TvShow, Boolean>) traktItems, pListener);
		else if(traktItems.keySet().iterator().next() instanceof Movie)
			return new SeenMovieTask(tm, fragment, (Map<Movie, Boolean>) traktItems, pListener);
		else if(traktItems.keySet().iterator().next() instanceof TvShowEpisode)
			return new SeenEpisodeTask(tm, fragment, (Map<TvShowEpisode, Boolean>) traktItems, pListener);
		else
			return null;
	}

	protected abstract TraktApiBuilder<?> createSeenBuilder(Map<T,Boolean> traktItems);
	protected abstract TraktApiBuilder<?> createUnseenBuilder(Map<T,Boolean> traktItems);
	protected abstract void insertInDb(Map<T,Boolean> traktItems);
	protected abstract void sendEvent(Map<T,Boolean> traktItems);

	@Override
	protected void doPrePostStuff() 
	{
		TraktApiBuilder<?> seenBuilder = createSeenBuilder(traktItems);
		if(seenBuilder != null)
			builders.add(seenBuilder);
		TraktApiBuilder<?> unseenBuilder = createUnseenBuilder(traktItems);
		if(unseenBuilder != null)
			builders.add(unseenBuilder);
	}

	@Override
	protected void doAfterPostStuff()
	{
		insertInDb(traktItems);
	}

	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(success)
			sendEvent(traktItems);
		//			tm.onTraktItemUpdated(traktItem);

	}

	public static final class SeenShowTask extends SeenTask<TvShow>
	{
		public SeenShowTask(TraktManager tm, Fragment fragment, Map<TvShow,Boolean> traktItems, PostListener pListener) 
		{
			super(tm, fragment, traktItems, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createSeenBuilder(Map<TvShow,Boolean> traktItems) 
		{
			//TODO seen builder in trakt-java
			//			EpisodeSeenBuilder seenBuilder = tm.showService().episodeSeen(Integer.valueOf(0)).episode(0, 0).;
			//			for (Iterator<Integer> it = listEpisodes.keySet().iterator(); it.hasNext() ;)
			//			{
			//				Integer episode = it.next();
			//				Boolean watched = listEpisodes.get(episode);
			//
			//				if(watched)
			//				{
			//					seenEpisodes++;
			//					seenBuilder.episode(seasons[i], episode);
			//				}
			//				else
			//				{
			//					unseenEpisodes++;
			//					unseenBuilder.episode(seasons[i], episode);
			//				}
			//			}
			return null;
		}

		@Override
		protected TraktApiBuilder<?> createUnseenBuilder(Map<TvShow,Boolean> traktItems) 
		{
			//TODO seen builder in trakt-java
			return null;
		}

		@Override
		protected void sendEvent(Map<TvShow,Boolean> traktItems) 
		{
			//TODO
		}

		@Override
		protected void insertInDb(Map<TvShow,Boolean> traktItems) 
		{
			//TODO
		}
	}

	public static final class SeenMovieTask extends SeenTask<Movie>
	{
		public SeenMovieTask(TraktManager tm, Fragment fragment, Map<Movie,Boolean> traktItems, PostListener pListener) 
		{
			super(tm, fragment, traktItems, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createSeenBuilder(Map<Movie,Boolean> traktItems) 
		{
			//TODO wait for justin response https://groups.google.com/forum/?fromgroups#!topic/traktapi/EI0xt5DQaXM
			//			return tm
			//					.movieService()
			//					.seen().movie(0, 0, new Date()).checkin(Integer.valueOf(traktItem.getId()));
			return null;
		}

		@Override
		protected TraktApiBuilder<?> createUnseenBuilder(Map<Movie,Boolean> traktItems) 
		{
			UnseenBuilder unseenBuilder = tm.movieService().unseen();
			for (Iterator<Movie> it = traktItems.keySet().iterator(); it.hasNext() ;)
			{
				Movie movie = it.next();
				if(!traktItems.get(movie))
					unseenBuilder.movie(movie.getId());
			}
			return unseenBuilder;
		}

		@Override
		protected void sendEvent(Map<Movie,Boolean> traktItems) 
		{
			//TODO
		}

		@Override
		protected void insertInDb(Map<Movie,Boolean> traktItems) 
		{
			//TODO
		}
	}

	public static final class SeenEpisodeTask extends SeenTask<TvShowEpisode>
	{
		public SeenEpisodeTask(TraktManager tm, Fragment fragment, Map<TvShowEpisode,Boolean> traktItems, PostListener pListener) 
		{
			super(tm, fragment, traktItems, pListener);
		}

		@Override
		protected TraktApiBuilder<?> createSeenBuilder(Map<TvShowEpisode,Boolean> traktItems) 
		{
			EpisodeSeenBuilder seenBuilder = null;
			for (Iterator<TvShowEpisode> it = traktItems.keySet().iterator(); it.hasNext() ;)
			{
				TvShowEpisode episode = it.next();
				if(traktItems.get(episode))
				{
					if(seenBuilder == null)
						seenBuilder = tm.showService().episodeSeen(Integer.valueOf(episode.tvdbId));
					seenBuilder.episode(episode.season, episode.number);
				}
			}
			return seenBuilder;
		}

		@Override
		protected TraktApiBuilder<?> createUnseenBuilder(Map<TvShowEpisode,Boolean> traktItems) 
		{
			EpisodeUnseenBuilder unseenBuilder = null;
			for (Iterator<TvShowEpisode> it = traktItems.keySet().iterator(); it.hasNext() ;)
			{
				TvShowEpisode episode = it.next();
				if(!traktItems.get(episode))
				{
					if(unseenBuilder == null)
						unseenBuilder = tm.showService().episodeUnseen(Integer.valueOf(episode.tvdbId));
					unseenBuilder.episode(episode.season, episode.number);
				}
			}
			return unseenBuilder;
		}

		@Override
		protected void sendEvent(Map<TvShowEpisode,Boolean> traktItems) 
		{
			//TODO
		}

		@Override
		protected void insertInDb(Map<TvShowEpisode,Boolean> traktItems) 
		{
			//TODO
		}
	}
}