package com.florianmski.tracktoid.adapters.lists;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.jakewharton.trakt.entities.TvShowSeason;

public class ListSeasonAdapter extends RootAdapter<TvShowSeason>
{	
	private ListCheckerManager<TvShowSeason> lcm;
	
	public ListSeasonAdapter(List<TvShowSeason> seasons, Context context, ListCheckerManager<TvShowSeason> lcm)
	{
		super(context, seasons);
		
		this.lcm = lcm;
	}
	
	@Override
	public void refreshItems(List<TvShowSeason> seasons)
	{
		this.items.clear();
		this.items.addAll(seasons);
		Collections.reverse(this.items);
		this.notifyDataSetChanged();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public View doGetView(int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

        if (convertView == null) 
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_season, parent, false);
            holder = new ViewHolder();
            holder.tvSeason = (TextView)convertView.findViewById(R.id.textViewSeason);
            holder.tvLeft = (TextView)convertView.findViewById(R.id.textViewLeft);
            holder.bv = (BadgesView)convertView.findViewById(R.id.badgesLayout);
            
            convertView.setTag(holder);
        } 
        else
            holder = (ViewHolder) convertView.getTag();
        
        TvShowSeason season = getItem(position);
        
        int episodes = season.episodes.count;
        int episodesWatched = season.episodesWatched;
        
        holder.tvSeason.setText(season.season == 0 ? "Specials" : "Season " + season.season);
        holder.tvLeft.setText(episodesWatched + "/" + episodes);
        
        holder.bv.initialize();
        holder.bv.toggleWatched(episodesWatched >= episodes);
		
        return lcm.checkView(season, convertView);
    }
    
    private static class ViewHolder 
    {
    	private TextView tvSeason;
    	private TextView tvLeft;
    	@SuppressWarnings("rawtypes")
		private BadgesView bv;
    }

}
