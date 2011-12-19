package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.PagerEpisodeAdapter;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBEpisodesTask;
import com.florianmski.tracktoid.trakt.tasks.WatchedEpisodesTask;
import com.florianmski.tracktoid.ui.activities.phone.EpisodeActivity;
import com.florianmski.tracktoid.ui.fragments.TraktFragment.FragmentListener;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class EpisodeFragment extends PagerFragment
{
	private String tvdbId;
	private String seasonId;
	
	public EpisodeFragment() {}
	
	public EpisodeFragment(FragmentListener listener) 
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

		setSubtitle(getActivity().getIntent().getStringExtra("title"));

		tvdbId = getActivity().getIntent().getStringExtra("tvdb_id");
		seasonId = getActivity().getIntent().getStringExtra("seasonId");

		ArrayList<TvShowEpisode> episodes = (ArrayList<TvShowEpisode>)getActivity().getIntent().getSerializableExtra("results");
		if(episodes == null)
			new DBEpisodesTask(getActivity(), new DBAdapter() 
			{
				@Override
				public void onDBEpisodes(List<TvShowEpisode> episodes) 
				{
					Utils.removeLoading();
					initPagerFragment(new PagerEpisodeAdapter(episodes, tvdbId, getActivity()));
				}
			}, seasonId).execute();
		else
		{
			Utils.removeLoading();
			initPagerFragment(new PagerEpisodeAdapter(episodes, tvdbId, getActivity()));
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		if(adapter == null || (!((PagerEpisodeAdapter) adapter).getEpisode(currentPagerPosition).getWatched() && seasonId != null))
		{
			menu.add(0, R.id.action_bar_watched, 0, "Watched")
				.setIcon(R.drawable.ab_icon_eye)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.action_bar_watched :
			//if adapter is not currently loading
			if(adapter != null)
			{
				getSupportActivity().invalidateOptionsMenu();
				TvShowEpisode e = ((PagerEpisodeAdapter) adapter).getEpisode(currentPagerPosition);
				tm.addToQueue(new WatchedEpisodesTask(tm, this, tvdbId, e.getSeason(), e.getNumber(), !e.getWatched()));
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onShowUpdated(TvShow show) 
	{
		if(show.getTvdbId().equals(tvdbId) && adapter != null)
			new DBEpisodesTask(getActivity(), new DBAdapter()
			{
				@Override
				public void onDBEpisodes(List<TvShowEpisode> episodes) 
				{
					((PagerEpisodeAdapter)adapter).reloadData(episodes);
					getSupportActivity().invalidateOptionsMenu();
				}
			}, seasonId).execute();
	}

	@Override
	public void onShowRemoved(TvShow show)
	{
		if(show.getTvdbId().equals(tvdbId))
			getActivity().finish();
	}

	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);

		getSupportActivity().invalidateOptionsMenu();
		setTitle(((PagerEpisodeAdapter)adapter).getEpisode(position).getTitle());
	}
}
