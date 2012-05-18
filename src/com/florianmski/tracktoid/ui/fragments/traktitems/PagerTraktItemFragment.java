package com.florianmski.tracktoid.ui.fragments.traktitems;

import java.util.List;

import android.os.Bundle;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.pagers.PagerTraktItemAdapter;
import com.florianmski.tracktoid.ui.fragments.PagerFragment;
import com.florianmski.traktoid.TraktoidInterface;

public abstract class PagerTraktItemFragment<T extends TraktoidInterface> extends PagerFragment
{	
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
				final List<T> items = (List<T>)getArguments().getSerializable(TraktoidConstants.BUNDLE_RESULTS);

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
