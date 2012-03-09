package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Activity;
import com.jakewharton.trakt.entities.ActivityItemBase;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.enumerations.ActivityAction;
import com.jakewharton.trakt.enumerations.ActivityType;

public class ActivityTask extends TraktTask
{
	private TraktApiBuilder<?> builder;
	private Activity activities;

	private DatabaseWrapper dbw;
	private SharedPreferences prefs;

	//shows we'll have to refresh (ex: show or an episode of a show which is not in the db)
	private TreeSet<TvShow> refreshList = new TreeSet<TvShow>();
	//shows we'll update (temp list)
	private TreeSet<TvShow> updateList = new TreeSet<TvShow>();
	//shows we'll update
	private TreeSet<TvShow> finalUpdateList = new TreeSet<TvShow>();

	public ActivityTask(TraktManager tm, Fragment fragment) 
	{
		super(tm, fragment);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected boolean doTraktStuffInBackground()
	{
		showToast("Starting Trakt -> Traktoid sync...", Toast.LENGTH_SHORT);

		activities = 
				tm
				.activityService()
				.user(TraktManager.getUsername())
				.timestamp(prefs.getLong("activity_timestamp", 0))
				.types(ActivityType.Episode, ActivityType.Show)
				.actions(ActivityAction.Checkin, ActivityAction.Rating, ActivityAction.Scrobble, ActivityAction.Seen)
				.fire();

		if(activities != null && activities.activity != null)
		{
			Collections.reverse(activities.activity);

			dbw = new DatabaseWrapper(context);
			dbw.open();

			for(ActivityItemBase activity : activities.activity)
			{
				//			Log.e("test", "type : " + activity.type + ", action : " + activity.action + ", show : " + activity.show.title);
				switch(activity.type)
				{
				case Episode :
				{
					switch(activity.action)
					{
					case Checkin :
					case Scrobble :
						updateEpisode(activity.show, activity.episode);
						break;
					case Seen :
						for(TvShowEpisode episode : activity.episodes)
							updateEpisode(activity.show, episode);
						break;
					}
				}
				case Show :
				{
					switch(activity.action)
					{
					case Rating :
						activity.show.rating = activity.rating;
						updateShow(activity.show);
						break;
					}
				}
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
		prefs.edit().putLong("activity_timestamp", Utils.getPSTTimestamp(System.currentTimeMillis())).commit();

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
		episode.watched = true;
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
}
