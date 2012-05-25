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
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.services.ShowService.EpisodeSeenBuilder;
import com.jakewharton.trakt.services.ShowService.EpisodeUnseenBuilder;

public class WatchedEpisodesTask extends TraktTask<TvShow>
{
	private String tvdbId;
	private int [] seasons;
	private List<Map<Integer, Boolean>> listWatched = new ArrayList<Map<Integer,Boolean>>();
	private TvShow show;
	private boolean checkin;

	public WatchedEpisodesTask(Fragment fragment, String tvdbId, int [] seasons, List<Map<Integer, Boolean>> listWatched) 
	{
		super(fragment);

		this.tvdbId = tvdbId;
		this.seasons = seasons;
		this.listWatched.addAll(listWatched);
	}

	public WatchedEpisodesTask(Fragment fragment, String tvdbId, int season, int episode, boolean watched) 
	{
		super(fragment);

		this.tvdbId = tvdbId;
		this.seasons = new int[]{season};
		this.listWatched.add(new HashMap<Integer, Boolean>());
		this.listWatched.get(0).put(episode, watched);
	}

	public WatchedEpisodesTask(Fragment fragment, String tvdbId, List<TvShowSeason> seasons, boolean watched) 
	{
		super(fragment);

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

	public WatchedEpisodesTask init(boolean checkin)
	{
		this.checkin = checkin;
		return this;
	}

	@Override
	protected TvShow doTraktStuffInBackground()
	{
		showToast("Sending...", Toast.LENGTH_SHORT);

		if(checkin)
		{
			int index = 0;
			for(Map<Integer, Boolean> map : listWatched)
			{
				if(!map.isEmpty())
					break;
				index++;
			}
//			tm.showService().cancelCheckin().fire();
			Response r = tm.showService()
			.checkin(Integer.valueOf(tvdbId))
			.episode(listWatched.get(index).keySet().iterator().next())
			.season(seasons[index])
			.fire();
			
			if(r.error != null)
			{
				showToast(r.error, Toast.LENGTH_SHORT);
				return null;
			}
			else
				showToast(r.message, Toast.LENGTH_SHORT);
		}
		else
		{
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
			
			showToast("Send to Trakt!", Toast.LENGTH_SHORT);
		}


		DatabaseWrapper dbw = new DatabaseWrapper(context);

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
		
		return show;
	}

	@Override
	protected void onCompleted(TvShow show)
	{
		super.onCompleted(show);

		if(show != null)
			TraktTask.traktItemUpdated(show);
	}

	public String getTvdbId() {
		return tvdbId;
	}

	public void setTvdbId(String tvdbId) {
		this.tvdbId = tvdbId;
	}

	public int[] getSeasons() {
		return seasons;
	}

	public void setSeasons(int[] seasons) {
		this.seasons = seasons;
	}

	public List<Map<Integer, Boolean>> getListWatched() {
		return listWatched;
	}

	public void setListWatched(List<Map<Integer, Boolean>> listWatched) {
		this.listWatched = listWatched;
	}

	public TvShow getShow() {
		return show;
	}

	public void setShow(TvShow show) {
		this.show = show;
	}
}
