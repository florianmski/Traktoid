package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktBus;
import com.florianmski.tracktoid.TraktItemsUpdatedEvent;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.florianmski.tracktoid.ui.activities.SinglePaneActivity;
import com.florianmski.tracktoid.ui.fragments.library.PagerLibraryFragment;
import com.jakewharton.trakt.entities.Movie;

public class UpdateMoviesTask extends BaseTask<Movie>
{
	private final static int MAX_PERCENTAGE = 100;
	private final static int NOTIFICATION_ID = 1337;

	private List<Movie> moviesSelected = new ArrayList<Movie>();
	private Movie lastProceedMovie;

	private Notification notification;
	private NotificationManager nm;
	private RemoteViews contentView;

	public UpdateMoviesTask(Context context, List<Movie> selectedShows) 
	{
		super(context, sSingleThreadExecutor);

		this.moviesSelected = selectedShows;
	}

	@Override
	protected Movie doTraktStuffInBackground()
	{		
		//sort shows by name, not really necessary
		Collections.sort(moviesSelected);
		
		createNotification();
		
		DatabaseWrapper dbw = new DatabaseWrapper(context);

		int i = 0;
		for(Movie m : moviesSelected)
		{
			/** 
			 * Because it seems impossible to setSecondaryProgress on progressBar in remoteViews, 
			 * I ended up with two different progressBar and a relativeLayout
			 */
			updateProgress(m.title, (int)(i * (MAX_PERCENTAGE*1.0/moviesSelected.size()*1.0)));
			updateSecondaryProgress("Downloading...", 0);

			String query = null;
			
			if(m.imdbId != null && !m.imdbId.trim().equals(""))
				query = m.imdbId;
			else if(m.tmdbId != null && !m.tmdbId.trim().equals(""))
				query = m.tmdbId;
			else if(m.url != null && !m.url.trim().equals(""))
				query = m.url.substring(m.url.lastIndexOf("/")+1);
			
			if(query != null)
			{
				Log.e("test", "url " + m.url + "test " + query);
				
				m = tm.movieService().summary(query).fire();
	
				dbw.insertOrUpdateMovie(m);
				
				lastProceedMovie = dbw.getMovie(m.url);
	
				//send an event to activities which are listening to the update of a specific movie (or not)
				this.publishProgress(0, lastProceedMovie, "update");
	
				i++;
			}
			else
				showToast(m.title + " not found...", Toast.LENGTH_SHORT);
		}		

		showToast("Refresh done!", Toast.LENGTH_SHORT);
		
		dbw.close();
		
		return lastProceedMovie;
	}

	@Override
	protected void onProgressPublished(int progress, Movie tmpResult, String... values)
	{
		super.onProgressPublished(progress, tmpResult, values);

		if(values != null && values.length > 0 && values[0].equals("update") && lastProceedMovie != null)
			TraktBus.getInstance().post(new TraktItemsUpdatedEvent<Movie>(lastProceedMovie));
	}
	
	@Override
	protected void onCompleted(Movie movies)
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
	protected void sendEvent(Movie result) 
	{
		// TODO Auto-generated method stub
	}

}
