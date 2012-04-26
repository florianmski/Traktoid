package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.WatchedModeManager;
import com.florianmski.tracktoid.adapters.lists.ListEpisodeAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.ui.activities.phone.EpisodeActivity;
import com.florianmski.tracktoid.ui.fragments.pagers.SeasonPagerFragment.OnWatchedModeListener;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public class SeasonFragment extends PagerItemFragment implements OnWatchedModeListener
{
	private TvShowSeason season;
	private String tvdbId;
	private ListView lvEpisodes;

	public static SeasonFragment newInstance(Bundle args)
	{
		SeasonFragment f = new SeasonFragment();
		f.setArguments(args);
		return f;
	}

	public static SeasonFragment newInstance(TvShowSeason season, String tvdbId)
	{		
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_SEASON, season);
		args.putString(TraktoidConstants.BUNDLE_TVDB_ID, tvdbId);

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
			tvdbId = getArguments().getString(TraktoidConstants.BUNDLE_TVDB_ID);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		getStatusView().show().text("Loading season " + season.season + ",\nPlease wait...");
		
		//TODO proper task
		new Thread()
		{
			@Override
			public void run()
			{
				DatabaseWrapper dbw = getDBWrapper();
				List<TvShowEpisode> episodes = dbw.getEpisodes(season.url);

				final ListEpisodeAdapter adapter = new ListEpisodeAdapter(episodes, getActivity(), tvdbId);

				getActivity().runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						lvEpisodes.setAdapter(adapter);
						WatchedModeManager.getInstance().addListener(SeasonFragment.this);
						
						if(adapter.isEmpty())
							getStatusView().hide().text("No episodes for this season");
						else
							getStatusView().hide().text(null);
					}
				});
			}
		}.start();
	}

	public ListEpisodeAdapter getAdapter()
	{
		return lvEpisodes != null ? (ListEpisodeAdapter) lvEpisodes.getAdapter() : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_season, container, false);

		lvEpisodes = (ListView)v.findViewById(R.id.listViewEpisodes);
		ImageView ivBackground = (ImageView)v.findViewById(R.id.imageViewBackground);

		lvEpisodes.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int episode, long arg3) 
			{
				Intent i = new Intent(getActivity(), EpisodeActivity.class);
				i.putExtra(TraktoidConstants.BUNDLE_SEASON_ID, season.url);
				i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, tvdbId);
				i.putExtra(TraktoidConstants.BUNDLE_TITLE, getArguments().getString(TraktoidConstants.BUNDLE_TITLE));
				i.putExtra(TraktoidConstants.BUNDLE_POSITION, episode);
				startActivity(i);
			}
		});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		if(getAdapter() != null && getAdapter().getWatchedMode())
		{
			menu.add(0, R.id.menu_all, 0, "All")
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

			menu.add(0, R.id.menu_none, 0, "None")
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.menu_all :
			if(getAdapter() != null)
				getAdapter().checkBoxSelection(true);
			return true;
		case R.id.menu_none :
			if(getAdapter() != null)
				getAdapter().checkBoxSelection(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		WatchedModeManager.getInstance().removeListener(this);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

	@Override
	public void setWatchedMode(boolean on) 
	{
		if(getAdapter() != null)
		{
			getAdapter().setWatchedMode(on);
			getSherlockActivity().invalidateOptionsMenu();
		}
	}

	@Override
	public Map<Integer, Boolean> getWatchedList() 
	{
		return (getAdapter() != null) ? getAdapter().getListWatched() : new HashMap<Integer, Boolean>();
	}

	@Override
	public void checkAll(String url) 
	{
		if(url.equals(season.url) && getAdapter() != null)
			getAdapter().checkBoxSelection(true);
	}

	@Override
	public void checkNone(String url) 
	{
		if(url.equals(season.url) && getAdapter() != null)
			getAdapter().checkBoxSelection(false);
	}
}