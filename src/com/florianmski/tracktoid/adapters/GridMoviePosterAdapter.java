package com.florianmski.tracktoid.adapters;

import java.util.List;

import android.app.Activity;

import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.enumerations.Rating;

public class GridMoviePosterAdapter extends GridPosterAdapter<Movie> implements AdapterInterface
{
	public GridMoviePosterAdapter(Activity context, List<Movie> movies, int height) 
	{
		super(context);
		this.items = movies;
		this.filteredItems.addAll(movies);
		this.height = height;
	}

	@Override
	public Rating getRating(Object item) 
	{
		return ((Movie)item).rating;
	}

	@Override
	public boolean isWatched(Object item) 
	{
//		return ((Movie)item).watched;
		//TODO something is wrong here
		return true;
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
