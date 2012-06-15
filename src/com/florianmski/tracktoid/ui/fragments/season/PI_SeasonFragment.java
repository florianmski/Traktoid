package com.florianmski.tracktoid.ui.fragments.season;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktListener;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.adapters.lists.ListEpisodeAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.ui.activities.phone.TraktItemsActivity;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.florianmski.tracktoid.widgets.CheckableListView;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public class PI_SeasonFragment extends TraktFragment implements TraktListener<TvShowEpisode>
{
	private TvShowSeason season;
	private int position;
	private CheckableListView<TvShowEpisode> lvEpisodes;
	private ListEpisodeAdapter adapter;

	public static PI_SeasonFragment newInstance(Bundle args)
	{
		PI_SeasonFragment f = new PI_SeasonFragment();
		f.setArguments(args);
		return f;
	}

	public static PI_SeasonFragment newInstance(TvShowSeason season, int position)
	{		
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_SEASON, season);
		args.putSerializable(TraktoidConstants.BUNDLE_POSITION, position);

		return newInstance(args);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if(getArguments() != null)
		{
			season = (TvShowSeason) getArguments().getSerializable(TraktoidConstants.BUNDLE_SEASON);
			position = getArguments().getInt(TraktoidConstants.BUNDLE_POSITION);
		}
		
		TraktTask.addObserver(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		getStatusView().show().text("Loading season " + season.season + ",\nPlease wait...");
		
		ListCheckerManager.getInstance().addListener(lvEpisodes);
		lvEpisodes.initialize(this, position, ListCheckerManager.<TvShowEpisode>getInstance());

		//TODO proper task
		new Thread()
		{
			@Override
			public void run()
			{
				DatabaseWrapper dbw = getDBWrapper();
				List<TvShowEpisode> episodes = dbw.getEpisodes(season.url);

				adapter = new ListEpisodeAdapter(episodes, season.url, getActivity());

				getActivity().runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						lvEpisodes.setAdapter(adapter);

						if(adapter.isEmpty())
							getStatusView().hide().text("No episodes for this season");
						else
							getStatusView().hide().text(null);
					}
				});
			}
		}.start();

//		lvEpisodes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//		lvEpisodes.setItemsCanFocus(false);
//		lvEpisodes.setOnItemLongClickListener(new OnItemLongClickListener() 
//		{
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
//			{
//				mMode = getSherlockActivity().startActionMode(mActionModeListener = new AnActionModeOfEpicProportions());
//				return true;
//			}
//		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_season, container, false);

		lvEpisodes = (CheckableListView<TvShowEpisode>)v.findViewById(R.id.listViewEpisodes);
		ImageView ivBackground = (ImageView)v.findViewById(R.id.imageViewBackground);

		lvEpisodes.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
			{
//				if(mMode != null)
//				{
//					boolean checked = !lvEpisodes.getCheckedItemPositions().get(position, false);
//					Log.d("test", "size : " + lvEpisodes.getCheckedItemPositions().size());
//					Log.d("test", "test : " + checked);
//					lvEpisodes.getCheckedItemPositions().put(position, checked);
////					mActionModeListener.onItemCheckedStateChanged(mMode, position, id, !checked);
//					v.setBackgroundResource(checked ? R.color.list_pressed_color : R.color.list_background_color);
//				}
//				else
//				{
					Intent i = new Intent(getActivity(), TraktItemsActivity.class);
					//				i.putExtra(TraktoidConstants.BUNDLE_SEASON_ID, season.url);
					//				i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, tvdbId);
					//				i.putExtra(TraktoidConstants.BUNDLE_TITLE, getArguments().getString(TraktoidConstants.BUNDLE_TITLE));
					i.putExtra(TraktoidConstants.BUNDLE_POSITION, position);
					i.putExtra(TraktoidConstants.BUNDLE_RESULTS, (Serializable)((RootAdapter<TvShowEpisode>) lvEpisodes.getAdapter()).getItems());
					startActivity(i);
//				}
			}
		});

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
		if(item.getItemId() == R.id.action_bar_multiple_selection)
			lvEpisodes.start();
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() 
	{
		ListCheckerManager.getInstance().removeListener(lvEpisodes);
		TraktTask.removeObserver(this);
		super.onDestroy();
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

	@Override
	public void onTraktItemsUpdated(List<TvShowEpisode> traktItems) 
	{
		if(traktItems != null)
			adapter.updateItems(traktItems);
	}

	@Override
	public void onTraktItemsRemoved(List<TvShowEpisode> traktItems) 
	{
		if(traktItems != null)
			adapter.remove(traktItems);
	}
}