package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.List;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Shout;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class ShoutsGetTask<T extends TraktoidInterface<T>> extends TraktTask
{
	private String tvdbId;
	private T traktItem;
	private List<Shout> shouts;
	private ShoutsListener listener;
	
//	public ShoutsGetTask(TraktManager tm, Fragment fragment, String tvdbId, ShoutsListener listener) 
//	{
//		super(tm, fragment);
//		
//		this.tvdbId = tvdbId;
//		this.listener = listener;
//	}
	
	public ShoutsGetTask(TraktManager tm, Fragment fragment, T traktItem, String tvdbId, ShoutsListener listener) 
	{
		super(tm, fragment);
		
		this.tvdbId = tvdbId;
		this.traktItem = traktItem;
		this.listener = listener;
	}
	
	@Override
	protected boolean doTraktStuffInBackground()
	{
//		showToast("Retrieving shouts...", Toast.LENGTH_SHORT);
		
		if(traktItem instanceof TvShow)
			shouts = tm.showService().shouts(traktItem.getId()).fire();
		else if(traktItem instanceof Movie)
			shouts = tm.movieService().shouts(traktItem.getId()).fire();
		else if(traktItem instanceof TvShowEpisode)
			shouts = tm.showService().episodeShouts(((TvShowEpisode)traktItem).tvdbId, ((TvShowEpisode)traktItem).season, ((TvShowEpisode)traktItem).number).fire();
		
		return true;
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
