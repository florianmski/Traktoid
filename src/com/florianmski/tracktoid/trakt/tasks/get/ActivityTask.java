package com.florianmski.tracktoid.trakt.tasks.get;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Activity;
import com.jakewharton.trakt.entities.ActivityItemBase;

public class ActivityTask extends TraktTask
{
	private TraktApiBuilder<?> builder;
	private ActivityListener listener;
	private Activity activities;
	
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
		
		for(ActivityItemBase activity : activities.activity)
		{
			Log.e("test", "test : " + activity.action);
		}
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
//		if(success && !Utils.isActivityFinished(fragment.getActivity()))
//			listener.onActivity(activity);
	}
	
	public interface ActivityListener
	{
		public void onActivity(ActivityItemBase activity);
	}
}
