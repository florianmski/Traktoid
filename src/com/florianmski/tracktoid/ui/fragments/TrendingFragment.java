package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.ui.fragments.pagers.items.ShowFragment;
import com.florianmski.tracktoid.widgets.coverflow.CoverFlow;
import com.florianmski.tracktoid.widgets.coverflow.CoverFlowImageAdapter;
import com.jakewharton.trakt.entities.TvShow;

public class TrendingFragment extends TraktFragment
{
	private CoverFlow cf;
	//	private TextView tvShowTitle;
	private ArrayList<TvShow> shows;

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
				//				tvShowTitle.setText(shows.get(position).title);
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
				getFragmentManager().beginTransaction().replace(R.id.fragment_show, ShowFragment.newInstance(shows.get(position))).commit();
			}
		});

		if(savedInstanceState == null)
		{
			commonTask = new ShowsTask(tm, this, new ShowsListener() 
			{
				@Override
				public void onShows(ArrayList<TvShow> shows) 
				{
					TrendingFragment.this.shows = shows;
					setAdapter();
				}
			}, tm.showService().trending(), false);
			commonTask.execute();
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
		//		tvShowTitle = (TextView)v.findViewById(R.id.textViewShowTitle);

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
		toSave.putSerializable(TraktoidConstants.BUNDLE_RESULTS, shows);
	}
}