package com.florianmski.tracktoid.adapters;

import java.util.List;

import android.app.Activity;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public class GridShowPosterAdapter extends GridPosterAdapter<TvShow> implements AdapterInterface
{
	public GridShowPosterAdapter(Activity context, List<TvShow> shows, int height) 
	{
		super(context);
		this.items = shows;
		this.filteredItems.addAll(shows);
		this.height = height;
	}

	@Override
	public Rating getRating(Object item) 
	{
		return ((TvShow)item).rating;
	}

	@Override
	public boolean isWatched(Object item) 
	{
		return ((TvShow)item).progress == 100;
	}

	@Override
	public String getUrl(Object item) 
	{
		return ((TvShow)item).images.poster;
	}

	@Override
	public String getId(Object item) 
	{
		return ((TvShow)item).tvdbId;
	}

}
