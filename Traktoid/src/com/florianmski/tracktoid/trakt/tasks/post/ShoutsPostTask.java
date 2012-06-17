package com.florianmski.tracktoid.trakt.tasks.post;

import android.content.Context;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class ShoutsPostTask<T extends TraktoidInterface<T>> extends PostTask
{
	private T traktItem;
	private String shout;
	private boolean spoiler;
	
	public ShoutsPostTask(Context context, T traktItem, String shout, boolean spoiler, PostListener pListener) 
	{
		super(context, null, pListener);
		
		this.traktItem = traktItem;
		this.shout = shout;
		this.spoiler = spoiler;
	}

	@Override
	protected void doPrePostStuff() 
	{
		if(traktItem instanceof TvShow)
			builders.add(tm
			.shoutService()
			.show(Integer.valueOf(traktItem.getId()))
			.shout(shout)
			.spoiler(spoiler));
		else if(traktItem instanceof Movie)
			builders.add(tm
			.shoutService()
			.movie(Integer.valueOf(traktItem.getId()))
			.shout(shout)
			.spoiler(spoiler));
		else if(traktItem instanceof TvShowEpisode)
			builders.add(tm
			.shoutService()
			.episode(Integer.valueOf(((TvShowEpisode)traktItem).tvdbId))
			.season(((TvShowEpisode)traktItem).season)
			.episode(((TvShowEpisode)traktItem).number)
			.shout(shout)
			.spoiler(spoiler));
	}
}
