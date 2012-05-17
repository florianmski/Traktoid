/*
est * Copyright 2011 Florian Mierzejewski
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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.florianmski.tracktoid.ui.fragments.pagers.items.PI_CalendarFragment;
import com.jakewharton.trakt.entities.CalendarDate;
import com.viewpagerindicator.TitleProvider;

public class PagerCalendarAdapter extends FragmentPagerAdapter implements TitleProvider
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
	public String getTitle(int position) 
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
