package com.florianmski.tracktoid.adapters.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.florianmski.tracktoid.ui.fragments.library.PI_LibaryMovieFragment;
import com.florianmski.tracktoid.ui.fragments.library.PI_LibraryFragment;
import com.florianmski.tracktoid.ui.fragments.library.PI_LibraryShowFragment;
import com.florianmski.traktoid.TraktoidInterface;
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
		PI_LibraryFragment<? extends TraktoidInterface> f = null;
		
		if(position == 0)
			f = PI_LibraryShowFragment.newInstance(null);
		else
			f = PI_LibaryMovieFragment.newInstance(null);
		
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