package com.florianmski.tracktoid.ui.fragments.pagers.items;

import android.os.Bundle;

import com.florianmski.tracktoid.TraktoidConstants;
import com.jakewharton.trakt.entities.Movie;

public class MovieFragment extends PagerItemTraktFragment<Movie>
{
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
			item = (Movie) getArguments().getSerializable(TraktoidConstants.BUNDLE_MOVIE);
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
}
