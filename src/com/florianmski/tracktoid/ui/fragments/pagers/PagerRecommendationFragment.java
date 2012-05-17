package com.florianmski.tracktoid.ui.fragments.pagers;


import com.florianmski.tracktoid.ui.fragments.MovieShowFragment;
import com.florianmski.tracktoid.ui.fragments.RecommendationFragment;
import com.florianmski.tracktoid.ui.fragments.RecommendationFragment.RecommendationMoviesFragment;
import com.florianmski.tracktoid.ui.fragments.RecommendationFragment.RecommendationShowsFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.PI_LibaryMovieFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.PI_LibraryShowFragment;


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
