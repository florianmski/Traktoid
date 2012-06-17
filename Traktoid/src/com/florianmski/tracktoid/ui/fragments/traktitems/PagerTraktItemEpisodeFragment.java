package com.florianmski.tracktoid.ui.fragments.traktitems;

import android.os.Bundle;

import com.jakewharton.trakt.entities.TvShowEpisode;

public class PagerTraktItemEpisodeFragment extends PagerTraktItemFragment<TvShowEpisode>
{
	private String tvdbId;
	private String seasonId;
	
	public static PagerTraktItemEpisodeFragment newInstance(Bundle args)
	{
		PagerTraktItemEpisodeFragment f = new PagerTraktItemEpisodeFragment();
		f.setArguments(args);
		return f;
	}
	
	public PagerTraktItemEpisodeFragment() {}

//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) 
//	{
//		super.onActivityCreated(savedInstanceState);
//		
//		getStatusView().show().text("Loading episodes,\nPlease wait...");
//
//		setSubtitle(getArguments().getString(TraktoidConstants.BUNDLE_TITLE));
//
//		tvdbId = getArguments().getString(TraktoidConstants.BUNDLE_TVDB_ID);
//		seasonId = getArguments().getString(TraktoidConstants.BUNDLE_SEASON_ID);
//
//		@SuppressWarnings("unchecked")
//		ArrayList<TvShowEpisode> episodes = (ArrayList<TvShowEpisode>)getArguments().getSerializable(TraktoidConstants.BUNDLE_RESULTS);
//		if(episodes == null)
//			new DBEpisodesTask(getActivity(), new DBAdapter() 
//			{
//				@Override
//				public void onDBEpisodes(List<TvShowEpisode> episodes) 
//				{
//					adapter = new PagerEpisodeAdapter(episodes, tvdbId, getFragmentManager());
//					
//					if(((PagerEpisodeAdapter)adapter).isEmpty())
//						getStatusView().hide().text("No episodes, this is strange...");
//					else
//						getStatusView().hide().text(null);
//					
//					initPagerFragment(adapter);
//				}
//			}, seasonId, tvdbId).fire();
//		else
//		{
//			adapter = new PagerEpisodeAdapter(episodes, tvdbId, getFragmentManager());
//			
//			if(((PagerEpisodeAdapter)adapter).isEmpty())
//				getStatusView().hide().text("No episodes, this is strange...");
//			else
//				getStatusView().hide().text(null);
//			
//			initPagerFragment(adapter);
//		}
//	}
	
//	@Override
//	public void onShowUpdated(TvShow show) 
//	{
//		if(show.tvdbId.equals(tvdbId) && adapter != null && seasonId != null)
//			new DBEpisodesTask(getActivity(), new DBAdapter()
//			{
//				@Override
//				public void onDBEpisodes(List<TvShowEpisode> episodes) 
//				{
//					((PagerEpisodeAdapter)adapter).reloadData(episodes);
//					getSherlockActivity().invalidateOptionsMenu();
//				}
//			}, seasonId, tvdbId).fire();
//	}
//
//	@Override
//	public void onShowRemoved(TvShow show)
//	{
//		if(show.tvdbId.equals(tvdbId))
//			getActivity().finish();
//	}
}
