package com.florianmski.tracktoid.ui.fragments.trending;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.trakt.tasks.get.TraktItemsTask;
import com.florianmski.tracktoid.trakt.tasks.get.TraktItemsTask.TraktItemsListener;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.florianmski.tracktoid.ui.fragments.traktitems.PI_TraktItemShowFragment;
import com.florianmski.tracktoid.widgets.coverflow.CoverFlow;
import com.florianmski.tracktoid.widgets.coverflow.CoverFlowImageAdapter;
import com.jakewharton.trakt.entities.TvShow;

public class TrendingFragment extends TraktFragment
{
	//TODO do movies to
	
	private CoverFlow cf;
	private List<TvShow> shows;

	public static TrendingFragment newInstance(Bundle args)
	{
		TrendingFragment f = new TrendingFragment();
		f.setArguments(args);
		return f;
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

		getStatusView().show().text("Retrieving trending shows,\nPlease wait...");
		
		cf.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				setTitle(shows.get(position).title);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		cf.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				getFragmentManager().beginTransaction().replace(R.id.fragment_show, PI_TraktItemShowFragment.newInstance(shows.get(position))).commit();
			}
		});

		if(savedInstanceState == null)
		{
			new TraktItemsTask<TvShow>(this, new TraktItemsListener<TvShow>() 
			{
				@Override
				public void onTraktItems(List<TvShow> shows) 
				{
					TrendingFragment.this.shows = shows;
					setAdapter();
				}
			}, tm.showService().trending(), false).fire();
		}
		else
			setAdapter();
	}

	private void setAdapter()
	{
		CoverFlowImageAdapter adapter = new CoverFlowImageAdapter(shows);
		cf.setAdapter(adapter);

		if(adapter.isEmpty())
			getStatusView().hide().text("No trending shows, strange...");
		else
			getStatusView().hide().text(null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_trending, null);

		cf = (CoverFlow)v.findViewById(R.id.coverflow);

		return v;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		shows = (ArrayList<TvShow>) savedInstanceState.get(TraktoidConstants.BUNDLE_RESULTS);
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		toSave.putSerializable(TraktoidConstants.BUNDLE_RESULTS, (ArrayList<TvShow>) shows);
	}
}