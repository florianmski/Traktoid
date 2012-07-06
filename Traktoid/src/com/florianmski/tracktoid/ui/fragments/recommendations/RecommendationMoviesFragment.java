package com.florianmski.tracktoid.ui.fragments.recommendations;

import java.util.List;

import android.os.Bundle;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.DismissResponse;
import com.jakewharton.trakt.entities.Genre;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.services.RecommendationsService.MoviesBuilder;

public class RecommendationMoviesFragment extends RecommendationFragment<Movie>
{	
	public static RecommendationMoviesFragment newInstance(Bundle args)
	{
		RecommendationMoviesFragment f = new RecommendationMoviesFragment();
		f.setArguments(args);
		return f;
	}

	public RecommendationMoviesFragment() {}

	@Override
	public TraktApiBuilder<DismissResponse> getDismissBuilder(String id)
	{
		return tm.recommendationsService().dismissMovie(id);
	}

	@Override
	public TraktApiBuilder<List<Movie>> getRecommendationBuilder(Genre genre) 
	{
		MoviesBuilder builder = tm.recommendationsService().movies();

		if(genre != null && !genre.slug.equals("all-genres"))
			builder.genre(genre);
		
		builder.startYear(startYear).endYear(endYear);
		
		return builder;
	}

	@Override
	public TraktApiBuilder<List<Genre>> getGenreBuilder() 
	{
		return tm.genreService().movies();
	}
}
