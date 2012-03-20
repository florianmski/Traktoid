package com.florianmski.tracktoid.adapters;

import java.util.List;

import android.app.Activity;

import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.enumerations.Rating;

public class GridMoviePosterAdapter extends GridPosterAdapter<Movie> implements AdapterInterface
{
	public GridMoviePosterAdapter(Activity context, List<Movie> movies, int height) 
	{
		this.context = context;
		this.items = movies;
		this.filteredItems.addAll(movies);
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
			//TODO
			for(Movie m : items)
			{
				if(!m.watched)
					filteredItems.add(m);
			}
			break;
		case FILTER_LOVED :
			filteredItems.clear();
			for(Movie m : items)
			{
				if(m.rating == Rating.Love)
					filteredItems.add(m);
			}
			break;
		}

		this.notifyDataSetChanged();
	}

	@Override
	public Rating getRating(Object item) 
	{
		return ((Movie)item).rating;
	}

	@Override
	public int getProgress(Object item) 
	{
		return 0;
	}

	@Override
	public String getUrl(Object item) 
	{
		return ((Movie)item).images.poster;
	}

	@Override
	public String getId(Object item) 
	{
		return ((Movie)item).imdbId;
	}

}
