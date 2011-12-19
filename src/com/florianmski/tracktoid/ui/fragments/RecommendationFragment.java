package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.ListRecommendationAdapter;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.ui.activities.phone.RecommendationActivity;
import com.florianmski.tracktoid.ui.activities.phone.ShowActivity;
import com.florianmski.tracktoid.ui.fragments.TraktFragment.FragmentListener;
import com.jakewharton.trakt.entities.TvShow;

public class RecommendationFragment extends TraktFragment
{
	private ArrayList<TvShow> recommendations;
	
	private ListView lvRecommendations;
	
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

		commonTask = new ShowsTask(tm, this, new ShowsListener() 
		{
			@Override
			public void onShows(ArrayList<TvShow> shows) 
			{
				Utils.removeLoading();
				recommendations = shows;
				lvRecommendations.setAdapter(new ListRecommendationAdapter(shows, getActivity()));
			}
		}, tm.recommendationsService().shows(), false);
		commonTask.execute();

		lvRecommendations.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Intent intent = new Intent(getActivity(), ShowActivity.class);
				intent.putExtra("results", recommendations);
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
}
