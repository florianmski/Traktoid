package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.util.Date;

import android.os.Bundle;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Ratings;
import com.jakewharton.trakt.enumerations.Rating;

public class MovieFragment extends PagerItemTraktFragment
{
	private Movie m;

	public static MovieFragment newInstance(Bundle args)
	{
		MovieFragment f = new MovieFragment();
		f.setArguments(args);
		return f;
	}

	public static MovieFragment newInstance(Movie m)
	{
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_MOVIE, m);

		return newInstance(args);
	}

	public MovieFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if(getArguments() != null)
			m = (Movie) getArguments().getSerializable(TraktoidConstants.BUNDLE_MOVIE);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

	@Override
	public Date getFirstAired() 
	{
		return m.released;
	}

	@Override
	public Ratings getRatings() 
	{
		return m.ratings;
	}

	@Override
	public Rating getRating() 
	{
		return m.rating;
	}

	@Override
	public boolean isWatched() 
	{
		return m.watched;
	}

	@Override
	public Image getImage() 
	{
		return new Image(m.imdbId, m.images.fanart, Image.FANART);
	}

	@Override
	public String getOverview() 
	{
		return m.overview;
	}
}
