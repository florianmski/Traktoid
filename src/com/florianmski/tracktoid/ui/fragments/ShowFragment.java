package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.PagerShowAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.UpdateShowsTask;
import com.florianmski.tracktoid.ui.activities.phone.ShoutsActivity;
import com.jakewharton.trakt.entities.TvShow;

public class ShowFragment extends PagerFragment
{
	//TODO onShowUpdated()
	private TvShow show;
	private boolean isExist;
	
	public ShowFragment() {}
	
	public ShowFragment(FragmentListener listener) 
	{
		super(listener);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setPageIndicatorType(PagerFragment.IT_CIRCLE);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		Utils.showLoading(getActivity());
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
				final List<TvShow> shows = (List<TvShow>)getActivity().getIntent().getSerializableExtra("results");

				getActivity().runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						Utils.removeLoading();
						initPagerFragment(new PagerShowAdapter(shows, getActivity()));
					}
				});
			}
		}.start();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		if(!isExist)
		{
			menu.add(0, R.id.action_bar_add, 0, "Add")
			.setIcon(R.drawable.ab_icon_add)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		menu.add(0, R.id.action_bar_shouts, 0, "Info")
		.setIcon(R.drawable.ab_icon_shouts)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
		case R.id.action_bar_shouts :
			Intent i = new Intent(getActivity(), ShoutsActivity.class);
			i.putExtra("tvdbId", show.tvdbId);
			startActivity(i);
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
		setTitle(show.title);
		isExist = show.progress > 0;
		getSupportActivity().invalidateOptionsMenu();
	}
}
