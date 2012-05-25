package com.florianmski.tracktoid.ui.fragments.traktitems;

import android.os.Bundle;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.jakewharton.trakt.entities.TvShow;

public class PI_TraktItemShowFragment extends PI_TraktItemFragment<TvShow>
{
	private boolean existsInDb = false;

	public static PI_TraktItemShowFragment newInstance(Bundle args)
	{
		PI_TraktItemShowFragment f = new PI_TraktItemShowFragment();
		f.setArguments(args);
		return f;
	}

	public static PI_TraktItemShowFragment newInstance(TvShow s)
	{
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_TRAKT_ITEM, s);

		return newInstance(args);
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
