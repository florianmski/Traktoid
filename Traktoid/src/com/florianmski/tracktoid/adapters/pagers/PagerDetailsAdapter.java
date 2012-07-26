package com.florianmski.tracktoid.adapters.pagers;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.PagerTabsViewFragment.TabsViewAdapter;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.MediaBase;

public class PagerDetailsAdapter<T extends TraktoidInterface<T>> extends TabsViewAdapter
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
		case android.R.layout.simple_list_item_1:
			TextView tvGenres = (TextView)v.findViewById(android.R.id.text1);
			//TODO do something better
			try
			{
				for(String text : ((MediaBase)item).genres)
					tvGenres.append(text);
			}
			catch(Exception e){}
			break;
		}
	}	

}
