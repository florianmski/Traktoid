package com.florianmski.tracktoid.adapters.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.florianmski.tracktoid.ui.fragments.calendar.PI_CalendarFragment;

public class PagerCalendarAdapter extends FragmentPagerAdapter
{
	public final static String calendarTitles[] = new String[]{"Premieres","My shows","Shows"};
	
	public PagerCalendarAdapter(FragmentManager fm)
	{
		super(fm);
	}

	@Override
    public int getCount() 
	{
        return calendarTitles.length;
    }

    @Override
    public Fragment getItem(int position) 
    {
    	return PI_CalendarFragment.newInstance(null);
    }

	@Override
    public CharSequence getPageTitle(int position)
	{
		return calendarTitles[position];
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
