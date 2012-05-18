package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.lists.ListSeasonAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBSeasonsTask;
import com.florianmski.tracktoid.image.Fanart;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.trakt.tasks.post.RateTask;
import com.florianmski.tracktoid.trakt.tasks.post.WatchedEpisodesTask;
import com.florianmski.tracktoid.ui.activities.phone.EpisodeActivity;
import com.florianmski.tracktoid.ui.activities.phone.SeasonActivity;
import com.florianmski.tracktoid.ui.activities.phone.ShowActivity;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.enumerations.Rating;

public class ProgressionFragment extends TraktFragment
{
	private final static int PERCENTAGE_STEP = 2;

	private ProgressBar sbProgress;
	private TextView tvProgress;
	private ListView lvSeasons;
	private ImageView ivBackground;

	private BadgesView bvNextEpisode;

	private ListSeasonAdapter adapter;

	private TvShow show = null;

	public static ProgressionFragment newInstance(Bundle args)
	{
		ProgressionFragment f = new ProgressionFragment();
		f.setArguments(args);
		return f;
	}
	
	public ProgressionFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		getStatusView().show().text("Loading seasons,\nPlease wait...");
		
		adapter = new ListSeasonAdapter(new ArrayList<TvShowSeason>(), getActivity());
		lvSeasons.setAdapter(adapter);

