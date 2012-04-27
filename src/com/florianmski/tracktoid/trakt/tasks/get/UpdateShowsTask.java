/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.ui.activities.phone.LibraryActivity;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public class UpdateShowsTask extends TraktTask
{
	private final static int MAX_PERCENTAGE = 100;
	private final static int NOTIFICATION_ID = 1337;

	private List<TvShow> showsSelected = new ArrayList<TvShow>();
	private TvShow lastProceedShow;

	private Notification notification;
	private NotificationManager nm;
	private RemoteViews contentView;

	public UpdateShowsTask(TraktManager tm, Fragment fragment, List<TvShow> selectedShows) 
	{
		super(tm, fragment);

		this.showsSelected = selectedShows;
	}

	@Override
	protected boolean doTraktStuffInBackground()
	{		
		//TODO remove it (or not ?)
		//allow task to failed before creating notification if something is wrong (bad username, trakt server issue...)
		//test if user account is ok
//		tm.accountService().test().fire();

		//sort shows by name, not really necessary
		Collections.sort(showsSelected);
		
		createNotification();
		
		DatabaseWrapper dbw = new DatabaseWrapper(context);

		int i = 0;
		for(TvShow s : showsSelected)
		{
			/** 
			 * Because it seems impossible to setSecondaryProgress on progressBar in remoteViews, 
			 * I ended up with two different progressBar and a relativeLayout
			 */
			updateProgress(s.title, (int)(i * (MAX_PERCENTAGE*1.0/showsSelected.size()*1.0)));
			updateSecondaryProgress("Downloading...", 0);

			showToast("Refreshing " + s.title + "...", Toast.LENGTH_SHORT);

			s = tm.showService().summary(s.tvdbId).extended().fire();

			dbw.insertOrUpdateShow(s);
			
			List<TvShowSeason> seasons = s.seasons;
			dbw.insertOrUpdateSeasons(seasons, s.tvdbId);
			for(TvShowSeason season : seasons)
			{				
				updateSecondaryProgress(season.season == 0 ? "Specials" : "Season " + season.season, (int) ((Math.abs(season.season-seasons.size()))*(MAX_PERCENTAGE*1.0/seasons.size()*1.0)));

				List<TvShowEpisode> episodes = season.episodes.episodes;
				dbw.insertOrUpdateEpisodes(episodes, season.url);
			}
			
			dbw.refreshPercentage(s.tvdbId);
			//get show with his progress field
			lastProceedShow = dbw.getShow(s.tvdbId);
			//get seasons with episodesWatched field (a bit stupid to retrieve this amount of data for only one field)
			//TODO something more optimized
			lastProceedShow.seasons = dbw.getSeasons(s.tvdbId, true, true);

			//send an event to activities which are listening to the update of a specific show (or not)
			this.publishProgress("update");

			i++;

			showToast(s.title + " refreshed!", Toast.LENGTH_SHORT);
		}		

		//if user choose to refresh only one show, no need to toast "show refreshed" then "refresh done"
		if(showsSelected.size() > 1)
			showToast("Refresh done!", Toast.LENGTH_SHORT);
		
		dbw.close();
		
		return true;
	}

	@Override
	protected void onProgressUpdate(String... values) 
	{
		super.onProgressUpdate(values);

		if(values[0].equals("update") && lastProceedShow != null)
			tm.onShowUpdated(lastProceedShow);
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(nm != null)
			nm.cancel(NOTIFICATION_ID);
	}
	
	private void createNotification()
	{
		notification = new Notification(R.drawable.ab_icon_refresh, "Refreshing...", System.currentTimeMillis());
		nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		contentView = new RemoteViews(context.getPackageName(), R.layout.notification_progress);
		notification.contentView = contentView;

		Intent notificationIntent = new Intent(context, LibraryActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.contentIntent = contentIntent;
		notification.flags = Notification.FLAG_NO_CLEAR;
		
		nm.notify(NOTIFICATION_ID, notification);
	}
	
	private void updateProgress(String text, int progress)
	{
		contentView.setTextViewText(R.id.textViewShow, text);
		contentView.setProgressBar(R.id.progressBarShows, 100, progress, false);
		nm.notify(NOTIFICATION_ID, notification);
	}
	
	private void updateSecondaryProgress(String text, int progress)
	{
		contentView.setTextViewText(R.id.textViewSeason, text);
		contentView.setProgressBar(R.id.progressBarSeasons, 100, progress, false);
		nm.notify(NOTIFICATION_ID, notification);
	}

}
