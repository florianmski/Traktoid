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

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
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
import com.florianmski.tracktoid.trakt.tasks.RateTask;
import com.florianmski.tracktoid.trakt.tasks.WatchedEpisodesTask;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.enumerations.Rating;

public class MyShowActivity extends TraktActivity
{
	private final static int PERCENTAGE_STEP = 2;

	private ProgressBar sbProgress;
	private TextView tvProgress;
	private ListView lvSeasons;
	private ImageView ivBackground;

	private LinearLayout llNextEpisode;

	private ListSeasonAdapter adapter;

	private TvShow show = null;
	
	private QuickAction qa;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_show);

		show = (TvShow)getIntent().getExtras().get("show");
		//in order to set the right heart color
		invalidateOptionsMenu();

		Utils.showLoading(this);
		setTitle(show.getTitle());

		sbProgress = (ProgressBar)findViewById(R.id.progressBarProgress);
		tvProgress = (TextView)findViewById(R.id.textViewProgress);
		lvSeasons = (ListView)findViewById(R.id.listViewSeasons);
		ivBackground = (ImageView)findViewById(R.id.imageViewBackground);

		sbProgress.setEnabled(false);
		sbProgress.setProgressDrawable(getResources().getDrawable(R.drawable.gradient_progress));

		llNextEpisode = (LinearLayout)findViewById(R.id.linearLayoutNextEpisode);

		lvSeasons.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				Intent i = new Intent(MyShowActivity.this, SeasonActivity.class);
				i.putExtra("tvdb_id", show.getTvdbId());
				i.putExtra("title", show.getTitle());
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
		}, show.getTvdbId(), false, false).execute();

		displayClearLogo();

		displayPercentage(show.getProgress());

		displayNextEpisode();
		
		qa = new QuickAction(this);
		Drawable d = getResources().getDrawable(R.drawable.ab_icon_rate).mutate();
		d.setColorFilter(Color.parseColor("#691909"), PorterDuff.Mode.MULTIPLY);
		qa.addActionItem(new ActionItem(Rating.Love.ordinal(), "Totally ninja!", d));
		d = getResources().getDrawable(R.drawable.ab_icon_rate).mutate();
		d.setColorFilter(Color.parseColor("#333333"), PorterDuff.Mode.MULTIPLY);
		qa.addActionItem(new ActionItem(Rating.Hate.ordinal(), "Week sauce :(", d));
		d = getResources().getDrawable(R.drawable.ab_icon_rate).mutate();
		qa.addActionItem(new ActionItem(Rating.UNRATE.ordinal(), "Unrate", d));
		
		qa.setOnActionItemClickListener(new OnActionItemClickListener() 
		{
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) 
			{
				tm.addToQueue(new RateTask(tm, MyShowActivity.this, show, Rating.values()[actionId]));
			}
		});
	}

	private void displayPercentage(int percentage)
	{		
		new ProgressBarRunnable(percentage).run();
	}

	private void displayClearLogo()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				final String url = Fanart.getFanartParser().getFanart(show.getTvdbId(), Fanart.CLEARLOGO, MyShowActivity.this);
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
		final TvShowEpisode e = dbw.getNextEpisode(show.getTvdbId());
		dbw.close();

		if(e != null)
		{
			llNextEpisode.setVisibility(View.VISIBLE);

			TextView tvTitle = (TextView)llNextEpisode.findViewById(R.id.textViewTitle);
			TextView tvEpisode = (TextView)llNextEpisode.findViewById(R.id.textViewEpisode);
			ImageView ivScreen = (ImageView)llNextEpisode.findViewById(R.id.imageViewScreen);

			tvTitle.setText(e.getTitle());
			tvEpisode.setText(Utils.addZero(e.getSeason()) + "x" + Utils.addZero(e.getNumber()));

			Image i = new Image(show.getTvdbId(), e.getImages().getScreen(), e.getSeason(), e.getNumber());
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
	public boolean onCreateOptionsMenu (Menu menu)
	{
		menu.add(0, R.id.action_bar_watched, 0, "Watched")
		.setIcon(R.drawable.ab_icon_eye)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		Drawable d = getResources().getDrawable(R.drawable.ab_icon_rate).mutate();

		if(show != null && show.getRating() != null)
		{
			switch(show.getRating())
			{
			case Love :
				d.setColorFilter(Color.parseColor("#691909"), PorterDuff.Mode.MULTIPLY);
				break;
			case Hate :
				d.setColorFilter(Color.parseColor("#333333"), PorterDuff.Mode.MULTIPLY);
				break;
			default :
				break;
			}
		}

		menu.add(0, R.id.action_bar_rate, 0, "Rate")
		.setIcon(d)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.action_bar_watched :
			//if adapter is not currently loading
			if(adapter != null)
			{
				final List<TvShowSeason> seasons = adapter.getSeasons();
				final List<TvShowSeason> seasonsChecked = new ArrayList<TvShowSeason>();
				CharSequence[] items = new CharSequence[seasons.size()];

				for(int i = 0; i < items.length; i++)
					items[i] = seasons.get(i).getSeason() == 0 ? "Sepcials" : "Season " + seasons.get(i).getSeason();

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMultiChoiceItems(items, null, new OnMultiChoiceClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) 
					{
						if(isChecked)
							seasonsChecked.add(seasons.get(which));
						else
							seasonsChecked.remove(seasons.get(which));
					}
				});

				builder.setPositiveButton("Watched", new android.content.DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						tm.addToQueue(new WatchedEpisodesTask(tm, MyShowActivity.this, show.getTvdbId(), seasonsChecked, true));
					}
				});

				builder.setNeutralButton("Unwatched", new android.content.DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						tm.addToQueue(new WatchedEpisodesTask(tm, MyShowActivity.this, show.getTvdbId(), seasonsChecked, false));
					}
				});

				builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});

				AlertDialog alert = builder.create();

				//avoid trying to show dialog if activity no longer exist
				if(!isFinishing())
					alert.show();
			}
			return true;
		case R.id.action_bar_rate :
			qa.show(findViewById(R.id.action_bar_rate));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onShowUpdated(TvShow show) 
	{
		if(show.getTvdbId().equals(show.getTvdbId()) && adapter != null)
		{
			displayPercentage(show.getProgress());
			displayNextEpisode();
			
			if(show.getSeasons() != null)
				adapter.reloadData(show.getSeasons());
			
			this.show = show;
			invalidateOptionsMenu();
		}
	}

	@Override
	public void onShowRemoved(TvShow show)
	{
		if(show.getTvdbId().equals(show.getTvdbId()))
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
