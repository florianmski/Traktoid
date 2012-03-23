package com.florianmski.tracktoid.adapters.pagers;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.florianmski.tracktoid.ui.fragments.pagers.items.MoviesLibraryFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.PagerItemLibraryFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.ShowsLibraryFragment;
import com.viewpagerindicator.TitleProvider;

public class PagerLibraryAdapter extends FragmentPagerAdapter implements TitleProvider
{
	private final static String titles[] = {"Shows", "Movies"};
	
	private ArrayList<OnFilterListener> filterListeners = new ArrayList<PagerLibraryAdapter.OnFilterListener>();
	
	public PagerLibraryAdapter(FragmentManager fm) 
	{
		super(fm);
	}

	@Override
	public String getTitle(int position) 
	{
		return titles[position];
	}

	@Override
	public Fragment getItem(int position) 
	{
		PagerItemLibraryFragment f = null;
		if(position == 0)
			f = ShowsLibraryFragment.newInstance(null);
		else
			f = MoviesLibraryFragment.newInstance(null);
		
		if(position < filterListeners.size())
			filterListeners.set(position, f);
		else
			filterListeners.add(position, f);
		
		return f;
	}
	
	public void fireFilterEvent(int pagerPosition, int filter, long itemId)
	{
		if(pagerPosition < filterListeners.size())
		{
			OnFilterListener listener = filterListeners.get(pagerPosition);
			if(listener != null)
				listener.onFilterClicked(filter, itemId);
			else
				filterListeners.remove(pagerPosition);
		}
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		super.destroyItem(container, position, object);
	}

	@Override
	public int getCount() 
	{
		return titles.length;
	}
	
	public interface OnFilterListener
	{
		public void onFilterClicked(int filter, long itemId);
	}
}