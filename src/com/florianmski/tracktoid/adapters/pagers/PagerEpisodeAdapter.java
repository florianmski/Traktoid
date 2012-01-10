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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.ui.fragments.pagers.items.EpisodeFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.ShowFragment;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.viewpagerindicator.TitleProvider;

public class PagerEpisodeAdapter extends FragmentStatePagerAdapter implements TitleProvider
{
	private List<TvShowEpisode> episodes;
	private String tvdbId;

	public PagerEpisodeAdapter(List<TvShowEpisode> episodes, String tvdb_id, FragmentManager fm)
	{
		super(fm);
		this.episodes = episodes;
		this.tvdbId = tvdb_id;
	}
	
	public void reloadData(List<TvShowEpisode> episodes)
	{
		this.episodes = episodes;
		this.notifyDataSetChanged();
	}
	
	public TvShowEpisode getEpisode(int position)
	{
		return episodes.get(position);
	}

	@Override
	public int getCount() 
	{
		return episodes.size();
	}

	@Override
	public String getTitle(int position) 
	{
		return "S" + Utils.addZero(episodes.get(position).season) + " E" + Utils.addZero(episodes.get(position).number);
	}

	@Override
	public Fragment getItem(int position) 
	{
		return EpisodeFragment.newInstance(episodes.get(position), tvdbId);
	}

	@Override
	/** @see http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view */
	public int getItemPosition(Object object) 
	{
	    return POSITION_NONE;
	}
}
