package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Activity;
import com.jakewharton.trakt.entities.ActivityItemBase;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class ActivityTask extends TraktTask
{
	private TraktApiBuilder<?> builder;
	private ActivityListener listener;
	private Activity activities;
	
	private DatabaseWrapper dbw;
	//TODO save a timestamp (be careful with gmt !)
	//TODO redo the checkintask

	//shows we'll have to refresh (ex: show or an episode of a show is not in the db)
	private TreeSet<TvShow> refreshList = new TreeSet<TvShow>();
	//shows we'll update (temp list)
	private TreeSet<TvShow> updateList = new TreeSet<TvShow>();
	//shows we'll update
	private TreeSet<TvShow> finalUpdateList = new TreeSet<TvShow>();

	public ActivityTask(TraktManager tm, Fragment fragment, ActivityListener listener, TraktApiBuilder<?> builder) 
	{
		super(tm, fragment);

		this.builder = builder;
		this.listener = listener;
	}

	@Override
	protected boolean doTraktStuffInBackground()
	{
		activities = (Activity) builder.fire();

		Collections.reverse(activities.activity);
		
		dbw = new DatabaseWrapper(context);
		dbw.open();

		for(ActivityItemBase activity : activities.activity)
		{
			Log.e("test", "type : " + activity.type + ", action : " + activity.action + ", show : " + activity.show.title);
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
					updateShow(activity.show);
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
//			listener.onActivity(activity);
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

	public interface ActivityListener
	{
		public void onActivity(ActivityItemBase activity);
	}
}
