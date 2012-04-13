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
import com.florianmski.tracktoid.ui.fragments.pagers.items.MovieFragment;
import com.jakewharton.trakt.entities.Movie;

public class PagerMovieAdapter extends FragmentStatePagerAdapter implements AdapterInterface
{
	private List<Movie> movies;

	public PagerMovieAdapter(List<Movie> movies, FragmentManager fm, Context context)
	{
		super(fm);

		DatabaseWrapper dbw = new DatabaseWrapper(context);

		//if a show on this list is in the db, get infos so we can display them (watched, loved...)
		for(int i = 0; i < movies.size(); i++)
		{
			Movie m = movies.get(i);
			if(dbw.showExist(m.url))
				movies.set(i, dbw.getMovie(m.url));
		}

		dbw.close();

		this.movies = movies;		
	}

	@Override
	public void clear() 
	{
		movies.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() 
	{
		return movies.size();
	}

	@Override
	public Fragment getItem(int position) 
	{
		return MovieFragment.newInstance(movies.get(position));
	}

	public Movie getMovie(int position)
	{
		return movies.get(position);
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