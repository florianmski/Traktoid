package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.OnNavigationListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.ListRecommendationAdapter;
import com.florianmski.tracktoid.adapters.ListRecommendationAdapter.DismissListener;
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

public class RecommendationFragment extends TraktFragment
{	
	private ListView lvRecommendations;
	
	private ListRecommendationAdapter adapter;
	
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
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		Utils.setEmptyView(lvRecommendations, getActivity());
//		Utils.showLoading(getActivity());

//		createGetRecommendationsTask(null);
//		commonTask.execute();
		
		new GenresTask(tm, this, new GenresListener() 
		{
			@Override
			public void onGenres(final ArrayList<Genre> genres) 
			{
				getSupportActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

				String[] items = new String[genres.size()+1];
				items[0] = "All Genres";
				
				for(int i = 1; i < items.length; i++)
					items[i] = genres.get(i-1).name;
				
				ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.abs__simple_spinner_item, items);
				spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				getSupportActivity().getSupportActionBar().setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() 
				{
					@Override
					public boolean onNavigationItemSelected(int position, long itemId) 
					{
						Genre g = position == 0 ? null : genres.get(position-1);
						
						if(adapter != null)
							adapter.clear();
						createGetRecommendationsTask(g);
						commonTask.execute();
						return false;
					}
				});
			}
		}).execute();
		
		lvRecommendations.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Intent intent = new Intent(getActivity(), ShowActivity.class);
				intent.putExtra("results", adapter.getRecommendations());
				intent.putExtra("position", position);
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
	
	private void createGetRecommendationsTask(final Genre genre)
	{
		ShowsBuilder builder = tm.recommendationsService().shows();
		
		if(genre != null)
			builder.genre(genre);			
		
		commonTask = new ShowsTask(tm, this, new ShowsListener() 
		{
			@Override
			public void onShows(ArrayList<TvShow> shows) 
			{
//				Utils.removeLoading();
				if(adapter == null)
				{
					adapter = new ListRecommendationAdapter(shows, getActivity());
					lvRecommendations.setAdapter(adapter);
				}
				else
					adapter.refreshData(shows);
				
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
								createGetRecommendationsTask(genre);
								commonTask.execute();
							}
						}).execute();
					}
				});
			}
		}, builder, false);
	}
}
