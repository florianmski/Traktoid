package com.florianmski.tracktoid.trakt.tasks;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask.ShowsListener;
import com.jakewharton.trakt.entities.Shout;

public class ShoutsGetTask extends TraktTask
{
	private String tvdbId;
	private List<Shout> shouts;
	private ShoutsListener listener;
	
	public ShoutsGetTask(TraktManager tm, Fragment fragment, String tvdbId, ShoutsListener listener) 
	{
		super(tm, fragment);
		
		this.tvdbId = tvdbId;
		this.listener = listener;
	}
	
	protected void doTraktStuffInBackground()
	{
		this.publishProgress("toast", "0", "Retrieving shouts...");
		
		shouts = tm.showService().shouts(tvdbId).fire();
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success && !Utils.isActivityFinished(fragment.getActivity()))
			listener.onShouts(shouts);
	}
	
	public interface ShoutsListener
	{
		public void onShouts(List<Shout> shouts);
	}

}
