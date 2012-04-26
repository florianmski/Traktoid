package com.florianmski.tracktoid.ui.fragments.pagers;

import java.util.List;

import android.os.Bundle;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.pagers.PagerMovieAdapter;
import com.jakewharton.trakt.entities.Movie;

public class MoviePagerFragment extends PagerFragment
{
	//TODO onMovieUpdated()
	private Movie movie;
	
	public static MoviePagerFragment newInstance(Bundle args)
	{
		MoviePagerFragment f = new MoviePagerFragment();
		f.setArguments(args);
		return f;
	}
	
	public MoviePagerFragment() {}
	
	public MoviePagerFragment(FragmentListener listener) 
	{
		super(listener);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setPageIndicatorType(PagerFragment.IT_CIRCLE);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		getStatusView().show().text("Loading movies,\nPlease wait...");
		setData();
	}
	
	public void setData()
	{
		new Thread()
		{
			@Override
			@SuppressWarnings("unchecked")
			public void run()
			{
				final List<Movie> movies = (List<Movie>)getArguments().getSerializable(TraktoidConstants.BUNDLE_RESULTS);

				getActivity().runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						adapter = new PagerMovieAdapter(movies, getFragmentManager(), getActivity());
						
						if(((PagerMovieAdapter)adapter).isEmpty())
							getStatusView().hide().text("No movies, this is strange...");
						else
							getStatusView().hide().text(null);
						
						initPagerFragment(adapter);
					}
				});
			}
		}.start();
	}

	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);

		movie = ((PagerMovieAdapter)adapter).getMovie(currentPagerPosition);
		setTitle(movie.title);
	}
}