		lvSeasons.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				Intent i = new Intent(getActivity(), SeasonActivity.class);
				i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, show.tvdbId);
				i.putExtra(TraktoidConstants.BUNDLE_TITLE, show.title);
				i.putExtra(TraktoidConstants.BUNDLE_POSITION, lvSeasons.getCount()-position-1);
				startActivity(i);
			}

		});

		refreshFragment(getArguments());
	}

	public void refreshFragment(Bundle bundle)
	{
		if(bundle != null)
		{
			TvShow show = (TvShow)bundle.get(TraktoidConstants.BUNDLE_SHOW);

			if(this.show == null || !this.show.tvdbId.equals(show.tvdbId))
			{
				this.show = show;
				ivBackground.setImageBitmap(null);

				//in order to set the right heart color
				getSherlockActivity().invalidateOptionsMenu();

				setTitle(show.title);

				new DBSeasonsTask(getActivity(), new DBAdapter() 
				{
					@Override
					public void onDBSeasons(List<TvShowSeason> seasons) 
					{
						Collections.reverse(seasons);
						adapter.updateItems(seasons);
						
						if(adapter.isEmpty())
							getStatusView().hide().text("This show has no seasons, wait... WTF ?");
						else
							getStatusView().hide().text(null);
					}
				}, show.tvdbId, false, false).fire();

				displayClearLogo();

				displayPercentage(show.progress);

				displayNextEpisode();
			}
		}
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
				final String url = Fanart.getFanartParser().getFanart(show.tvdbId, Fanart.CLEARLOGO, getActivity());
				if(getActivity() != null)
					getActivity().runOnUiThread(new Runnable() 
					{
						@Override
						public void run()
						{
							AQuery aq = new AQuery(getActivity());
							aq.id(ivBackground).image(url, true, false, 0, 0, null, android.R.anim.fade_in);
						}
					});
			}
		}.start();
	}

	private void displayNextEpisode()
	{
		DatabaseWrapper dbw = getDBWrapper();
		final TvShowEpisode e = dbw.getNextEpisode(show.tvdbId);

		if(e != null)
		{
			bvNextEpisode.setVisibility(View.VISIBLE);

			TextView tvTitle = (TextView)bvNextEpisode.findViewById(R.id.textViewTitle);
			TextView tvEpisode = (TextView)bvNextEpisode.findViewById(R.id.textViewEpisode);
			ImageView ivScreen = (ImageView)bvNextEpisode.findViewById(R.id.imageViewScreen);

			bvNextEpisode.initialize();
			bvNextEpisode.setTraktItem(e);
			tvTitle.setText(e.title);
			tvEpisode.setText(Utils.addZero(e.season) + "x" + Utils.addZero(e.number));

			TraktImage i = TraktImage.getScreen(e);
			final AQuery aq = new AQuery(getActivity());
			BitmapAjaxCallback cb = new BitmapAjaxCallback()
	        {
	        	@Override
	            public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
	        	{     
	        		aq.id(iv).image(Utils.borderBitmap(bm, getActivity())).animate(android.R.anim.fade_in);
	            }

	        }.url(i.getUrl()).fileCache(false).memCache(true).ratio(9.0f / 16.0f);
	        aq.id(ivScreen).image(cb);

			bvNextEpisode.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent i = new Intent(getActivity(), EpisodeActivity.class);
					for(TvShowSeason s : adapter.getItems())
					{
						if(s.season == e.season)
							i.putExtra(TraktoidConstants.BUNDLE_SEASON_ID, s.url);
					}
					
					i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, show.tvdbId);
					i.putExtra(TraktoidConstants.BUNDLE_POSITION, e.number-1);
					startActivity(i);
				}
			});
		}
		else
			bvNextEpisode.setVisibility(View.GONE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		if (container == null) 
			getFragmentManager().beginTransaction().hide(this).commit();

		View v = inflater.inflate(R.layout.fragment_my_show, null);

		sbProgress = (ProgressBar)v.findViewById(R.id.progressBarProgress);
		tvProgress = (TextView)v.findViewById(R.id.textViewProgress);
		lvSeasons = (ListView)v.findViewById(R.id.listViewSeasons);
		ivBackground = (ImageView)v.findViewById(R.id.imageViewBackground);

		sbProgress.setEnabled(false);
		sbProgress.setProgressDrawable(getResources().getDrawable(R.drawable.gradient_progress));

		bvNextEpisode = (BadgesView)v.findViewById(R.id.badgesLayoutNextEpisode);

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(0, R.id.action_bar_watched, 0, "Watched")
		.setIcon(R.drawable.ab_icon_eye)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		Drawable d = getResources().getDrawable(R.drawable.ab_icon_rate).mutate();
		
		SubMenu rateMenu = menu.addSubMenu("Rate");
		d.setColorFilter(Color.parseColor("#691909"), PorterDuff.Mode.MULTIPLY);
		rateMenu.add(0, R.id.action_bar_rate_love, 0, "Totally ninja!")
		.setIcon(d)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		d = getResources().getDrawable(R.drawable.ab_icon_rate).mutate();
		d.setColorFilter(Color.parseColor("#333333"), PorterDuff.Mode.MULTIPLY);
		rateMenu.add(0, R.id.action_bar_rate_hate, 0, "Week sauce :(")
		.setIcon(d)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		d = getResources().getDrawable(R.drawable.ab_icon_rate).mutate();
		rateMenu.add(0, R.id.action_bar_rate_unrate, 0, "Unrate")
		.setIcon(d)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		if(show != null && show.rating != null)
		{
			d = getResources().getDrawable(R.drawable.ab_icon_rate).mutate();
			switch(show.rating)
			{
			//TODO go to res/color
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
		
        MenuItem rateItem = rateMenu.getItem();
        rateItem.setIcon(d);
        rateItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add(0, R.id.action_bar_about, 0, "Info")
		.setIcon(R.drawable.ab_icon_info)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
				final List<TvShowSeason> seasons = adapter.getItems();
				final List<TvShowSeason> seasonsChecked = new ArrayList<TvShowSeason>();
				CharSequence[] items = new CharSequence[seasons.size()];

				for(int i = 0; i < items.length; i++)
					items[i] = seasons.get(i).season == 0 ? "Specials" : "Season " + seasons.get(i).season;

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
						if(!seasonsChecked.isEmpty())
							tm.addToQueue(new WatchedEpisodesTask(tm, ProgressionFragment.this, show.tvdbId, seasonsChecked, true));
						else
							Toast.makeText(getActivity(), "Nothing to send...", Toast.LENGTH_SHORT).show();
					}
				});

				builder.setNeutralButton("Unwatched", new android.content.DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						if(!seasonsChecked.isEmpty())
							tm.addToQueue(new WatchedEpisodesTask(tm, ProgressionFragment.this, show.tvdbId, seasonsChecked, false));
						else
							Toast.makeText(getActivity(), "Nothing to send...", Toast.LENGTH_SHORT).show();
					}
				});

				builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});

				AlertDialog alert = builder.create();

				//avoid trying to show dialog if activity no longer exist
				if(!getActivity().isFinishing())
					alert.show();
			}
			return true;
		case R.id.action_bar_rate_love :
			RateTask.createTask(tm, this, show, Rating.Love, null);
			return true;
		case R.id.action_bar_rate_hate :
			RateTask.createTask(tm, this, show, Rating.Hate, null);
			return true;
		case R.id.action_bar_rate_unrate :
			RateTask.createTask(tm, this, show, Rating.Unrate, null);
			return true;
		case R.id.action_bar_about :
			Intent i = new Intent(getActivity(), ShowActivity.class);
			ArrayList<TvShow> shows = new ArrayList<TvShow>();
			shows.add(this.show);
			i.putExtra(TraktoidConstants.BUNDLE_RESULTS, shows);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onShowUpdated(TvShow show) 
	{
		if(show.tvdbId.equals(this.show.tvdbId) && adapter != null)
		{
			displayPercentage(show.progress);
			displayNextEpisode();

			if(show.seasons != null)
				adapter.updateItems(show.seasons);

			this.show = show;
			getSherlockActivity().invalidateOptionsMenu();
		}
	}

	@Override
	public void onShowRemoved(TvShow show)
	{
		if(show.tvdbId.equals(show.tvdbId))
			getActivity().finish();
	}

	private class ProgressBarRunnable implements Runnable
	{
		private int percentage;
		private int currentPercentage = 0;
		private boolean stop = false;

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
			if(currentPercentage < percentage)
				sbProgress.post(this);
			else if(currentPercentage == percentage && !stop)
			{
				sbProgress.post(this);
				stop = true;
			}
			else
				sbProgress.removeCallbacks(this);
		}

	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
