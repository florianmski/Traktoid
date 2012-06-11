package com.florianmski.tracktoid.ui.fragments.season;

import java.util.List;
import java.util.Map;

import android.os.Bundle;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.pagers.PagerSeasonAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.post.InCollectionTask;
import com.florianmski.tracktoid.trakt.tasks.post.InWatchlistTask;
import com.florianmski.tracktoid.trakt.tasks.post.SeenTask;
import com.florianmski.tracktoid.ui.fragments.PagerFragment;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public class PagerSeasonFragment extends PagerFragment
{
	private boolean watchedMode = false;
	private String tvdbId;
	private List<TvShowSeason> seasons;
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
					SeenTask.createTask(PagerSeasonFragment.this, lcm.getItemsList(), item.getItemId() == R.id.action_bar_watched_seen, null).fire();
					break;
				case R.id.action_bar_add_to_watchlist:
				case R.id.action_bar_remove_from_watchlist:
					InWatchlistTask.createTask(PagerSeasonFragment.this, lcm.getItemsList(), item.getItemId() == R.id.action_bar_add_to_watchlist, null).fire();
					break;
				case R.id.action_bar_add_to_collection:
				case R.id.action_bar_remove_from_collection:
					InCollectionTask.createTask(PagerSeasonFragment.this, lcm.getItemsList(), item.getItemId() == R.id.action_bar_add_to_collection, null).fire();
					break;
				}
				return true;
			}
		});
		
		if(lcm.isActivated())
			getSherlockActivity().startActionMode(lcm.getCallback());

//		new DBSeasonsTask(this, new DBAdapter() 
//		{
//			@Override
//			public void onDBSeasons(List<TvShowSeason> seasons) 
//			{
//				SeasonActivity.this.seasons = seasons;
//				Utils.removeLoading();
//				initPagerActivity(new PagedListEpisodesAdapter(seasons, tvdbId, SeasonActivity.this));
//			}
//		}, tvdbId, true, true).fire();

		setData();
	}
	
	//don't know why but using this thread is like 3 time faster than using an asynctask doing the same thing (???)
	public void setData()
	{
//		new Thread()
//		{
//			@Override
//			public void run()
//			{
//				DatabaseWrapper dbw = getDBWrapper();
//				String tvdb_id = getArguments().getString(TraktoidConstants.BUNDLE_TVDB_ID);
//				List<TvShowSeason> seasons = dbw.getSeasons(tvdb_id, true, true);
//
//				adapter = new PagerSeasonAdapter(seasons, tvdb_id, getFragmentManager(), getActivity().getApplicationContext());
//
//				getActivity().runOnUiThread(new Runnable() 
//				{
//					@Override
//					public void run() 
//					{
//						if(((PagerSeasonAdapter)adapter).isEmpty())
//							getStatusView().hide().text("No seasons, this is strange...");
//						else
//							getStatusView().hide().text(null);
//						
//						initPagerFragment(adapter);
//					}
//				});
//			}
//		}.start();
		//TODO proper task
		new Thread()
		{
			@Override
			public void run()
			{
				DatabaseWrapper dbw = getDBWrapper();
				seasons = dbw.getSeasons(tvdbId, false, true);

				adapter = new PagerSeasonAdapter(seasons, getFragmentManager());

				getActivity().runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						if(((PagerSeasonAdapter)adapter).isEmpty())
							getStatusView().hide().text("No seasons, this is strange...");
						else
							getStatusView().hide().text(null);
						
						initPagerFragment(adapter);
					}
				});
			}
		}.start();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		if(watchedMode)
		{
			menu.add(0, R.id.action_bar_send, 0, "Send")
				.setIcon(R.drawable.ab_icon_send)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		menu.add(0, R.id.action_bar_watched, 0, "Watched")
			.setIcon(R.drawable.ab_icon_eye)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
//		case R.id.action_bar_watched :
//			//if adapter is not currently loading
//			if(adapter != null)
//			{
//				watchedMode = !watchedMode;
//				getSherlockActivity().invalidateOptionsMenu();
//				WatchedModeManager.getInstance().setWatchedMode(watchedMode);
//			}
//			return true;
//		case R.id.action_bar_send :
//		{
//			List<Map<Integer, Boolean>> listWatched = WatchedModeManager.getInstance().getWatchedList();
//			int[] seasons = ((PagerSeasonAdapter) adapter).getSeasons();
//
//			boolean isEmpty = true;
//			for(int i = 0; i < listWatched.size(); i++)
//				isEmpty &= listWatched.get(i).isEmpty();
//
//			if(isEmpty)
//				Toast.makeText(getActivity(), "Nothing to send...", Toast.LENGTH_SHORT).show();
//			else
//				Utils.chooseBetweenSeenAndCheckin((new WatchedEpisodesTask(this, tvdbId, seasons, listWatched)), getActivity());
//
//			watchedMode = !watchedMode;
//			getSherlockActivity().invalidateOptionsMenu();
//			WatchedModeManager.getInstance().setWatchedMode(watchedMode);
//		}
//		return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//TODO
//	@Override
//	public void onShowUpdated(TvShow show)
//	{
//		if(show.tvdbId.equals(tvdbId) && adapter != null && show.seasons != null)
//			((PagerSeasonAdapter) adapter).reloadData(show.seasons);
//	}
//
//	@Override
//	public void onShowRemoved(TvShow show)
//	{
//		if(show.tvdbId.equals(tvdbId))
//			getActivity().finish();
//	}
	
	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);
		
		lcm.setPageSelected(position);
		//TODO
//		setBackground(TraktImage.getnew Image(tvdbId, seasons.get(position).season));
	}
	
	public interface OnWatchedModeListener
	{
		public void setWatchedMode(boolean on);
		public Map<Integer, Boolean> getWatchedList();
		public void checkAll(String url);
		public void checkNone(String url);
	}
}
