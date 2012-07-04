package com.florianmski.tracktoid.adapters.pagers;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.florianmski.tracktoid.ui.fragments.calendar.PI_CalendarFragment;
import com.jakewharton.trakt.entities.CalendarDate;

public class PagerCalendarAdapter extends FragmentPagerAdapter
{
	public final static String calendarTitles[] = new String[]{"Premieres","My shows","Shows"};

	private ArrayList<ArrayList<CalendarDate>> calendars;
	
	public PagerCalendarAdapter(ArrayList<ArrayList<CalendarDate>> calendars, FragmentManager fm)
	{
		super(fm);
		this.calendars = calendars;
	}
	
	public void clear() 
	{
		calendars.clear();
		notifyDataSetChanged();
	}

	@Override
    public int getCount() 
	{
        return calendars.size();
    }

    @Override
    public Fragment getItem(int position) 
    {
    	//TODO why not calendarfragment.newinstance ?
    	return new PI_CalendarFragment(calendars.get(position));
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
