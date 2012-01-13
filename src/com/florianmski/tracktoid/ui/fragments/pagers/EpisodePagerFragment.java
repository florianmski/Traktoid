package com.florianmski.tracktoid.ui.fragments.pagers;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.pagers.PagerEpisodeAdapter;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBEpisodesTask;
import com.florianmski.tracktoid.trakt.tasks.post.WatchedEpisodesTask;
import com.florianmski.tracktoid.ui.activities.phone.ShoutsActivity;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class EpisodePagerFragment extends PagerFragment
{
	private String tvdbId;
	private String seasonId;
	
	public static EpisodePagerFragment newInstance(Bundle args)
	{
		EpisodePagerFragment f = new EpisodePagerFragment();
		f.setArguments(args);
		return f;
	}
	
	public EpisodePagerFragment() 
	{
		
	}
	
	public EpisodePagerFragment(FragmentListener listener) 
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
		
		getStatusView().show().text("Loading episodes,\nPlease wait...");

		setSubtitle(getArguments().getString(TraktoidConstants.BUNDLE_TITLE));

		tvdbId = getArguments().getString(TraktoidConstants.BUNDLE_TVDB_ID);
		seasonId = getArguments().getString(TraktoidConstants.BUNDLE_SEASON_ID);

		@SuppressWarnings("unchecked")
		ArrayList<TvShowEpisode> episodes = (ArrayList<TvShowEpisode>)getArguments().getSerializable(TraktoidConstants.BUNDLE_RESULTS);
		if(episodes == null)
			new DBEpisodesTask(getActivity(), new DBAdapter() 
			{
				@Override
				public void onDBEpisodes(List<TvShowEpisode> episodes) 
				{
					adapter = new PagerEpisodeAdapter(episodes, tvdbId, getSupportFragmentManager());
					
					if(((PagerEpisodeAdapter)adapter).isEmpty())
						getStatusView().hide().text("No episodes, this is strange...");
					else
						getStatusView().hide().text(null);
					
					initPagerFragment(adapter);
				}
			}, seasonId).execute();
		else
		{
			adapter = new PagerEpisodeAdapter(episodes, tvdbId, getSupportFragmentManager());
			
			if(((PagerEpisodeAdapter)adapter).isEmpty())
				getStatusView().hide().text("No episodes, this is strange...");
			else
				getStatusView().hide().text(null);
			
			initPagerFragment(adapter);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		//if seasonId is null, this episode is not in our db
		if(adapter == null || (!((PagerEpisodeAdapter) adapter).getEpisode(currentPagerPosition).watched && seasonId != null))
		{
			menu.add(0, R.id.action_bar_watched, 0, "Watched")
				.setIcon(R.drawable.ab_icon_eye)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		menu.add(0, R.id.action_bar_shouts, 0, "Shouts")
		.setIcon(R.drawable.ab_icon_shouts)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
				Utils.chooseBetweenSeenAndCheckin(new WatchedEpisodesTask(tm, this, tvdbId, e.season, e.number, !e.watched), getActivity());
			}
			return true;
		case R.id.action_bar_shouts :
			Intent i = new Intent(getActivity(), ShoutsActivity.class);
			i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, tvdbId);
			i.putExtra(TraktoidConstants.BUNDLE_EPISODE, ((PagerEpisodeAdapter) adapter).getEpisode(currentPagerPosition));
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onShowUpdated(TvShow show) 
	{
		if(show.tvdbId.equals(tvdbId) && adapter != null && seasonId != null)
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
		if(show.tvdbId.equals(tvdbId))
			getActivity().finish();
	}

	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);

		getSupportActivity().invalidateOptionsMenu();
		setTitle(((PagerEpisodeAdapter)adapter).getEpisode(position).title);
	}
}
