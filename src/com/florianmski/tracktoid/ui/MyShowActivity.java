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
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.ListSeasonAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBSeasonsTask;
import com.florianmski.tracktoid.image.Fanart;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.entities.TvShow;

public class MyShowActivity extends TraktActivity
{
	private final static int PERCENTAGE_STEP = 2;

	private ProgressBar sbProgress;
	private TextView tvProgress;
	private ListView lvSeasons;
	private ImageView ivBackground;

	private LinearLayout llNextEpisode;
	
	private ListSeasonAdapter adapter;

	private String tvdbId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_show);

		Utils.showLoading(this);
		setTitle(getIntent().getStringExtra("title"));
		
		sbProgress = (ProgressBar)findViewById(R.id.progressBarProgress);
		tvProgress = (TextView)findViewById(R.id.textViewProgress);
		lvSeasons = (ListView)findViewById(R.id.listViewSeasons);
		ivBackground = (ImageView)findViewById(R.id.imageViewBackground);

		sbProgress.setEnabled(false);
		sbProgress.setProgressDrawable(getResources().getDrawable(R.drawable.gradient_progress));

		llNextEpisode = (LinearLayout)findViewById(R.id.linearLayoutNextEpisode);

		tvdbId = getIntent().getStringExtra("tvdb_id");
		int percentage = getIntent().getIntExtra("percentage", 0);
		
		lvSeasons.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				Intent i = new Intent(MyShowActivity.this, SeasonActivity.class);
				i.putExtra("tvdb_id", tvdbId);
				i.putExtra("title", getIntent().getStringExtra("title"));
				i.putExtra("position", lvSeasons.getCount()-position-1);
				startActivity(i);
			}

		});

		new DBSeasonsTask(this, new DBAdapter() 
		{
			@Override
			public void onDBSeasons(List<TvShowSeason> seasons) 
			{
				Utils.removeLoading();
				adapter = new ListSeasonAdapter(seasons, MyShowActivity.this);
				lvSeasons.setAdapter(adapter);
			}
		}, tvdbId, false, false).execute();
		
		displayClearLogo();

		displayPercentage(percentage);
		
		displayNextEpisode();
	}
	
	private void displayPercentage(int percentage)
	{		
		new ProgressBarRunnable(percentage).run();
	}

	private void displayClearLogo()
	{
		new Thread()
		{
			public void run()
			{
				final String url = Fanart.getFanartParser().getFanart(tvdbId, Fanart.CLEARLOGO, MyShowActivity.this);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run()
					{
						AQuery aq = new AQuery(MyShowActivity.this);
						aq.id(ivBackground).image(url, true, false, 0, 0, null, android.R.anim.fade_in);
					}
				});
			}
		}.start();
	}
	
	private void displayNextEpisode()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(this);
		dbw.open();
		final TvShowEpisode e = dbw.getNextEpisode(tvdbId);
		dbw.close();

		if(e != null)
		{
			llNextEpisode.setVisibility(View.VISIBLE);

			TextView tvTitle = (TextView)llNextEpisode.findViewById(R.id.textViewTitle);
			TextView tvEpisode = (TextView)llNextEpisode.findViewById(R.id.textViewEpisode);
			ImageView ivScreen = (ImageView)llNextEpisode.findViewById(R.id.imageViewScreen);

			tvTitle.setText(e.getTitle());
			tvEpisode.setText(Utils.addZero(e.getSeason()) + "x" + Utils.addZero(e.getNumber()));
			
			Image i = new Image(tvdbId, e.getImages().getScreen(), e.getSeason(), e.getNumber(), true);
			AQuery aq = new AQuery(MyShowActivity.this);
			aq.id(ivScreen).image(i.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in, 9.0f / 16.0f);

			llNextEpisode.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent i = new Intent(MyShowActivity.this, EpisodeActivity.class);
					ArrayList<TvShowEpisode> episodes = new ArrayList<TvShowEpisode>();
					episodes.add(e);
					i.putExtra("results", episodes);
					MyShowActivity.this.startActivity(i);
				}
			});
		}
		else
			llNextEpisode.setVisibility(View.GONE);
	}
	
	@Override
	public void onShowUpdated(TvShow show) 
	{
		if(show.getTvdbId().equals(tvdbId) && adapter != null)
		{
			displayPercentage(show.getProgress());
			displayNextEpisode();
			Collections.reverse(show.getSeasons());
			adapter.reloadData(show.getSeasons());
		}
	}

	@Override
	public void onShowRemoved(TvShow show)
	{
		if(show.getTvdbId().equals(tvdbId))
			finish();
	}
	
	private class ProgressBarRunnable implements Runnable
	{
		private int percentage;
		private int currentPercentage = 0;
		
		private Handler h = new Handler();
		
		public ProgressBarRunnable(int percentage)
		{
			this.percentage = percentage;
		}

		@Override
		public void run() 
		{
			sbProgress.setProgress(currentPercentage);
			tvProgress.setText(currentPercentage+"%");
			if(currentPercentage <= percentage - PERCENTAGE_STEP)
				currentPercentage += PERCENTAGE_STEP;
			else
				currentPercentage += percentage - currentPercentage;
			if(currentPercentage <= percentage)
				h.post(this);
		}
		
	}
}
