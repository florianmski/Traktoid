package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.List;

import android.support.v4.app.Fragment;

import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Shout;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class ShoutsGetTask<T extends TraktoidInterface<T>> extends GetTask<List<Shout>>
{
	private T traktItem;
	private List<Shout> shouts;
	private ShoutsListener listener;
	
	public ShoutsGetTask(Fragment fragment, T traktItem, ShoutsListener listener) 
	{
		super(fragment);
		
		this.traktItem = traktItem;
		this.listener = listener;
	}
	
	@Override
	protected List<Shout> doTraktStuffInBackground()
	{		
		if(traktItem instanceof TvShow)
			shouts = tm.showService().shouts(traktItem.getId()).fire();
		else if(traktItem instanceof Movie)
			shouts = tm.movieService().shouts(traktItem.getId()).fire();
		else if(traktItem instanceof TvShowEpisode)
			shouts = tm.showService().episodeShouts(((TvShowEpisode)traktItem).tvdbId, ((TvShowEpisode)traktItem).season, ((TvShowEpisode)traktItem).number).fire();
		
		return shouts;
	}
	
	public interface ShoutsListener
	{
		public void onShouts(List<Shout> shouts);
	}

	@Override
	protected void sendEvent(List<Shout> result) 
	{
		if(getRef() != null)
			listener.onShouts(shouts);
	}

}
