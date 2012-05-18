package com.florianmski.tracktoid.ui.fragments.recommendations;


import com.florianmski.tracktoid.ui.fragments.MovieShowFragment;


public class PagerRecommendationFragment extends MovieShowFragment
{	
	public PagerRecommendationFragment() {}

	@Override
	public Class<?> getShowFragment() 
	{
		return RecommendationShowsFragment.class;
	}

	@Override
	public Class<?> getMovieFragment() 
	{
		return RecommendationMoviesFragment.class;
	}

	
}
