package com.florianmski.tracktoid.trakt.tasks.get;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.entities.ActivityItemBase;

public class CheckinTask extends TraktTask
{
	private CheckinListener listener;
	private ActivityItemBase checkin;
	

	public CheckinTask(TraktManager tm, Fragment fragment, CheckinListener listener) 
	{
		super(tm, fragment);

		this.listener = listener;
	}

	@Override
	protected boolean doTraktStuffInBackground()
	{
		checkin = tm.userService().watching(TraktManager.getUsername()).fire();

		return true;
	}

	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(success)
			listener.onCheckin(checkin);
	}
	
	public interface CheckinListener
	{
		public void onCheckin(ActivityItemBase checkin);
	}
}
