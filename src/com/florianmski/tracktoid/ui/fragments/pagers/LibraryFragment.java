package com.florianmski.tracktoid.ui.fragments.pagers;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.pagers.PagerLibraryAdapter;
import com.florianmski.tracktoid.adapters.pagers.PagerShowAdapter;
import com.florianmski.tracktoid.ui.fragments.pagers.items.MoviesLibraryFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.ShowsLibraryFragment;


public class LibraryFragment extends TabsPagerFragment 
{	
	public static LibraryFragment newInstance(Bundle args)
	{
		LibraryFragment f = new LibraryFragment();
		f.setArguments(args);
		return f;
	}

	public LibraryFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void addTab(TabsAdapter mTabsAdapter) 
	{
		mTabsAdapter.addTab(mTabHost.newTabSpec("shows").setIndicator("Shows"), ShowsLibraryFragment.class, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("movies").setIndicator("Movies"), MoviesLibraryFragment.class, null);
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.fragment_library, null);
	}
}
