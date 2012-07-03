package com.florianmski.tracktoid.ui.fragments.show;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktListener;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.lists.ListSeasonAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBSeasonsTask;
import com.florianmski.tracktoid.image.Fanart;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.trakt.tasks.post.InCollectionTask.InCollectionEpisodeTask;
import com.florianmski.tracktoid.trakt.tasks.post.RateTask;
import com.florianmski.tracktoid.trakt.tasks.post.SeenTask.SeenEpisodeTask;
import com.florianmski.tracktoid.ui.activities.phone.SeasonActivity;
import com.florianmski.tracktoid.ui.activities.phone.TraktItemsActivity;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.florianmski.tracktoid.widgets.CheckableListView;
import com.florianmski.tracktoid.widgets.RateDialog;
import com.florianmski.tracktoid.widgets.RateDialog.OnColorChangedListener;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.enumerations.Rating;

public class ProgressFragment extends TraktFragment implements TraktListener<TvShow>
{
	private final static int PERCENTAGE_STEP = 2;

	private ProgressBar sbProgress;
	private TextView tvProgress;
	private CheckableListView<TvShowSeason> lvSeasons;
	private ImageView ivBackground;

	private BadgesView<TvShowEpisode> bvNextEpisode;

	private ListSeasonAdapter adapter;

	private TvShow show = null;
	
	private ListCheckerManager<TvShowSeason> lcm;

	public static ProgressFragment newInstance(Bundle args)
	{
		ProgressFragment f = new ProgressFragment();
		f.setArguments(args);
		return f;
	}

	public ProgressFragment() {}

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
		
		//TODO save state in case of configuration change
		
		lcm = new ListCheckerManager<TvShowSeason>();
		lcm.setOnActionModeListener(new Callback() 
		{
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
			{
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) 
			{
				
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) 
			{
				SubMenu seenMenu = menu.addSubMenu("watched");
				seenMenu.add(0, R.id.action_bar_watched_seen, 0, "Seen")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				seenMenu.add(0, R.id.action_bar_watched_unseen, 0, "Unseen")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				MenuItem seenItem = seenMenu.getItem();
		        seenItem.setIcon(R.drawable.ab_icon_eye);
		        seenItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
				
				SubMenu collectionMenu = menu.addSubMenu("collection");
				collectionMenu.add(0, R.id.action_bar_add_to_collection, 0, "add to collection")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				collectionMenu.add(0, R.id.action_bar_remove_from_collection, 0, "remove from collection")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				MenuItem collectionItem = collectionMenu.getItem();
				collectionItem.setIcon(R.drawable.badge_collection);
				collectionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
			{
				switch(item.getItemId())
				{
				case R.id.action_bar_watched_unseen:
				case R.id.action_bar_watched_seen:
					SeenEpisodeTask.createSeasonTask(getActivity(), lcm.getItemsList(), item.getItemId() == R.id.action_bar_watched_seen, null).fire();
					break;
				case R.id.action_bar_add_to_collection:
				case R.id.action_bar_remove_from_collection:
					InCollectionEpisodeTask.createSeasonTask(getActivity(), lcm.getItemsList(), item.getItemId() == R.id.action_bar_add_to_collection, null).fire();
					break;
				}
				return true;
			}
		});
		
		lcm.addListener(lvSeasons);
		lvSeasons.initialize(this, 0, lcm);
		
		if(lcm.isActivated())
			getSherlockActivity().startActionMode(lcm.getCallback());

		getStatusView().show().text("Loading seasons,\nPlease wait...");

		adapter = new ListSeasonAdapter(new ArrayList<TvShowSeason>(), getActivity(), lcm);
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
	
	@Override 
	public void onDestroy()
	{
		super.onDestroy();
		lcm.removeListener(lvSeasons);
	}

	public void refreshFragment(Bundle bundle)
	{
		if(bundle != null)
		{
			TvShow show = (TvShow)bundle.get(TraktoidConstants.BUNDLE_TRAKT_ITEM);

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
						adapter.refreshItems(seasons);

						if(adapter.isEmpty())
							getStatusView().hide().text("This show has no seasons, wait... WTF ?");
						else
							getStatusView().hide().text(null);
					}
				}, show.tvdbId, true, false).fire();

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
					Intent i = new Intent(getActivity(), TraktItemsActivity.class);
					//					for(TvShowSeason s : adapter.getItems())
					//					{
					//						if(s.season == e.season)
					//							i.putExtra(TraktoidConstants.BUNDLE_SEASON_ID, s.url);
					//					}
					//					
					//					i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, show.tvdbId);
					List<TvShowEpisode> nextEpisode = new ArrayList<TvShowEpisode>();
					nextEpisode.add(e);
					i.putExtra(TraktoidConstants.BUNDLE_RESULTS, (Serializable) nextEpisode);
					i.putExtra(TraktoidConstants.BUNDLE_POSITION, e.number-1);
					startActivity(i);
				}
			});
		}
		else
			bvNextEpisode.setVisibility(View.GONE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		if (container == null) 
			getFragmentManager().beginTransaction().hide(this).commit();

		View v = inflater.inflate(R.layout.fragment_my_show, null);

		sbProgress = (ProgressBar)v.findViewById(R.id.progressBarProgress);
		tvProgress = (TextView)v.findViewById(R.id.textViewProgress);
		lvSeasons = (CheckableListView<TvShowSeason>)v.findViewById(R.id.listViewSeasons);
		ivBackground = (ImageView)v.findViewById(R.id.imageViewBackground);

		sbProgress.setEnabled(false);
		sbProgress.setProgressDrawable(getResources().getDrawable(R.drawable.gradient_progress));

		bvNextEpisode = (BadgesView<TvShowEpisode>)v.findViewById(R.id.badgesLayoutNextEpisode);

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		
		menu.add(0, R.id.action_bar_multiple_selection, 0, "Multiple selection")
		.setIcon(R.drawable.ab_icon_mark)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.action_bar_multiple_selection:
			lvSeasons.start();
			return true;
		}
		return super.onOptionsItemSelected(item);
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

	@Override
	public void onTraktItemsUpdated(List<TvShow> traktItems) 
	{
		for(TvShow traktItem : traktItems)
			if(traktItem.tvdbId.equals(this.show.tvdbId) && adapter != null)
			{
				displayPercentage(traktItem.progress);
				displayNextEpisode();

				if(traktItem.seasons != null)
					adapter.refreshItems(traktItem.seasons);

				this.show = traktItem;
				getSherlockActivity().invalidateOptionsMenu();
				
				break;
			}
	}

	@Override
	public void onTraktItemsRemoved(List<TvShow> traktItems) 
	{
		for(TvShow traktItem : traktItems)
			if(traktItem.tvdbId.equals(show.tvdbId))
			{
				getActivity().finish();
				break;
			}
	}
}
