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

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.florianmski.tracktoid.ui.fragments.pagers.items.SeasonFragment;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.viewpagerindicator.TitleProvider;

public class PagerSeasonAdapter extends FragmentStatePagerAdapter implements TitleProvider
{
	private List<TvShowSeason> seasons;
	private String tvdbId;

	public PagerSeasonAdapter(List<TvShowSeason> seasons, String tvdbId, FragmentManager fm, Context context)
	{
		super(fm);
				
		this.seasons = seasons;
		this.tvdbId = tvdbId;
	}
	
	public void clear() 
	{
		seasons.clear();
		notifyDataSetChanged();
	}

	public void reloadData(List<TvShowSeason> seasons)
	{
		this.seasons = seasons;
		notifyDataSetChanged();
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
	
	public boolean isEmpty() 
	{
		return getCount() == 0;
	}
}