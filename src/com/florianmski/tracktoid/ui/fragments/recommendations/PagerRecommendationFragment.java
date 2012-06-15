package com.florianmski.tracktoid.ui.fragments.recommendations;


import android.os.Bundle;

import com.florianmski.tracktoid.ui.fragments.MovieShowFragment;


public class PagerRecommendationFragment extends MovieShowFragment
{	
	public static PagerRecommendationFragment newInstance(Bundle args)
	{
		PagerRecommendationFragment f = new PagerRecommendationFragment();
		f.setArguments(args);
		return f;
	}
	
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
