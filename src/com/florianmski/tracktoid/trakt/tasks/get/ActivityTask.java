package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Activity;
import com.jakewharton.trakt.entities.ActivityItemBase;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class ActivityTask extends TraktTask
{
	private TraktApiBuilder<?> builder;
	private Activity activities;

	private DatabaseWrapper dbw;
	private SharedPreferences prefs;

	//shows we'll have to refresh (ex: show or an episode of a show which is not in the db)
	private List<TvShow> refreshList = new ArrayList<TvShow>();
	//shows we'll update (temp list)
	private List<TvShow> updateList = new ArrayList<TvShow>();
	//shows we'll update
	private List<TvShow> finalUpdateList = new ArrayList<TvShow>();

	public ActivityTask(TraktManager tm, Fragment fragment) 
	{
		super(tm, fragment);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected boolean doTraktStuffInBackground()
	{
		showToast("Starting Trakt -> Traktoid sync...", Toast.LENGTH_SHORT);

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
					case Collection :
						activity.show.inCollection = true;
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
					}
					updateMovie(activity.movie);
					break;
				}
			}

			for(TvShow show : updateList)
			{
				dbw.insertOrUpdateShow(show);
				dbw.refreshPercentage(show.tvdbId);
				show = dbw.getShow(show.tvdbId);
				show.seasons = dbw.getSeasons(show.tvdbId, true, true);
				finalUpdateList.add(show);
			}

			dbw.close();

		}

		showToast("Sync over!", Toast.LENGTH_SHORT);
		prefs.edit().putLong("activity_timestamp", activities.timestamps.current.getTime()/1000).commit();

		return true;
	}

	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(success)
		{
			for(TvShow show : finalUpdateList)
				tm.onShowUpdated(show);

			if(!refreshList.isEmpty())
				tm.addToQueue(new UpdateShowsTask(tm, fragment, new ArrayList<TvShow>(refreshList)));
		}
	}

	private void updateEpisode(TvShow show, TvShowEpisode episode)
	{
		//this episode is in the db
		if(dbw.insertOrUpdateEpisode(episode))
			//add to the update list
			updateList.add(show);
		else
			//add to the refresh list
			refreshList.add(show);
	}

	private void updateShow(TvShow show)
	{
		//this show is in the db
		if(dbw.showExist(show.tvdbId))
			//add to the update list
			updateList.add(show);
		else
			//add to the refresh list
			refreshList.add(show);
	}

	private void updateMovie(Movie movie)
	{
		//TODO
		//		//this show is in the db
		//		if(dbw.movieExist(movie.imdbId))
		//			//add to the update list
		//			updateList.add(show);
		//		else
		//			//add to the refresh list
		//			refreshList.add(show);
	}
}
