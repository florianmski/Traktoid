package com.florianmski.tracktoid.adapters.pagers;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.PagerTabsViewFragment.TabsViewAdapter;
import com.florianmski.traktoid.TraktoidInterface;

public class PagerDetailsAdapter<T extends TraktoidInterface> extends TabsViewAdapter
{
	private T item;
	
	public PagerDetailsAdapter(FragmentActivity activity, TabHost tabHost,	ViewPager pager, T item) 
	{
		super(activity, tabHost, pager);
		this.item = item;
	}

	@Override
	public void fillLayout(View v, int layoutId)
	{
		switch(layoutId)
		{
		case R.layout.pager_item_details_summary:
			TextView tvOverview = (TextView)v.findViewById(R.id.textViewOverview);
			tvOverview.setText(item.getOverview());
			break;
		}
	}	

}
