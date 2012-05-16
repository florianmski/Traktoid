package com.florianmski.tracktoid.ui.fragments.pagers;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.R;
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
		mTabsAdapter.addTab("Shows", ShowsLibraryFragment.class, null);
		mTabsAdapter.addTab("Movies", MoviesLibraryFragment.class, null);
		
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.fragment_library, null);
	}
}
