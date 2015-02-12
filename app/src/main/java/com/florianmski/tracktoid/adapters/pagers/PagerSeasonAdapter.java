package com.florianmski.tracktoid.adapters.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.florianmski.tracktoid.ui.fragments.season.SeasonFragment;

public class PagerSeasonAdapter extends FragmentStatePagerAdapter
{
    private String showId;
    private String[] seasonIds;
    private int[] seasons;

	public PagerSeasonAdapter(String showId, String[] seasonIds, int[] seasons, FragmentManager fm)
	{
		super(fm);

        this.showId = showId;
        this.seasons = seasons;
        this.seasonIds = seasonIds;
	}

	@Override
	public int getCount() 
	{
		return seasons.length;
	}

	@Override
    public CharSequence getPageTitle(int position)
	{
		int season = seasons[position];
		return season == 0 ? "Specials" : "Season " + season;
	}
	
	@Override
	public Fragment getItem(int position) 
	{
		return SeasonFragment.newInstance(showId, seasonIds[position], seasons[position]);
	}
	
	@Override
	/** @see http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view */
	public int getItemPosition(Object object)
	{
	    return POSITION_NONE;
	}
}