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

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.florianmski.tracktoid.R;
import com.viewpagerindicator.TitleProvider;

public class PagerCalendarAdapter extends PagerAdapter implements TitleProvider
{
	private final static String calendarTitles[] = new String[]{"Premieres","My shows","Shows"};

	private Context context;
	private ArrayList<ListCalendarAdapter> adapters;

	public PagerCalendarAdapter(Context context, ArrayList<ListCalendarAdapter> adapters)
	{
		this.context = context;
		this.adapters = adapters;
	}

	@Override
	public int getCount() 
	{
		return adapters.size();
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

	@Override
	public void destroyItem(View pager, int position, Object view) 
	{
		((ViewPager)pager).removeView((View)view);
	}

	@Override
	public void finishUpdate(View container) {}

	@Override
	public Object instantiateItem(View pager, int position) 
	{
		View v = LayoutInflater.from(context).inflate(R.layout.pager_item_season, null, false);
		ListView lvEpisodes = (ListView)v.findViewById(R.id.listViewEpisodes);

		lvEpisodes.setAdapter(adapters.get(position));

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
