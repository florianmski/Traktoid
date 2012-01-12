package com.florianmski.tracktoid.ui.fragments.pagers.items;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListEpisodeAdapter;
import com.florianmski.tracktoid.ui.activities.phone.EpisodeActivity;
import com.jakewharton.trakt.entities.TvShowSeason;

public class SeasonFragment extends PagerItemFragment
{
	private TvShowSeason season;
	private String tvdbId;
	private static OnConstructionListener listener;
	
	public static SeasonFragment newInstance(Bundle args)
	{
		SeasonFragment f = new SeasonFragment();
		f.setArguments(args);
		return f;
	}
	
	public static SeasonFragment newInstance(TvShowSeason season, String tvdbId)
	{		
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_SEASON, season);
		args.putString(TraktoidConstants.BUNDLE_TVDB_ID, tvdbId);

		return newInstance(args);
	}
	
	public static void setListener(OnConstructionListener listener)
	{
		SeasonFragment.listener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		if(getArguments() != null)
		{
			season = (TvShowSeason) getArguments().getSerializable(TraktoidConstants.BUNDLE_SEASON);
			tvdbId = getArguments().getString(TraktoidConstants.BUNDLE_TVDB_ID);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_season, container, false);
		
		ListView lvEpisodes = (ListView)v.findViewById(R.id.listViewEpisodes);
		ImageView ivBackground = (ImageView)v.findViewById(R.id.imageViewBackground);

		//TODO
		//this is fucking ugly
		//I can't find a solution for now, if someone has an idea it would be great
		//I can't pass adapter into seasonfragment because listepisodeadapter isn't serializable (error when fragmentpager want to save its state)
		//so I come up with this crappy solution
		//maybe I shouldn't keep a list of adapters in memory (see PagerSeasonAdapter) 
		//but it allows a really simple management for this complicated activity 
		//works great though :)
		lvEpisodes.setAdapter(listener.iNeedAdapter(season.season));

		lvEpisodes.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int episode, long arg3) 
			{
				Intent i = new Intent(getActivity(), EpisodeActivity.class);
				i.putExtra(TraktoidConstants.BUNDLE_SEASON_ID, season.url);
				i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, tvdbId);
				i.putExtra(TraktoidConstants.BUNDLE_TITLE, getArguments().getString(TraktoidConstants.BUNDLE_TITLE));
				//TODO if episode is 0 something is wrong here
				i.putExtra(TraktoidConstants.BUNDLE_POSITION, episode);
				startActivity(i);
			}
		});

		return v;
	}
	
	public interface OnConstructionListener
	{
		public ListEpisodeAdapter iNeedAdapter(int season);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		// TODO Auto-generated method stub
	}
}