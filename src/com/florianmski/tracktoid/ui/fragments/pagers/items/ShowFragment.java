package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.trakt.tasks.get.UpdateShowsTask;
import com.florianmski.tracktoid.ui.activities.phone.ShoutsActivity;
import com.jakewharton.trakt.entities.Ratings;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public class ShowFragment extends PagerItemTraktFragment
{
	private TvShow s;
	private boolean existsInDb = false;

	public static ShowFragment newInstance(Bundle args)
	{
		ShowFragment f = new ShowFragment();
		f.setArguments(args);
		return f;
	}

	public static ShowFragment newInstance(TvShow s)
	{
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_TVSHOW, s);

		return newInstance(args);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		s = (TvShow) (getArguments() != null ? getArguments().getSerializable(TraktoidConstants.BUNDLE_TVSHOW) : null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		DatabaseWrapper dbw = getDBWrapper();
		existsInDb = dbw.showExist(s.tvdbId);
		getSherlockActivity().invalidateOptionsMenu();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		
		//check if user has already this show in his lib. If so hide the "add" button
		if(!existsInDb)
		{
			menu.add(0, R.id.action_bar_add, 0, "Add")
			.setIcon(R.drawable.ab_icon_add)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		menu.add(0, R.id.action_bar_shouts, 0, "Shouts")
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
			shows.add(s);
			tm.addToQueue(new UpdateShowsTask(tm, this, shows));
			return true;
		case R.id.action_bar_shouts :
			Intent i = new Intent(getActivity(), ShoutsActivity.class);
			i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, s.tvdbId);
			i.putExtra(TraktoidConstants.BUNDLE_TITLE, s.title);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

	@Override
	public Date getFirstAired() 
	{
		return s.firstAired;
	}

	@Override
	public Ratings getRatings() 
	{
		return s.ratings;
	}

	@Override
	public Rating getRating() 
	{
		return s.rating;
	}

	@Override
	public boolean isWatched() 
	{
		return s.progress == 100;
	}

	@Override
	public Image getImage() 
	{
		return new Image(s.tvdbId, s.images.fanart, Image.FANART);
	}

	@Override
	public String getOverview() 
	{
		return s.overview;
	}
}
