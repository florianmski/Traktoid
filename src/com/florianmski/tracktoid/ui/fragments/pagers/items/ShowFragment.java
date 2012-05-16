package com.florianmski.tracktoid.ui.fragments.pagers.items;

import android.os.Bundle;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.jakewharton.trakt.entities.TvShow;

public class ShowFragment extends PagerItemTraktFragment<TvShow>
{
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

		item = (TvShow) (getArguments() != null ? getArguments().getSerializable(TraktoidConstants.BUNDLE_TVSHOW) : null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		DatabaseWrapper dbw = getDBWrapper();
		existsInDb = dbw.showExist(item.tvdbId);
		getSherlockActivity().invalidateOptionsMenu();
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
