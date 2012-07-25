package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.florianmski.tracktoid.TraktBus;
import com.florianmski.tracktoid.TraktItemsUpdatedEvent;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.jakewharton.trakt.entities.Activity;
import com.jakewharton.trakt.entities.ActivityItemBase;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class SynchronizationTask extends BaseTask<Activity>
{
	private Activity activities;

	private DatabaseWrapper dbw;
	private SharedPreferences prefs;

	//shows we'll have to refresh (ex: show or an episode of a show which is not in the db)
	private List<TvShow> traktUpdateShowList = new ArrayList<TvShow>();
	//shows we'll update (temp list)
	private List<TvShow> localUpdateShowList = new ArrayList<TvShow>();
	//shows we'll update
	private List<TvShow> finalUpdateShowList = new ArrayList<TvShow>();

	//movies we'll have to refresh (ex: a movie which is not in the db)
	private List<Movie> traktUpdateMovieList = new ArrayList<Movie>();
	//movies we'll update (temp list)
	private List<Movie> localUpdateMovieList = new ArrayList<Movie>();
	//movies we'll update
	private List<Movie> finalUpdateMovieList = new ArrayList<Movie>();

	//episodes we'll update (temp list)
	private List<TvShowEpisode> localUpdateEpisodeList = new ArrayList<TvShowEpisode>();
	//episodes we'll update
	private List<TvShowEpisode> finalUpdateEpisodeList = new ArrayList<TvShowEpisode>();

	public SynchronizationTask(android.app.Activity context) 
	{
		super(context, sSingleThreadExecutor);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected Activity doTraktStuffInBackground()
	{
		showToast("Synchronization...", Toast.LENGTH_SHORT);

		long timestamp = prefs.getLong("activity_timestamp", 0);
		Log.e("start timestamp", new Date(timestamp*1000).toString());
		Log.e("start timestamp",timestamp+" coucou");

		activities = 
				tm
				.activityService()
				.user(TraktManager.getUsername())
				.timestamp(timestamp)
				.fire();

		if(activities != null && activities.activity != null)
		{
			Collections.reverse(activities.activity);

			dbw = new DatabaseWrapper(context);

			for(ActivityItemBase activity : activities.activity)
			{
				Log.e("activity timestamp",activity.timestamp.toString());
				//				if(Utils.getPSTTimestamp(activity.timestamp.getTime()) < timestamp)
				//					continue;

				switch(activity.type)
				{
				case Episode :
					switch(activity.action)
					{
					case Checkin :
					case Scrobble :
					case Watching :
						activity.episode.watched = true;
						updateEpisode(activity.show, activity.episode);
						break;
					case Seen :
					case Collection :
						for(TvShowEpisode episode : activity.episodes)
						{
							switch(activity.action)
							{
							case Seen :
								episode.watched = true;
								break;
							case Collection :
								episode.inCollection = true;
								break;
							case Watchlist :
								episode.inWatchlist = true;
								break;
							case Rating :
								episode.rating = activity.rating;
								break;
							default:
								break;
							}
							updateEpisode(activity.show, episode);
						}
						break;
					case Watchlist :
						activity.episode.inWatchlist = true;
						updateEpisode(activity.show, activity.episode);
						break;
					case Rating :
						activity.episode.rating = activity.rating;
						updateEpisode(activity.show, activity.episode);
						break;
					default:
						break;
					}
					break;
				case Show :
					switch(activity.action)
					{
					case Rating :
						activity.show.rating = activity.rating;
						break;
					case Watchlist :
						activity.show.inWatchlist = true;
						break;
					default:
						break;
					}
					updateShow(activity.show);
					break;
				case Movie :
					switch(activity.action)
					{
					case Rating :
						activity.movie.rating = activity.rating;
						break;
					case Watchlist :
						activity.movie.inWatchlist = true;
						break;
					case Collection :
						activity.movie.inCollection = true;
						break;
					case Checkin :
					case Scrobble :
					case Watching :
					case Seen :
						activity.movie.watched = true;
						break;
					default:
						break;
					}
					updateMovie(activity.movie);
					break;
				default:
					break;
				}
			}

			for(TvShow show : localUpdateShowList)
			{
				dbw.insertOrUpdateShow(show);
				dbw.refreshPercentage(show.tvdbId);
				show = dbw.getShow(show.tvdbId);
				finalUpdateShowList.add(show);
			}

			for(TvShowEpisode episode : localUpdateEpisodeList)
			{
				episode = dbw.getEpisode(episode.url);
				finalUpdateEpisodeList.add(episode);
			}

		}

		//TODO take a lot of time & memory
		//if timestamp == 0 the json we'll be incredibly huge
//		if(timestamp != 0)
//		{
//			Update u = tm.updateService().shows().timestamp(timestamp).fire();
//			for(TvShow s : u.shows)
//			{
//				if(dbw.showExist(s.tvdbId))
//					add(traktUpdateShowList, s);
//			}
//
//			u = tm.updateService().movies().timestamp(timestamp).fire();
//			for(Movie m : u.movies)
//			{
//				if(dbw.movieExist(m.imdbId))
//					add(traktUpdateMovieList, m);
//			}
//		}

		dbw.close();

		showToast("Sync done!", Toast.LENGTH_SHORT);
		prefs.edit().putLong("activity_timestamp", activities.timestamps.current.getTime()/1000).commit();

		return activities;
	}

	public <T> void add(List<T> l, T item)
	{
		if(!l.contains(item))
			l.add(item);
	}

	private void updateEpisode(TvShow show, TvShowEpisode episode)
	{
		//this episode is in the db
		if(dbw.insertOrUpdateEpisode(episode))
		{
			//add to the update list
			add(localUpdateShowList, show);
			add(localUpdateEpisodeList, episode);
		}
		else
			//add to the refresh list
			add(traktUpdateShowList, show);
	}

	private void updateShow(TvShow show)
	{
		//this show is in the db
		if(dbw.showExist(show.tvdbId))
			//add to the update list
			add(localUpdateShowList, show);
		else
			//add to the refresh list
			add(traktUpdateShowList, show);
	}

	private void updateMovie(Movie movie)
	{
		//this movie is in the db
		if(dbw.movieExist(movie.imdbId))
			//add to the update list
			add(localUpdateMovieList, movie);
		else
			//add to the refresh list
			add(traktUpdateMovieList, movie);
	}

	@Override
	protected void sendEvent(Activity result) 
	{
		TraktBus.getInstance().post(new TraktItemsUpdatedEvent<TvShow>(finalUpdateShowList));
		TraktBus.getInstance().post(new TraktItemsUpdatedEvent<TvShowEpisode>(finalUpdateEpisodeList));
		TraktBus.getInstance().post(new TraktItemsUpdatedEvent<Movie>(finalUpdateMovieList));

		if(!traktUpdateShowList.isEmpty())
			new UpdateShowsTask(context, new ArrayList<TvShow>(traktUpdateShowList)).fire();
		
		if(!traktUpdateMovieList.isEmpty())
			new UpdateMoviesTask(context, new ArrayList<Movie>(traktUpdateMovieList)).fire();
	}
}

