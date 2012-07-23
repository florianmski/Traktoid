package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.ui.activities.phone.SinglePaneActivity;
import com.florianmski.tracktoid.ui.fragments.library.PagerLibraryFragment;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public class UpdateShowsTask extends GetTask<TvShow>
{
	//TODO make one task with shows/movies
	
	private final static int MAX_PERCENTAGE = 100;
	private final static int NOTIFICATION_ID = 1337;

	private List<TvShow> showsSelected = new ArrayList<TvShow>();
	private TvShow lastProceedShow;

	private Notification notification;
	private NotificationManager nm;
	private RemoteViews contentView;

	public UpdateShowsTask(Context context, List<TvShow> selectedShows) 
	{
		super(context, sSingleThreadExecutor);

		this.showsSelected = selectedShows;
	}

	@Override
	protected TvShow doTraktStuffInBackground()
	{
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
			this.publishProgress(0, lastProceedShow, "update");

			i++;

			showToast(s.title + " refreshed!", Toast.LENGTH_SHORT);
		}		

		//if user choose to refresh only one show, no need to toast "show refreshed" then "refresh done"
		if(showsSelected.size() > 1)
			showToast("Refresh done!", Toast.LENGTH_SHORT);
		
		dbw.close();
		
		return lastProceedShow;
	}

	@Override
	protected void onProgressPublished(int progress, TvShow tmpResult, String... values)
	{
		super.onProgressPublished(progress, tmpResult, values);

		if(values != null && values.length > 0 && values[0].equals("update") && lastProceedShow != null)
			TraktTask.traktItemUpdated(lastProceedShow);
	}
	
	@Override
	protected void onCompleted(TvShow show)
	{		
		if(nm != null)
			nm.cancel(NOTIFICATION_ID);
	}
	
	@SuppressWarnings("deprecation")
	private void createNotification()
	{
		notification = new Notification(R.drawable.ab_icon_refresh, "Refreshing...", System.currentTimeMillis());
		nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		contentView = new RemoteViews(context.getPackageName(), R.layout.notification_progress);
		notification.contentView = contentView;

		Intent notificationIntent = new Intent(context, SinglePaneActivity.class);
		notificationIntent.putExtra(TraktoidConstants.BUNDLE_CLASS, PagerLibraryFragment.class.getName());
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

	@Override
	protected void sendEvent(TvShow result) 
	{
		// TODO Auto-generated method stub
	}

}
