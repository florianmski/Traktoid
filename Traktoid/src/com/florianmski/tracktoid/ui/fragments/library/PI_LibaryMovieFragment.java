package com.florianmski.tracktoid.ui.fragments.library;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.GridPosterAdapter;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBMoviesTask;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.get.TraktItemsTask;
import com.florianmski.tracktoid.trakt.tasks.get.TraktItemsTask.TraktItemsListener;
import com.florianmski.tracktoid.trakt.tasks.get.UpdateMoviesTask;
import com.florianmski.tracktoid.ui.activities.phone.MovieActivity;
import com.jakewharton.trakt.entities.Movie;

public class PI_LibaryMovieFragment extends PI_LibraryFragment<Movie>
{
	public static PI_LibaryMovieFragment newInstance(Bundle args)
	{
		PI_LibaryMovieFragment f = new PI_LibaryMovieFragment();
		f.setArguments(args);
		return f;
	}

	public PI_LibaryMovieFragment() {}

	@Override
	public void checkUpdateTask() 
	{
		//TODO
	}

	@Override
	public GridPosterAdapter<Movie> setupAdapter() 
	{
		return new GridPosterAdapter<Movie>(getActivity(), new ArrayList<Movie>(), refreshGridView(), lcm);
	}

	@Override
	public void onGridItemClick(AdapterView<?> arg0, View v, int position, long arg3) 
	{		
		Bundle b = new Bundle();
		Movie movie = adapter.getItem(position);
		b.putSerializable(TraktoidConstants.BUNDLE_TRAKT_ITEM, movie);
		launchActivity(MovieActivity.class, b);
	}

	@Override
	public void displayContent() 
	{
		if(getDBWrapper().isThereMovies())
		{
			new DBMoviesTask(getActivity(), new DBAdapter() 
			{
				@Override
				public void onDBMovies(List<Movie> movies)
				{
					adapter.refreshItems(movies);
					getStatusView().hide().text(null);
				}
			}).fire();
		}
	}

	@Override
	public void onRefreshClick() 
	{
		new TraktItemsTask<Movie>(getActivity(), new TraktItemsListener<Movie>() 
				{
			@Override
			public void onTraktItems(List<Movie> movies) 
			{
				createMoviesDialog(movies);					
			}
				}, tm.userService().libraryMoviesAll(TraktManager.getUsername()), true).fire();
	}

	public void createMoviesDialog(final List<Movie> movies)
	{
		final ArrayList<Movie> selectedMovies = new ArrayList<Movie>();

		String[] items = new String[movies.size()];

		for(int i = 0; i < movies.size(); i++)
			items[i] = movies.get(i).title;

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Which movies(s) do you want to refresh ?");
		builder.setMultiChoiceItems(items, null, new OnMultiChoiceClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) 
			{
				if(isChecked)
					selectedMovies.add(movies.get(which));
				else
					selectedMovies.remove(movies.get(which));
			}
		});

		builder.setPositiveButton("Go!", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				if(selectedMovies.size() > 0)
					new UpdateMoviesTask(getActivity(), selectedMovies).fire();
				else
					Toast.makeText(getActivity(), "Nothing selected...", Toast.LENGTH_SHORT).show();
			}
		});

		builder.setNeutralButton("All", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				new UpdateMoviesTask(getActivity(), movies).fire();
			}
		});

		builder.setNegativeButton("Cancel", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});

		AlertDialog alert = builder.create();

		//avoid trying to show dialog if activity no longer exist
		if(!getActivity().isFinishing())
			alert.show();
	}
}
