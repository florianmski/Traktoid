package com.florianmski.tracktoid.ui.fragments.recommendations;

import java.util.List;

import android.os.Bundle;

import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.DismissResponse;
import com.jakewharton.trakt.entities.Genre;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.services.RecommendationsService.ShowsBuilder;

public class RecommendationShowsFragment extends RecommendationFragment<TvShow>
{	
	public static RecommendationShowsFragment newInstance(Bundle args)
	{
		RecommendationShowsFragment f = new RecommendationShowsFragment();
		f.setArguments(args);
		return f;
	}

	public RecommendationShowsFragment() {}

	@Override
	public TraktApiBuilder<DismissResponse> getDismissBuilder(String id)
	{
		return tm.recommendationsService().dismissShow(Integer.valueOf(id));
	}

	@Override
	public TraktApiBuilder<List<TvShow>> getRecommendationBuilder(Genre genre) 
	{
		ShowsBuilder builder = tm.recommendationsService().shows();

		if(genre != null)
			builder.genre(genre);

		if(spStartYear != null && spEndYear != null)
			builder.startYear(spStartYear.getSelectedItemPosition() + START_YEAR).endYear(END_YEAR - spEndYear.getSelectedItemPosition());

		return builder;
	}

	@Override
	public TraktApiBuilder<List<Genre>> getGenreBuilder() 
	{
		return tm.genreService().shows();
	}
}