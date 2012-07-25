package com.florianmski.tracktoid.ui.fragments.trending;


import android.os.Bundle;

import com.florianmski.tracktoid.ui.fragments.MovieShowFragment;


public class PagerTrendingFragment extends MovieShowFragment
{	
	public static PagerTrendingFragment newInstance(Bundle args)
	{
		PagerTrendingFragment f = new PagerTrendingFragment();
		f.setArguments(args);
		return f;
	}
	
	public PagerTrendingFragment() {}

	@Override
	public Class<?> getShowFragment() 
	{
		return TrendingShowsFragment.class;
	}

	@Override
	public Class<?> getMovieFragment() 
	{
		return TrendingMoviesFragment.class;
	}

	
}
