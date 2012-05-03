package com.florianmski.tracktoid.ui.fragments.pagers;

import java.util.List;

import android.os.Bundle;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.pagers.PagerTraktItemAdapter;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.TvShow;

public class TraktItemPagerFragment<T extends TraktoidInterface<T>> extends PagerFragment
{
	//TODO onShowUpdated()
	private T traktItem;
	
	public static TraktItemPagerFragment newInstance(Bundle args)
	{
		TraktItemPagerFragment f = new TraktItemPagerFragment();
		f.setArguments(args);
		return f;
	}
	
	public TraktItemPagerFragment() {}
	
	public TraktItemPagerFragment(FragmentListener listener) 
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
		
		getStatusView().show().text("Loading items,\nPlease wait...");
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
				final List<TvShow> shows = (List<TvShow>)getArguments().getSerializable(TraktoidConstants.BUNDLE_RESULTS);

				getActivity().runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						adapter = new PagerTraktItemAdapter(shows, getFragmentManager(), getActivity());
						
						if(((PagerTraktItemAdapter)adapter).isEmpty())
							getStatusView().hide().text("No items, this is strange...");
						else
							getStatusView().hide().text(null);
						
						initPagerFragment(adapter);
					}
				});
			}
		}.start();
	}

	@Override
	public void onPageSelected(int position) 
	{
		super.onPageSelected(position);

		traktItem = (T) ((PagerTraktItemAdapter)adapter).getTraktItem(currentPagerPosition);
		setTitle(traktItem.getTitle());
	}
}
