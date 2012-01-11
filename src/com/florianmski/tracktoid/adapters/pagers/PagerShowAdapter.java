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

import com.florianmski.tracktoid.adapters.AdapterInterface;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.ui.fragments.pagers.items.ShowFragment;
import com.jakewharton.trakt.entities.TvShow;

public class PagerShowAdapter extends FragmentStatePagerAdapter implements AdapterInterface
{
	private List<TvShow> shows;

	public PagerShowAdapter(List<TvShow> shows, FragmentManager fm, Context context)
	{
		super(fm);

		DatabaseWrapper dbw = new DatabaseWrapper(context);
		dbw.open();

		//if a show on this list is in the db, get infos so we can display them (watched, loved...)
		for(int i = 0; i < shows.size(); i++)
		{
			TvShow s = shows.get(i);
			if(dbw.showExist(s.tvdbId))
				shows.set(i, dbw.getShow(s.tvdbId));
		}

		dbw.close();

		this.shows = shows;		
	}

	@Override
	public void clear() 
	{
		shows.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() 
	{
		return shows.size();
	}

	@Override
	public Fragment getItem(int position) 
	{
		return ShowFragment.newInstance(shows.get(position));
	}

	public TvShow getTvShow(int position)
	{
		return shows.get(position);
	}

	@Override
	/** @see http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view */
	public int getItemPosition(Object object) 
	{
		return POSITION_NONE;
	}

	@Override
	public boolean isEmpty() 
	{
		return getCount() == 0;
	}
}