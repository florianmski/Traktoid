package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.trakt.tasks.get.GenresTask;
import com.florianmski.tracktoid.trakt.tasks.get.GenresTask.GenresListener;
import com.florianmski.tracktoid.trakt.tasks.get.MoviesTask;
import com.florianmski.tracktoid.trakt.tasks.get.MoviesTask.MoviesListener;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.jakewharton.trakt.entities.Genre;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.services.RecommendationsService.MoviesBuilder;

public class RecommendationMoviesFragment extends RecommendationFragment<Movie> implements ActionBar.OnNavigationListener
{	
	public static RecommendationMoviesFragment newInstance(Bundle args)
	{
		RecommendationMoviesFragment f = new RecommendationMoviesFragment();
		f.setArguments(args);
		return f;
	}

	public RecommendationMoviesFragment() {}

	public RecommendationMoviesFragment(FragmentListener listener) 
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
				RecommendationMoviesFragment.this.genres = genres;
				setListNavigationMode();				
			}
		}, tm.genreService().movies());
	}
	
	@Override
	public PostTask getDismissTask(String id) 
	{
		return new PostTask(tm, RecommendationMoviesFragment.this, tm.recommendationsService().dismissMovie(id), new PostListener() 
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
		MoviesBuilder builder = tm.recommendationsService().movies();

		if(genre != null)
			builder.genre(genre);
		
		return new MoviesTask(tm, this, new MoviesListener() 
		{
			@Override
			public void onMovies(ArrayList<Movie> movies) 
			{
				RecommendationMoviesFragment.this.items = movies;
				setAdapter();
			}
		}, builder, false);
	}
}
