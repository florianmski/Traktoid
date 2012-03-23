package com.florianmski.tracktoid.adapters.pagers;

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
		
		return f;
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
}