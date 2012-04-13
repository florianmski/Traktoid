package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.trakt.tasks.get.GenresTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.get.GenresTask.GenresListener;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.jakewharton.trakt.entities.Genre;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.services.RecommendationsService.ShowsBuilder;

public class RecommendationShowsFragment extends RecommendationFragment<TvShow> implements ActionBar.OnNavigationListener
{	
	public static RecommendationShowsFragment newInstance(Bundle args)
	{
		RecommendationShowsFragment f = new RecommendationShowsFragment();
		f.setArguments(args);
		return f;
	}

	public RecommendationShowsFragment() {}

	public RecommendationShowsFragment(FragmentListener listener) 
	{
		super(listener);
	}

	@Override
	public GenresTask getGenresTask() 
	{
		return new GenresTask(tm, this, new GenresListener() 
		{
			@Override
			public void onGenres(final List<Genre> genres) 
			{				
				RecommendationShowsFragment.this.genres = genres;
				setListNavigationMode();				
			}
		}, tm.genreService().shows());
	}
	
	@Override
	public PostTask getDismissTask(String id) 
	{
		return new PostTask(tm, RecommendationShowsFragment.this, tm.recommendationsService().dismissShow(Integer.valueOf(id)), new PostListener() 
		{
			@Override
			public void onComplete(Response r, boolean success) 
			{
				adapter.clear();
				createGetRecommendationsTask().execute();
			}
		});
	}

	@Override
	public TraktTask getItemsTask(Genre genre) 
	{
		ShowsBuilder builder = tm.recommendationsService().shows();

		if(genre != null)
			builder.genre(genre);
		
		return new ShowsTask(tm, this, new ShowsListener() 
		{
			@Override
			public void onShows(ArrayList<TvShow> shows) 
			{
				RecommendationShowsFragment.this.items = shows;
				setAdapter();
			}
		}, builder, false);
	}
}
