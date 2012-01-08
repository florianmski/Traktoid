package com.florianmski.tracktoid.ui.fragments;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.lists.ListEpisodeAdapter;
import com.florianmski.tracktoid.adapters.pagers.PagerListEpisodesAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.trakt.tasks.post.WatchedEpisodesTask;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowSeason;

public class SeasonFragment extends PagerFragment
{
	private boolean watchedMode = false;
	private String tvdbId;
	private List<TvShowSeason> seasons;
	
	public SeasonFragment() {}
	
	public SeasonFragment(FragmentListener listener) 
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
		
		Utils.showLoading(getActivity());
		setTitle(getActivity().getIntent().getStringExtra("title"));
		
		tvdbId = getActivity().getIntent().getStringExtra("tvdb_id");

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
				String tvdb_id = getActivity().getIntent().getStringExtra("tvdb_id");
				List<TvShowSeason> seasons = dbw.getSeasons(tvdb_id, true, true);
				SeasonFragment.this.seasons = seasons;
				dbw.close();

				adapter = new PagerListEpisodesAdapter(seasons, tvdb_id, getActivity());

				getActivity().runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						Utils.removeLoading();
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
				((PagerListEpisodesAdapter) adapter).setWatchedMode(watchedMode);
			}
			return true;
		case R.id.action_bar_send :
		{
			List<Map<Integer, Boolean>> listWatched = ((PagerListEpisodesAdapter) adapter).getListWatched();
			int[] seasons = ((PagerListEpisodesAdapter) adapter).getSeasons();

			boolean isEmpty = true;
			for(int i = 0; i < listWatched.size(); i++)
				isEmpty &= listWatched.get(i).isEmpty();

			if(isEmpty)
				Toast.makeText(getActivity(), "Nothing to send...", Toast.LENGTH_SHORT).show();
			else
				Utils.chooseBetweenSeenAndCheckin((new WatchedEpisodesTask(tm, this, tvdbId, seasons, listWatched)), getActivity());

			watchedMode = !watchedMode;
			getSupportActivity().invalidateOptionsMenu();
			((PagerListEpisodesAdapter) adapter).setWatchedMode(watchedMode);
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
			((PagerListEpisodesAdapter) adapter).reloadData(show.seasons);
	}

	@Override
	public void onShowRemoved(TvShow show)
	{
		if(show.tvdbId.equals(tvdbId))
			getActivity().finish();
	}

	//TODO
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu)
//	{
//		return watchedMode ? super.onPrepareOptionsMenu(menu) : false;
//	}

	public void checkBoxSelection(boolean checked)
	{
		ListEpisodeAdapter a = ((PagerListEpisodesAdapter) adapter).getAdapters().get(currentPagerPosition);
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
