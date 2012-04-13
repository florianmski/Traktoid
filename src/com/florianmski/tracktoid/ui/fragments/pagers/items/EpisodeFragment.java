package com.florianmski.tracktoid.ui.fragments.pagers.items;

import android.os.Bundle;
import com.florianmski.tracktoid.TraktoidConstants;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class EpisodeFragment extends PagerItemTraktFragment<TvShowEpisode>
{
	private String tvdbId;

	public static EpisodeFragment newInstance(Bundle args)
	{
		EpisodeFragment f = new EpisodeFragment();
		f.setArguments(args);
		return f;
	}

	public static EpisodeFragment newInstance(TvShowEpisode e, String tvdbId)
	{
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_EPISODE, e);
		args.putString(TraktoidConstants.BUNDLE_TVDB_ID, tvdbId);

		return newInstance(args);
	}

	public EpisodeFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if(getArguments() != null)
		{
			item = (TvShowEpisode) getArguments().getSerializable(TraktoidConstants.BUNDLE_EPISODE);
			tvdbId = getArguments().getString(TraktoidConstants.BUNDLE_TVDB_ID);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

}
