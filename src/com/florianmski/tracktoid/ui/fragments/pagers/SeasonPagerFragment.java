package com.florianmski.tracktoid.ui.fragments.pagers;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.lists.ListEpisodeAdapter;
import com.florianmski.tracktoid.adapters.pagers.PagerSeasonAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.trakt.tasks.post.WatchedEpisodesTask;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowSeason;

public class SeasonPagerFragment extends PagerFragment
{
	private boolean watchedMode = false;
	private String tvdbId;
	private List<TvShowSeason> seasons;
	
	public static SeasonPagerFragment newInstance(Bundle args)
	{
		SeasonPagerFragment f = new SeasonPagerFragment();
		f.setArguments(args);
		return f;
	}
	
	public SeasonPagerFragment() {}
	
	public SeasonPagerFragment(FragmentListener listener) 
	{
		super(listener);
	}
	
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

//		new DBSeasonsTask(this, new DBAdapter() 
//		{
//			@Override
//			public void onDBSeasons(List<TvShowSeason> seasons) 
//			{
//				SeasonActivity.this.seasons = seasons;
//				Utils.removeLoading();
//				initPagerActivity(new PagedListEpisodesAdapter(seasons, tvdbId, SeasonActivity.this));
//			}
//		}, tvdbId, true, true).execute();

		setData();
	}
	
	//don't know why but using this thread is like 3 time faster than using an asynctask doing the same thing (???)
	public void setData()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				DatabaseWrapper dbw = new DatabaseWrapper(getActivity());
				dbw.open();
				String tvdb_id = getArguments().getString(TraktoidConstants.BUNDLE_TVDB_ID);
				List<TvShowSeason> seasons = dbw.getSeasons(tvdb_id, true, true);
				SeasonPagerFragment.this.seasons = seasons;
				dbw.close();

				adapter = new PagerSeasonAdapter(seasons, tvdb_id, getSupportFragmentManager(), getActivity().getApplicationContext());

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

			menu.add(0, R.id.menu_all, 0, "All")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

			menu.add(0, R.id.menu_none, 0, "None")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
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
		case R.id.action_bar_watched :
			//if adapter is not currently loading
			if(adapter != null)
			{
				watchedMode = !watchedMode;
				getSupportActivity().invalidateOptionsMenu();
				((PagerSeasonAdapter) adapter).setWatchedMode(watchedMode);
			}
			return true;
		case R.id.action_bar_send :
		{
			List<Map<Integer, Boolean>> listWatched = ((PagerSeasonAdapter) adapter).getListWatched();
			int[] seasons = ((PagerSeasonAdapter) adapter).getSeasons();

			boolean isEmpty = true;
			for(int i = 0; i < listWatched.size(); i++)
				isEmpty &= listWatched.get(i).isEmpty();

			if(isEmpty)
				Toast.makeText(getActivity(), "Nothing to send...", Toast.LENGTH_SHORT).show();
			else
				Utils.chooseBetweenSeenAndCheckin((new WatchedEpisodesTask(tm, this, tvdbId, seasons, listWatched)), getActivity());

			watchedMode = !watchedMode;
			getSupportActivity().invalidateOptionsMenu();
			((PagerSeasonAdapter) adapter).setWatchedMode(watchedMode);
		}
		return true;
		case R.id.menu_all :
			checkBoxSelection(true);
			return true;
		case R.id.menu_none :
			checkBoxSelection(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onShowUpdated(TvShow show)
	{
		if(show.tvdbId.equals(tvdbId) && adapter != null && show.seasons != null)
			((PagerSeasonAdapter) adapter).reloadData(show.seasons);
	}

	@Override
	public void onShowRemoved(TvShow show)
	{
		if(show.tvdbId.equals(tvdbId))
			getActivity().finish();
	}

	public void checkBoxSelection(boolean checked)
	{
		ListEpisodeAdapter a = ((PagerSeasonAdapter) adapter).getAdapters().get(currentPagerPosition);
		a.checkBoxSelection(checked);
		a.notifyDataSetChanged();
	}
	
	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);
		
		setBackground(new Image(tvdbId, seasons.get(position).season));
	}
}
