package com.florianmski.tracktoid.ui.fragments.traktitems;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.florianmski.tracktoid.ApiCache;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.pagers.PagerTraktItemAdapter;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.ui.fragments.PagerFragment;
import com.florianmski.tracktoid.ui.fragments.trending.TrendingFragment;
import com.florianmski.traktoid.TraktoidInterface;

public class PagerTraktItemFragment<T extends TraktoidInterface<T>> extends PagerFragment
{	
	public static <T extends TraktoidInterface<T>> PagerTraktItemFragment<T> newInstance(Bundle args)
	{
		PagerTraktItemFragment<T> f = new PagerTraktItemFragment<T>();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setPageIndicatorType(PagerFragment.IT_UNDERLINE);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		getStatusView().show().text("Loading,\nPlease wait...");
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
				final List<T> items = new ArrayList<T>();
				int trendingType = getArguments().getInt(TraktoidConstants.BUNDLE_TRENDING, 0);
				
				if(trendingType != 0)
				{
					if(trendingType == TrendingFragment.MOVIE)
						items.addAll((List<T>) ApiCache.read(TraktManager.getInstance().movieService().trending(), getSherlockActivity()));
					else
						items.addAll((List<T>) ApiCache.read(TraktManager.getInstance().showService().trending(), getSherlockActivity()));
				}
				else
					items.addAll((List<T>)getArguments().getSerializable(TraktoidConstants.BUNDLE_RESULTS));

				getActivity().runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						adapter = new PagerTraktItemAdapter<T>(items, getFragmentManager(), getActivity());
						
						if(((PagerTraktItemAdapter<T>)adapter).isEmpty())
							getStatusView().hide().text("No items, this is strange...");
						else
							getStatusView().hide().text(null);
						
						initPagerFragment(adapter);
					}
				});
			}
		}.start();
	}
}
