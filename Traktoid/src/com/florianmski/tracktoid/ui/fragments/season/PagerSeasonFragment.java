package com.florianmski.tracktoid.ui.fragments.season;

import java.util.List;
import java.util.Map;

import android.os.Bundle;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.pagers.PagerSeasonAdapter;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBSeasonsTask;
import com.florianmski.tracktoid.events.TraktItemsRemovedEvent;
import com.florianmski.tracktoid.events.TraktItemsUpdatedEvent;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.trakt.tasks.post.InCollectionTask;
import com.florianmski.tracktoid.trakt.tasks.post.InWatchlistTask;
import com.florianmski.tracktoid.trakt.tasks.post.SeenTask;
import com.florianmski.tracktoid.ui.fragments.PagerFragment;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.squareup.otto.Subscribe;

public class PagerSeasonFragment extends PagerFragment
{
	private String tvdbId;
	private ListCheckerManager<TvShowEpisode> lcm;

	public static PagerSeasonFragment newInstance(Bundle args)
	{
		PagerSeasonFragment f = new PagerSeasonFragment();
		f.setArguments(args);
		return f;
	}

	public PagerSeasonFragment() {}
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setPageIndicatorType(IT_UNDERLINE);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		getStatusView().show().text("Loading seasons,\nPlease wait...");
		setTitle(getArguments().getString(TraktoidConstants.BUNDLE_TITLE));

		tvdbId = getArguments().getString(TraktoidConstants.BUNDLE_TVDB_ID);

		lcm = ListCheckerManager.getInstance();
		lcm.setPageSelected(currentPagerPosition);
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

				SubMenu watchlistMenu = menu.addSubMenu("watchlist");
				watchlistMenu.add(0, R.id.action_bar_add_to_watchlist, 0, "add to watchlist")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				watchlistMenu.add(0, R.id.action_bar_remove_from_watchlist, 0, "remove from watchlist")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				MenuItem watchlistItem = watchlistMenu.getItem();
				watchlistItem.setIcon(R.drawable.badge_watchlist);
				watchlistItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);

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
					SeenTask.createTask(getActivity(), lcm.getItemsList(), item.getItemId() == R.id.action_bar_watched_seen, null).fire();
					break;
				case R.id.action_bar_add_to_watchlist:
				case R.id.action_bar_remove_from_watchlist:
					InWatchlistTask.createTask(getActivity(), lcm.getItemsList(), item.getItemId() == R.id.action_bar_add_to_watchlist, null).fire();
					break;
				case R.id.action_bar_add_to_collection:
				case R.id.action_bar_remove_from_collection:
					InCollectionTask.createTask(getActivity(), lcm.getItemsList(), item.getItemId() == R.id.action_bar_add_to_collection, null).fire();
					break;
				}
				return true;
			}
		});

		if(lcm.isActivated())
			getSherlockActivity().startActionMode(lcm.getCallback());

		setData();
	}

	public void setData()
	{
		new DBSeasonsTask(getActivity(), tvdbId, false, true, new DBAdapter() 
		{
			@Override
			public void onDBSeasons(List<TvShowSeason> seasons) 
			{
				adapter = new PagerSeasonAdapter(seasons, getFragmentManager());

				if(((PagerSeasonAdapter)adapter).isEmpty())
					getStatusView().hide().text("No seasons, this is strange...");
				else
					getStatusView().hide().text(null);

				initPagerFragment(adapter);
			}
		}).execute();
	}
	
	@SuppressWarnings("rawtypes")
	@Subscribe
	public void onTraktItemsUpdated(TraktItemsUpdatedEvent event) 
	{
		//TODO
		//		if(show.tvdbId.equals(tvdbId) && adapter != null && show.seasons != null)
		//			((PagerSeasonAdapter) adapter).reloadData(show.seasons);
	}

	@SuppressWarnings("rawtypes")
	@Subscribe
	public void onTraktItemsRemoved(TraktItemsRemovedEvent event) 
	{
		//TODO
		//		if(show.tvdbId.equals(tvdbId))
		//		getActivity().finish();
	}

	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);

		lcm.setPageSelected(position);
		setSubtitle(adapter.getPageTitle(position).toString());
		setBackground(TraktImage.getSeasonPoster(((PagerSeasonAdapter)adapter).getSeason(position), tvdbId));
	}

	public interface OnWatchedModeListener
	{
		public void setWatchedMode(boolean on);
		public Map<Integer, Boolean> getWatchedList();
		public void checkAll(String url);
		public void checkNone(String url);
	}
}
