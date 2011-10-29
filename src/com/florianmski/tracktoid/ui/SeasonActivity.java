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

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.ListEpisodeAdapter;
import com.florianmski.tracktoid.adapters.PagerListEpisodesAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.trakt.tasks.WatchedEpisodesTask;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowSeason;

public class SeasonActivity extends TraktPagerActivity
{
	private boolean watchedMode = false;
	private String tvdbId;
	private List<TvShowSeason> seasons;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		Utils.showLoading(this);
		setTitle(getIntent().getStringExtra("title"));
		
		tvdbId = getIntent().getStringExtra("tvdb_id");

//		new DBSeasonsTask(this, new DBAdapter() 
//		{
//			@Override
//			public void onDBSeasons(List<TvShowSeason> seasons) 
//			{
//				SeasonActivity.this.seasons = seasons;
//				Utils.removeLoading();
//				initPagerActivity(new PagedListEpisodesAdapter(seasons, tvdbId, SeasonActivity.this));
//			}
//		}, tvdbId, true, true).execute();

		setData();
	}

	//don't know why but using this thread is like 3 time faster than using an asynctask doing the same thing (???)
	public void setData()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				DatabaseWrapper dbw = new DatabaseWrapper(SeasonActivity.this);
				dbw.open();
				String tvdb_id = getIntent().getStringExtra("tvdb_id");
				List<TvShowSeason> seasons = dbw.getSeasons(tvdb_id, true, true);
				SeasonActivity.this.seasons = seasons;
				dbw.close();

				adapter = new PagerListEpisodesAdapter(seasons, tvdb_id, SeasonActivity.this);

				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						Utils.removeLoading();
						initPagerActivity(adapter);
					}
				});
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		if(watchedMode)
		{
			menu.add(0, R.id.action_bar_send, 0, "Send")
				.setIcon(R.drawable.ab_icon_send)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

			menu.add(0, R.id.menu_all, 0, "All")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

			menu.add(0, R.id.menu_none, 0, "None")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}
		menu.add(0, R.id.action_bar_watched, 0, "Watched")
			.setIcon(R.drawable.ab_icon_eye)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
				watchedMode = !watchedMode;
				invalidateOptionsMenu();
				((PagerListEpisodesAdapter) adapter).setWatchedMode(watchedMode);
			}
			return true;
		case R.id.action_bar_send :
		{
			List<Map<Integer, Boolean>> listWatched = ((PagerListEpisodesAdapter) adapter).getListWatched();
			int[] seasons = ((PagerListEpisodesAdapter) adapter).getSeasons();

			boolean isEmpty = true;
			for(int i = 0; i < listWatched.size(); i++)
				isEmpty &= listWatched.get(i).isEmpty();

			if(isEmpty)
				Toast.makeText(this, "Nothing to send...", Toast.LENGTH_SHORT).show();
			else
				tm.addToQueue(new WatchedEpisodesTask(tm, this, tvdbId, seasons, listWatched));

			watchedMode = !watchedMode;
			invalidateOptionsMenu();
			((PagerListEpisodesAdapter) adapter).setWatchedMode(watchedMode);
		}
		return true;
		case R.id.menu_all :
			checkBoxSelection(true);
			return true;
		case R.id.menu_none :
			checkBoxSelection(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onShowUpdated(TvShow show)
	{
		if(show.getTvdbId().equals(tvdbId) && adapter != null && show.getSeasons() != null)
			((PagerListEpisodesAdapter) adapter).reloadData(show.getSeasons());
	}

	@Override
	public void onShowRemoved(TvShow show)
	{
		if(show.getTvdbId().equals(tvdbId))
			finish();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return watchedMode ? super.onPrepareOptionsMenu(menu) : false;
	}

	public void checkBoxSelection(boolean checked)
	{
		ListEpisodeAdapter a = ((PagerListEpisodesAdapter) adapter).getAdapters().get(currentPagerPosition);
		a.checkBoxSelection(checked);
		a.notifyDataSetChanged();
	}
	
	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);
		
		setBackground(new Image(tvdbId, seasons.get(position).getSeason()));
	}
}
