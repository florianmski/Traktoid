package com.florianmski.tracktoid.adapters;

import java.util.List;

import android.app.Activity;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public class GridShowPosterAdapter extends GridPosterAdapter<TvShow> implements AdapterInterface
{
	public GridShowPosterAdapter(Activity context, List<TvShow> shows, int height) 
	{
		this.context = context;
		this.items = shows;
		this.filteredItems.addAll(shows);
		this.height = height;
	}

	@Override
	public void setFilter(int filter)
	{
		currentFilter = filter;
		switch(filter)
		{
		case FILTER_ALL :
			filteredItems.clear();
			filteredItems.addAll(items);
			break;
		case FILTER_UNWATCHED :
			filteredItems.clear();
			for(TvShow s : items)
			{
				if(s.progress < 100)
					filteredItems.add(s);
			}
			break;
		case FILTER_LOVED :
			filteredItems.clear();
			for(TvShow s : items)
			{
				if(s.rating == Rating.Love)
					filteredItems.add(s);
			}
			break;
		}

		this.notifyDataSetChanged();
	}

	@Override
	public Rating getRating(Object item) 
	{
		return ((TvShow)item).rating;
	}

	@Override
	public int getProgress(Object item) 
	{
		return ((TvShow)item).progress;
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
