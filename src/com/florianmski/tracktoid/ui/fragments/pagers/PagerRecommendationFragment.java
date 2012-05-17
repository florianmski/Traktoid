package com.florianmski.tracktoid.ui.fragments.pagers;


import com.florianmski.tracktoid.ui.fragments.MovieShowFragment;
import com.florianmski.tracktoid.ui.fragments.RecommendationFragment.RecommendationMoviesFragment;
import com.florianmski.tracktoid.ui.fragments.RecommendationFragment.RecommendationShowsFragment;


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
