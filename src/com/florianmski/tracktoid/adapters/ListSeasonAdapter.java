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

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.jakewharton.trakt.entities.TvShowSeason;

public class ListSeasonAdapter extends BaseAdapter
{
	private List<TvShowSeason> seasons;
	private Context context;
	
	public ListSeasonAdapter(List<TvShowSeason> seasons, Context context)
	{
		this.seasons = seasons;
		this.context = context;
	}
	
	public void reloadData(List<TvShowSeason> seasons)
	{
		this.seasons.clear();
		this.seasons.addAll(seasons);
		Collections.reverse(this.seasons);
		this.notifyDataSetChanged();
	}
	
	public List<TvShowSeason> getSeasons()
	{
		return seasons;
	}
	
	@Override
	public int getCount() 
	{
		return seasons.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return null;
	}

	@Override
	public long getItemId(int position) 
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

        if (convertView == null) 
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_season, parent, false);
            holder = new ViewHolder();
            holder.tvSeason = (TextView)convertView.findViewById(R.id.textViewSeason);
            holder.tvLeft = (TextView)convertView.findViewById(R.id.textViewLeft);
            holder.ivBandeau = (ImageView)convertView.findViewById(R.id.imageViewBadge);
            
            convertView.setTag(holder);
        } 
        else
            holder = (ViewHolder) convertView.getTag();
        
        TvShowSeason season = seasons.get(position);
        
        int episodes = season.episodes.count;
        int episodesWatched = season.episodesWatched;
        
        holder.tvSeason.setText(season.season == 0 ? "Specials" : "Season " + season.season);
        holder.tvLeft.setText(episodesWatched + "/" + episodes);
        holder.ivBandeau.setVisibility(episodesWatched >= episodes ? View.VISIBLE : View.INVISIBLE);
		
        return convertView;
    }
    
    private static class ViewHolder 
    {
    	private TextView tvSeason;
    	private TextView tvLeft;
    	private ImageView ivBandeau;
    }

}
