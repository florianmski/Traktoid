package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.util.ArrayList;

import net.londatiga.android.QuickAction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.florianmski.tracktoid.adapters.GridMoviePosterAdapter;
import com.florianmski.tracktoid.adapters.GridPosterAdapter;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.get.MoviesTask;
import com.florianmski.tracktoid.trakt.tasks.get.MoviesTask.MoviesListener;
import com.jakewharton.trakt.entities.Movie;

public class MoviesFragment extends PagerItemLibraryFragment
{
	public static MoviesFragment newInstance(Bundle args)
	{
		MoviesFragment f = new MoviesFragment();
		f.setArguments(args);
		return f;
	}
	
	public MoviesFragment() {}

	@Override
	public void checkUpdateTask() 
	{
		//TODO
	}

	@Override
	public GridPosterAdapter<Movie> setupAdapter() 
	{
		return new GridMoviePosterAdapter(getActivity(), new ArrayList<Movie>(), refreshGridView());
	}

	@Override
	public Intent onGridItemClick(AdapterView<?> arg0, View v, int position, long arg3) 
	{
		//TODO
		return null;
	}

	@Override
	public void onDBEmpty() 
	{
		//TODO
	}

	@Override
	public void onDBNotEmpty() 
	{
		tm.addToQueue(new MoviesTask(tm, this, new MoviesListener() 
		{
			@Override
			public void onMovies(ArrayList<Movie> movies) 
			{
				adapter.updateItems(movies);						
			}
		}, tm.userService().libraryMoviesAll(TraktManager.getUsername()), true));
	}

	@Override
	public void onRefreshQAClick(QuickAction source, int pos, int actionId) 
	{
		//TODO
	}

	@Override
	public void onDeleteQAClick(QuickAction source, int pos, int actionId) 
	{
		//TODO
	}

	@Override
	public void onRateQAClick(QuickAction source, int pos, int actionId) 
	{
		//TODO
	}

	@Override
	public void onRefreshClick() 
	{
		//TODO
	}

}
