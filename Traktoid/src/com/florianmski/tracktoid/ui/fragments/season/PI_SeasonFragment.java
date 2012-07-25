package com.florianmski.tracktoid.ui.fragments.season;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktItemsRemovedEvent;
import com.florianmski.tracktoid.TraktItemsUpdatedEvent;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.adapters.lists.ListEpisodeAdapter;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBSeasonTask;
import com.florianmski.tracktoid.ui.activities.TraktItemsActivity;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.florianmski.tracktoid.widgets.CheckableListView;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.squareup.otto.Subscribe;

public class PI_SeasonFragment extends TraktFragment
{
	private TvShowSeason season;
	private int position;
	private CheckableListView<TvShowEpisode> lvEpisodes;
	private ListEpisodeAdapter adapter;

	public static PI_SeasonFragment newInstance(Bundle args)
	{
		PI_SeasonFragment f = new PI_SeasonFragment();
		f.setArguments(args);
		return f;
	}

	public static PI_SeasonFragment newInstance(TvShowSeason season, int position)
	{		
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_SEASON, season);
		args.putSerializable(TraktoidConstants.BUNDLE_POSITION, position);

		return newInstance(args);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if(getArguments() != null)
		{
			season = (TvShowSeason) getArguments().getSerializable(TraktoidConstants.BUNDLE_SEASON);
			position = getArguments().getInt(TraktoidConstants.BUNDLE_POSITION);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		getStatusView().show().text("Loading season " + season.season + ",\nPlease wait...");

		ListCheckerManager.getInstance().addListener(lvEpisodes);
		lvEpisodes.initialize(this, position, ListCheckerManager.<TvShowEpisode>getInstance());

		new DBSeasonTask(getSherlockActivity(), season.url, new DBAdapter() 
		{
			public void onDBSeason(List<TvShowEpisode> episodes)
			{
				adapter = new ListEpisodeAdapter(episodes, season.url, getActivity());

				lvEpisodes.setAdapter(adapter);

				if(adapter.isEmpty())
					getStatusView().hide().text("No episodes for this season");
				else
					getStatusView().hide().text(null);
			}
		}).execute();		
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_season, container, false);

		lvEpisodes = (CheckableListView<TvShowEpisode>)v.findViewById(R.id.listViewEpisodes);

		lvEpisodes.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
			{
				Intent i = new Intent(getActivity(), TraktItemsActivity.class);
				i.putExtra(TraktoidConstants.BUNDLE_POSITION, position);
				i.putExtra(TraktoidConstants.BUNDLE_RESULTS, (Serializable)((RootAdapter<TvShowEpisode>) lvEpisodes.getAdapter()).getItems());
				startActivity(i);
			}
		});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(0, R.id.action_bar_multiple_selection, 0, "Multiple selection")
		.setIcon(R.drawable.ab_icon_mark)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if(item.getItemId() == R.id.action_bar_multiple_selection)
			lvEpisodes.start();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() 
	{
		ListCheckerManager.getInstance().removeListener(lvEpisodes);
		super.onDestroy();
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

	@Subscribe
	public void onTraktItemsUpdated(TraktItemsUpdatedEvent<TvShowEpisode> event) 
	{
		List<TvShowEpisode> traktItems = event.getTraktItems(this);
		if(adapter != null && traktItems != null)
			adapter.remove(traktItems);
	}

	@Subscribe
	public void onTraktItemsRemoved(TraktItemsRemovedEvent<TvShowEpisode> event) 
	{
		List<TvShowEpisode> traktItems = event.getTraktItems(this);
		if(adapter != null && traktItems != null)
			adapter.updateItems(traktItems);
	}
}