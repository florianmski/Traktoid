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

package com.florianmski.tracktoid.trakt.tasks.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.services.ShowService.EpisodeSeenBuilder;
import com.jakewharton.trakt.services.ShowService.EpisodeUnseenBuilder;

public class WatchedEpisodesTask extends TraktTask
{
	private String tvdbId;
	private int [] seasons;
	private List<Map<Integer, Boolean>> listWatched = new ArrayList<Map<Integer,Boolean>>();
	private TvShow show;

	public WatchedEpisodesTask(TraktManager tm, Fragment fragment, String tvdbId, int [] seasons, List<Map<Integer, Boolean>> listWatched) 
	{
		super(tm, fragment);
		
		this.tvdbId = tvdbId;
		this.seasons = seasons;
		this.listWatched.addAll(listWatched);
	}

	public WatchedEpisodesTask(TraktManager tm, Fragment fragment, String tvdbId, int season, int episode, boolean watched) 
	{
		super(tm, fragment);
		
		this.tvdbId = tvdbId;
		this.seasons = new int[]{season};
		this.listWatched.add(new HashMap<Integer, Boolean>());
		this.listWatched.get(0).put(episode, watched);
	}
	
	public WatchedEpisodesTask(TraktManager tm, Fragment fragment, String tvdbId, List<TvShowSeason> seasons, boolean watched) 
	{
		super(tm, fragment);
		
		this.tvdbId = tvdbId;
		this.seasons = new int[seasons.size()];
		
		for(int i = 0; i < seasons.size(); i++)
		{
			this.seasons[i] = seasons.get(i).season;
			this.listWatched.add(new HashMap<Integer, Boolean>());
			for(int j = 1; j <= seasons.get(i).episodes.count; j++)
				this.listWatched.get(i).put(j, watched);
		}
	}
	
	@Override
	protected boolean doTraktStuffInBackground()
	{
		showToast("Sending...", Toast.LENGTH_SHORT);
		
		EpisodeSeenBuilder seenBuilder = tm.showService().episodeSeen(Integer.valueOf(tvdbId));
		EpisodeUnseenBuilder unseenBuilder = tm.showService().episodeUnseen(Integer.valueOf(tvdbId));

		int seenEpisodes = 0;
		int unseenEpisodes = 0;

		for(int i = 0; i < seasons.length; i++)
		{
			Map<Integer, Boolean> listEpisodes = listWatched.get(i);
			for (Iterator<Integer> it = listEpisodes.keySet().iterator(); it.hasNext() ;)
			{
				Integer episode = it.next();
				Boolean watched = listEpisodes.get(episode);

				if(watched)
				{
					seenEpisodes++;
					seenBuilder.episode(seasons[i], episode);
				}
				else
				{
					unseenEpisodes++;
					unseenBuilder.episode(seasons[i], episode);
				}
			}
		}

		if(seenEpisodes > 0)
			seenBuilder.fire();
		if(unseenEpisodes > 0)
			unseenBuilder.fire();

		if(seenEpisodes > 0 || unseenEpisodes > 0)
		{
			DatabaseWrapper dbw = new DatabaseWrapper(context);
			dbw.open();

			for(int i = 0; i < seasons.length; i++)
			{
				Map<Integer, Boolean> listEpisodes = listWatched.get(i);
				for (Iterator<Integer> it = listEpisodes.keySet().iterator() ; it.hasNext() ; )
				{
					Integer episode = it.next();
					Boolean watched = listEpisodes.get(episode);

					dbw.markEpisodeAsWatched(watched, tvdbId, seasons[i], episode);
				}
			}

			dbw.refreshPercentage(tvdbId);
			show = dbw.getShow(tvdbId);			
			show.seasons = dbw.getSeasons(tvdbId, true, true);
			
			dbw.close();
			
			showToast("Send to Trakt!", Toast.LENGTH_SHORT);
		}
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success && show != null)
			tm.onShowUpdated(show);
	}
}
