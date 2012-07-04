package com.florianmski.tracktoid.adapters.pagers;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.florianmski.tracktoid.ui.fragments.season.PI_SeasonFragment;
import com.jakewharton.trakt.entities.TvShowSeason;

public class PagerSeasonAdapter extends FragmentStatePagerAdapter
{
	private List<TvShowSeason> seasons;

	public PagerSeasonAdapter(List<TvShowSeason> seasons, FragmentManager fm)
	{
		super(fm);
				
		this.seasons = seasons;
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
    public CharSequence getPageTitle(int position)
	{
		int season = seasons.get(position).season;
		return season == 0 ? "Specials" : "Season "+season;
	}
	
	@Override
	public Fragment getItem(int position) 
	{
		return PI_SeasonFragment.newInstance(seasons.get(position), position);
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