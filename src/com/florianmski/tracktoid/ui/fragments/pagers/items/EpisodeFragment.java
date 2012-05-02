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
	
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
//	{
//		super.onCreateOptionsMenu(menu, inflater);
//		if seasonId is null, this episode is not in our db
//		if(this.item != null && !this.item.watched)
//		{
//			menu.add(0, R.id.action_bar_watched, 0, "Watched")
//				.setIcon(R.drawable.ab_icon_eye)
//				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//		}
//		menu.add(0, R.id.action_bar_shouts, 0, "Shouts")
//		.setIcon(R.drawable.ab_icon_shouts)
//		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) 
//	{
//		switch(item.getItemId())
//		{
//		case R.id.action_bar_watched :
//				getSherlockActivity().invalidateOptionsMenu();
//				Utils.chooseBetweenSeenAndCheckin(new WatchedEpisodesTask(tm, this, tvdbId, this.item.season, this.item.number, !this.item.watched), getActivity());
//			return true;
//		case R.id.action_bar_shouts :
//			Intent i = new Intent(getActivity(), ShoutsActivity.class);
//			i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, tvdbId);
//			i.putExtra(TraktoidConstants.BUNDLE_EPISODE, this.item);
//			startActivity(i);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

}
