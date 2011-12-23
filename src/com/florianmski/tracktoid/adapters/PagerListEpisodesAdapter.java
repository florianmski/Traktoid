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

package com.florianmski.tracktoid.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.activities.phone.EpisodeActivity;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.viewpagerindicator.TitleProvider;

public class PagerListEpisodesAdapter extends PagerAdapter implements TitleProvider
{
	private List<TvShowSeason> seasons;
	private List<ListEpisodeAdapter> adapters = new ArrayList<ListEpisodeAdapter>();
	private String tvdbId;
	private Activity context;

	public PagerListEpisodesAdapter(List<TvShowSeason> seasons, String tvdbId, Activity context)
	{
		this.seasons = seasons;
		this.tvdbId = tvdbId;
		this.context = context;

		for(TvShowSeason s : seasons)
			adapters.add(new ListEpisodeAdapter(s.episodes.episodes, context, tvdbId));
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
	/** @see http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view */
	public int getItemPosition(Object object) 
	{
	    return POSITION_NONE;
	}

	@Override
	public void destroyItem(View pager, int position, Object view) 
	{
		((ViewPager)pager).removeView((View)view);
	}

	@Override
	public void finishUpdate(View container) {}

	@Override
	public Object instantiateItem(View pager, final int position) 
	{
		View v = LayoutInflater.from(context).inflate(R.layout.pager_item_season, null, false);
		ListView lvEpisodes = (ListView)v.findViewById(R.id.listViewEpisodes);
		ImageView ivBackground = (ImageView)v.findViewById(R.id.imageViewBackground);

		lvEpisodes.setAdapter(adapters.get(position));

		lvEpisodes.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int episode, long arg3) 
			{
				Intent i = new Intent(context, EpisodeActivity.class);
				i.putExtra("seasonId", seasons.get(position).url);
				i.putExtra("tvdb_id", tvdbId);
				i.putExtra("title", context.getIntent().getStringExtra("title"));
				i.putExtra("position", episode);
				context.startActivity(i);
			}
		});

		((ViewPager)pager).addView(v, 0);

		return v;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) 
	{
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {}

	@Override
	public Parcelable saveState() 
	{
		return null;
	}

	@Override
	public void startUpdate(View container) {}

}