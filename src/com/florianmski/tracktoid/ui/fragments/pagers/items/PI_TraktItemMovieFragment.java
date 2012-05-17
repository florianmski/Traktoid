package com.florianmski.tracktoid.ui.fragments.pagers.items;

import android.os.Bundle;

import com.florianmski.tracktoid.TraktoidConstants;
import com.jakewharton.trakt.entities.Movie;

public class PI_TraktItemMovieFragment extends PI_TraktItemFragment<Movie>
{
	public static PI_TraktItemMovieFragment newInstance(Bundle args)
	{
		PI_TraktItemMovieFragment f = new PI_TraktItemMovieFragment();
		f.setArguments(args);
		return f;
	}

	public static PI_TraktItemMovieFragment newInstance(Movie m)
	{
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_TRAKT_ITEM, m);

		return newInstance(args);
	}

	public PI_TraktItemMovieFragment() {}
	
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
