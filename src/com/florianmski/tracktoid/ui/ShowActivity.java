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

package com.florianmski.tracktoid.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.PagerShowAdapter;
import com.florianmski.tracktoid.trakt.tasks.UpdateShowsTask;
import com.jakewharton.trakt.entities.TvShow;

public class ShowActivity extends TraktPagerActivity
{
	//TODO onShowUpdated()
	private TvShow show;
	private boolean isExist;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		Utils.showLoading(this);
		setData();		
	}

	public void setData()
	{
		new Thread()
		{
			@Override
			@SuppressWarnings("unchecked")
			public void run()
			{
				final List<TvShow> shows = (List<TvShow>)getIntent().getSerializableExtra("results");

				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						Utils.removeLoading();
						setTitleIndicator(false);
						initPagerActivity(new PagerShowAdapter(shows, ShowActivity.this));
					}
				});
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		if(!isExist)
		{
			menu.add(0, R.id.action_bar_add, 0, "Add")
			.setIcon(R.drawable.ab_icon_add)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.action_bar_add :
			ArrayList<TvShow> shows = new ArrayList<TvShow>();
			shows.add(show);
			tm.addToQueue(new UpdateShowsTask(tm, this, shows));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);

		//check if user has already this show in his lib. If so hide the "add" button
		show = ((PagerShowAdapter)adapter).getItem(currentPagerPosition);
		setTitle(show.getTitle());
		isExist = show.getProgress() > 0;
		invalidateOptionsMenu();
	}
}
