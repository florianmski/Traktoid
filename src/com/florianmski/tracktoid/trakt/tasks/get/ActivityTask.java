package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask.ShowsListener;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.ActivityItemBase;
import com.jakewharton.trakt.entities.TvShow;

public class ActivityTask extends TraktTask
{
	private TraktApiBuilder<?> builder;
	private ActivityListener listener;
	private ActivityItemBase activity;
	
	public ActivityTask(TraktManager tm, Fragment fragment, ActivityListener listener, TraktApiBuilder<?> builder) 
	{
		super(tm, fragment);
		
		this.builder = builder;
		this.listener = listener;
		
		this.setSilent(true);
	}
	
	@Override
	protected boolean doTraktStuffInBackground()
	{
		activity = (ActivityItemBase) builder.fire();
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success && !Utils.isActivityFinished(fragment.getActivity()))
			listener.onActivity(activity);
	}
	
	public interface ActivityListener
	{
		public void onActivity(ActivityItemBase activity);
	}
}
