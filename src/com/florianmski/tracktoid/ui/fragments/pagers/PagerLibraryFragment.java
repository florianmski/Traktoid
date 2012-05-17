package com.florianmski.tracktoid.ui.fragments.pagers;


import com.florianmski.tracktoid.ui.fragments.MovieShowFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.PI_LibaryMovieFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.PI_LibraryShowFragment;


public class PagerLibraryFragment extends MovieShowFragment
{	
	public PagerLibraryFragment() {}

	@Override
	public Class<?> getShowFragment() 
	{
		return PI_LibraryShowFragment.class;
	}

	@Override
	public Class<?> getMovieFragment() 
	{
		return PI_LibaryMovieFragment.class;
	}

	
}
