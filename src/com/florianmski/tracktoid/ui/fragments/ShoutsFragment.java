package com.florianmski.tracktoid.ui.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.ListShoutsAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.ShoutsGetTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShoutsGetTask.ShoutsListener;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.Shout;

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

		commonTask = new ShoutsGetTask(tm, this, tvdbId, new ShoutsListener() 
		{
			@Override
			public void onShouts(List<Shout> shouts) 
			{
				Utils.removeLoading();
				ShoutsFragment.this.shouts = shouts;
				lvShouts.setAdapter(new ListShoutsAdapter(shouts, getActivity()));
			}
		});
		commonTask.execute();

		lvShouts.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				//TODO
			}
		});
		
		new PostTask(tm, this, tm.shoutService().show(2).shout(""), new PostListener() 
		{
			@Override
			public void onComplete(Response r) 
			{
				
			}
		}).execute();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_shouts, null);
		
		lvShouts = (ListView)v.findViewById(R.id.listViewShouts);
		
		return v;
	}
}
