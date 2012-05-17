package com.florianmski.tracktoid.ui.fragments.pagers.items;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class PI_TraktItemEpisodeFragment extends PI_TraktItemFragment<TvShowEpisode>
{	
	public static PI_TraktItemEpisodeFragment newInstance(Bundle args)
	{
		PI_TraktItemEpisodeFragment f = new PI_TraktItemEpisodeFragment();
		f.setArguments(args);
		return f;
	}
	
	public static PI_TraktItemEpisodeFragment newInstance(TvShowEpisode e)
	{
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_TRAKT_ITEM, e);

		return newInstance(args);
	}

	public PI_TraktItemEpisodeFragment() {}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		if(item != null)
			setSubtitle("S" + Utils.addZero(item.season) + " E" + Utils.addZero(item.number));
	}
	
	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

}
