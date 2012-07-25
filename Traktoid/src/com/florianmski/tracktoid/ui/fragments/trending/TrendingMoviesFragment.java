package com.florianmski.tracktoid.ui.fragments.trending;

import java.util.List;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Movie;

public class TrendingMoviesFragment extends TrendingFragment<Movie>
{
	@Override
	public TraktApiBuilder<List<Movie>> getTrendingBuilder() 
	{
		return TraktManager.getInstance().movieService().trending();
	}
}
