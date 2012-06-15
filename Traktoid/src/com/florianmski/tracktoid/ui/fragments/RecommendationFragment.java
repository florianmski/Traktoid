package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListRecommendationAdapter;
import com.florianmski.tracktoid.adapters.lists.ListRecommendationAdapter.DismissListener;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.trakt.tasks.get.GenresTask;
import com.florianmski.tracktoid.trakt.tasks.get.GenresTask.GenresListener;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.ui.activities.phone.ShowActivity;
import com.jakewharton.trakt.entities.Genre;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.services.RecommendationsService.ShowsBuilder;

public class RecommendationFragment extends TraktFragment implements ActionBar.OnNavigationListener
{	
	private ListView lvRecommendations;

	private ListRecommendationAdapter adapter;

	private ArrayList<Genre> genres;
	private ArrayList<TvShow> shows;

	private int recreation;

	public static RecommendationFragment newInstance(Bundle args)
	{
		RecommendationFragment f = new RecommendationFragment();
		f.setArguments(args);
		return f;
	}

	public RecommendationFragment() {}

	public RecommendationFragment(FragmentListener listener) 
	{
		super(listener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		recreation = (savedInstanceState != null) ? 2 : 0;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		if(recreation == 0)
		{
			getStatusView().show().text("Retrieving genres,\nPlease wait...");

			new GenresTask(tm, this, new GenresListener() 
			{
				@Override
				public void onGenres(final ArrayList<Genre> genres) 
				{				
					RecommendationFragment.this.genres = genres;
					setListNavigationMode();				
				}
			}).execute();
		}
		else
		{
			setListNavigationMode();
			setAdapter();
		}

		lvRecommendations.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Intent intent = new Intent(getActivity(), ShowActivity.class);
				intent.putExtra(TraktoidConstants.BUNDLE_RESULTS, adapter.getRecommendations());
				intent.putExtra(TraktoidConstants.BUNDLE_POSITION, position);
				startActivity(intent);
			}

		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_recommendation, null);

		lvRecommendations = (ListView)v.findViewById(R.id.listViewRecommendation);

		return v;
	}

	private void setListNavigationMode()
	{
		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] items = new String[genres.size()+1];
		items[0] = "All Genres";

		for(int i = 1; i < items.length; i++)
			items[i] = genres.get(i-1).name;

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.sherlock_spinner_item, items);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		getSherlockActivity().getSupportActionBar().setListNavigationCallbacks(spinnerAdapter, this);
	}

	private void setAdapter()
	{
		if(adapter == null)
		{
			adapter = new ListRecommendationAdapter(shows, getActivity());
			lvRecommendations.setAdapter(adapter);
		}
		else
			adapter.refreshData(shows);

		if(adapter.getCount() == 0)
			getStatusView().hide().text("No recommendations, strange...");
		else
			getStatusView().hide().text(null);

		adapter.setOnDismissListener(new DismissListener() 
		{
			@Override
			public void onDismiss(String tvdbId) 
			{
				new PostTask(tm, RecommendationFragment.this, tm.recommendationsService().dismissShow(Integer.valueOf(tvdbId)), new PostListener() 
				{
					@Override
					public void onComplete(Response r, boolean success) 
					{
						adapter.clear();
						createGetRecommendationsTask().execute();
					}
				}).execute();
			}
		});
	}

	private TraktTask createGetRecommendationsTask()
	{
		int index = getSherlockActivity().getSupportActionBar().getSelectedNavigationIndex();
		Genre genre = index <= 0 || index > genres.size() ? null : genres.get(index-1);
		getStatusView().show().text("Retrieving recommendations" + ((genre == null) ? "" : " in \"" + genre.name + "\"") + ",\nPlease wait...");

		ShowsBuilder builder = tm.recommendationsService().shows();

		if(genre != null)
			builder.genre(genre);			

		return commonTask = new ShowsTask(tm, this, new ShowsListener() 
		{
			@Override
			public void onShows(ArrayList<TvShow> shows) 
			{
				RecommendationFragment.this.shows = shows;
				setAdapter();
			}
		}, builder, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		shows = (ArrayList<TvShow>) savedInstanceState.get("shows");
		genres = (ArrayList<Genre>) savedInstanceState.get("genres");
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		toSave.putSerializable("shows", shows);
		toSave.putSerializable("genres", genres);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId)
	{
		//don't know why but this event is fired two times when activity is recreated, strange...
		if(recreation == 0)
		{
			if(adapter != null)
				adapter.clear();
			createGetRecommendationsTask().execute();
		}
		else 
			recreation--;
		return false;
	}
}
