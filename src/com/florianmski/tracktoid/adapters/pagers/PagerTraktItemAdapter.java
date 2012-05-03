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

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.ui.fragments.pagers.items.MovieFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.ShowFragment;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;

public class PagerTraktItemAdapter<T extends TraktoidInterface<T>> extends FragmentStatePagerAdapter
{
	private List<T> items;

	public PagerTraktItemAdapter(List<T> items, FragmentManager fm, Context context)
	{
		super(fm);

		DatabaseWrapper dbw = new DatabaseWrapper(context);

		//if a show on this list is in the db, get infos so we can display them (watched, loved...)
		for(int i = 0; i < items.size(); i++)
		{
			T item = items.get(i);
			if(dbw.showExist(item.getId()))
			{
				if(item instanceof TvShow)
					items.set(i, (T) dbw.getShow(item.getId()));
				else if(item instanceof Movie)
					items.set(i, (T) dbw.getMovie(((Movie) item).url));
			}
		}

		dbw.close();

		this.items = items;		
	}

	public void clear() 
	{
		items.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() 
	{
		return items.size();
	}

	@Override
	public Fragment getItem(int position) 
	{
		if(items.get(position) instanceof TvShow)
			return ShowFragment.newInstance((TvShow)items.get(position));
		else
			return MovieFragment.newInstance((Movie)items.get(position));
	}
	
	public T getTraktItem(int position)
	{
		return items.get(position);
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