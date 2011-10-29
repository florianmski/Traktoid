/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.PagerEpisodeAdapter;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBEpisodesTask;
import com.florianmski.tracktoid.trakt.tasks.WatchedEpisodesTask;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class EpisodeActivity extends TraktPagerActivity
{
	private String tvdbId;
	private String seasonId;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Utils.showLoading(this);

		setSubtitle(getIntent().getStringExtra("title"));

		tvdbId = getIntent().getStringExtra("tvdb_id");
		seasonId = getIntent().getStringExtra("seasonId");

		ArrayList<TvShowEpisode> episodes = (ArrayList<TvShowEpisode>)getIntent().getSerializableExtra("results");
		if(episodes == null)
			new DBEpisodesTask(this, new DBAdapter() 
			{
				@Override
				public void onDBEpisodes(List<TvShowEpisode> episodes) 
				{
					Utils.removeLoading();
					initPagerActivity(new PagerEpisodeAdapter(episodes, tvdbId, EpisodeActivity.this));
				}
			}, seasonId).execute();
		else
		{
			Utils.removeLoading();
			initPagerActivity(new PagerEpisodeAdapter(episodes, tvdbId, EpisodeActivity.this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		if(adapter == null || !((PagerEpisodeAdapter) adapter).getEpisode(currentPagerPosition).getWatched())
		{
			menu.add(0, R.id.action_bar_watched, 0, "Watched")
				.setIcon(R.drawable.ab_icon_eye)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return super.onCreateOptionsMenu(menu);
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
				invalidateOptionsMenu();
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
			new DBEpisodesTask(this, new DBAdapter()
			{
				@Override
				public void onDBEpisodes(List<TvShowEpisode> episodes) 
				{
					((PagerEpisodeAdapter)adapter).reloadData(episodes);
					invalidateOptionsMenu();
				}
			}, seasonId).execute();
	}

	@Override
	public void onShowRemoved(TvShow show)
	{
		if(show.getTvdbId().equals(tvdbId))
			finish();
	}

	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);

		invalidateOptionsMenu();
		setTitle(((PagerEpisodeAdapter)adapter).getEpisode(position).getTitle());
	}

}
