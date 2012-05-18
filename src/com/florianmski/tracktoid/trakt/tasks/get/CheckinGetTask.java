package com.florianmski.tracktoid.trakt.tasks.get;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.ActivityItemBase;
import com.jakewharton.trakt.enumerations.ActivityAction;
import com.jakewharton.trakt.enumerations.ActivityType;

public class CheckinGetTask extends TraktTask
{
	private CheckinListener listener;
	private ActivityItemBase checkin;
	private TraktoidInterface traktItem;
	

	public CheckinGetTask(TraktManager tm, Fragment fragment, CheckinListener listener) 
	{
		super(tm, fragment);

		this.listener = listener;
	}

	@Override
	protected boolean doTraktStuffInBackground()
	{
		checkin = tm.userService().watching(TraktManager.getUsername()).fire();

		if(checkin != null && checkin.action == ActivityAction.Checkin)
		{
			if(checkin.type == ActivityType.Episode)
				traktItem = checkin.episode;
			else
				traktItem = checkin.movie;
			return true;
		}
		
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(success)
			listener.onCheckin(traktItem);
	}
	
	public interface CheckinListener
	{
		public void onCheckin(TraktoidInterface traktItem);
	}
}
