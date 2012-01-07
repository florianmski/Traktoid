package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.ListRecommendationAdapter;
import com.florianmski.tracktoid.adapters.ListRecommendationAdapter.DismissListener;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.ui.activities.phone.ShowActivity;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;

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
		
		Utils.showLoading(getActivity());

		createGetRecommendationsTask();
		commonTask.execute();

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
	
	private void createGetRecommendationsTask()
	{
		commonTask = new ShowsTask(tm, this, new ShowsListener() 
		{
			@Override
			public void onShows(ArrayList<TvShow> shows) 
			{
				Utils.removeLoading();
				if(adapter == null)
				{
					adapter = new ListRecommendationAdapter(shows, getActivity());
					lvRecommendations.setAdapter(adapter);
					
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
									createGetRecommendationsTask();
									commonTask.execute();
								}
							}).execute();
						}
					});
				}
				else
					adapter.refreshData(shows);
			}
		}, tm.recommendationsService().shows(), false);
	}
}
