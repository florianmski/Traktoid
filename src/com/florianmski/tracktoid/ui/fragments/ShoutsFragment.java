package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.ListRecommendationAdapter;
import com.florianmski.tracktoid.adapters.ListShoutsAdapter;
import com.florianmski.tracktoid.trakt.tasks.ShoutsGetTask;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.ShoutsGetTask.ShoutsListener;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.ui.activities.phone.ShowActivity;
import com.florianmski.tracktoid.ui.fragments.TraktFragment.FragmentListener;
import com.jakewharton.trakt.entities.Shout;
import com.jakewharton.trakt.entities.TvShow;

public class ShoutsFragment extends TraktFragment
{
	private List<Shout> shouts;
	
	private ListView lvShouts;
	
	public ShoutsFragment() {}
	
	public ShoutsFragment(FragmentListener listener) 
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
		
		String tvdbId = getActivity().getIntent().getStringExtra("tvdbId");

		new ShoutsGetTask(tm, this, tvdbId, new ShoutsListener() 
		{
			@Override
			public void onShouts(List<Shout> shouts) 
			{
				Utils.removeLoading();
				ShoutsFragment.this.shouts = shouts;
				lvShouts.setAdapter(new ListShoutsAdapter(shouts, getActivity()));
			}
		}).execute();

		lvShouts.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				//TODO
			}

		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_shouts, null);
		
		lvShouts = (ListView)v.findViewById(R.id.listViewShouts);
		
		return v;
	}
}
