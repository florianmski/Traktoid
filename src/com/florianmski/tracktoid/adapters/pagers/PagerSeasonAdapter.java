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

package com.florianmski.tracktoid.adapters.pagers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.florianmski.tracktoid.adapters.AdapterInterface;
import com.florianmski.tracktoid.adapters.lists.ListEpisodeAdapter;
import com.florianmski.tracktoid.ui.fragments.pagers.items.SeasonFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.SeasonFragment.OnConstructionListener;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.viewpagerindicator.TitleProvider;

public class PagerSeasonAdapter extends FragmentStatePagerAdapter implements TitleProvider, OnConstructionListener, AdapterInterface
{
	private List<TvShowSeason> seasons;
	private List<ListEpisodeAdapter> adapters = new ArrayList<ListEpisodeAdapter>();
	private String tvdbId;

	public PagerSeasonAdapter(List<TvShowSeason> seasons, String tvdbId, FragmentManager fm, Context context)
	{
		super(fm);
		
		SeasonFragment.setListener(this);
		
		this.seasons = seasons;
		this.tvdbId = tvdbId;

		for(TvShowSeason s : seasons)
			adapters.add(new ListEpisodeAdapter(s.episodes.episodes, context, tvdbId));
	}
	
	@Override
	public void clear() 
	{
		seasons.clear();
		adapters.clear();
		notifyDataSetChanged();
	}

	public void reloadData(List<TvShowSeason> seasons)
	{
		this.seasons = seasons;
				
		for(int i = 0; i < seasons.size(); i++)
		{
			adapters.get(i).reloadData(seasons.get(i).episodes.episodes);
			adapters.get(i).notifyDataSetChanged();
		}
	}

	public void setWatchedMode(boolean watchedMode)
	{
		for(ListEpisodeAdapter a : adapters)
		{
			a.setWatchedMode(watchedMode);
			a.notifyDataSetChanged();
		}
	}

	public List<ListEpisodeAdapter> getAdapters()
	{
		return adapters;
	}

	public List<Map<Integer, Boolean>> getListWatched()
	{
		List<Map<Integer, Boolean>> listWatched = new ArrayList<Map<Integer,Boolean>>();
		for(int i = 0; i < adapters.size(); i++)
			listWatched.add(adapters.get(i).getListWatched());
		
		return listWatched;
	}

	public int[] getSeasons()
	{
		int[] seasons = new int[this.seasons.size()];
		for(int i = 0; i < seasons.length; i++)
			seasons[i] = this.seasons.get(i).season;

		return seasons;
	}

	@Override
	public int getCount() 
	{
		return seasons.size();
	}

	@Override
	public String getTitle(int position) 
	{
		int season = seasons.get(position).season;
		return season == 0 ? "Specials" : "Season "+season;
	}
	
	@Override
	public Fragment getItem(int position) 
	{
		return SeasonFragment.newInstance(seasons.get(position), tvdbId);
	}
	
	@Override
	/** @see http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view */
	public int getItemPosition(Object object) 
	{
	    return POSITION_NONE;
	}
	
	@Override
	public ListEpisodeAdapter iNeedAdapter(int season) 
	{
		for(int i = 0; i < seasons.size(); i++)
		{
			if(season == seasons.get(i).season)
				return adapters.get(i);
		}
		return null;
	}
	
	@Override
	public boolean isEmpty() 
	{
		return getCount() == 0;
	}
}