package com.florianmski.tracktoid.ui.fragments.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.base.switcher.SwitchShowMovieFragment;

public class SearchSwitchFragment extends SwitchShowMovieFragment
{
	public static SearchSwitchFragment newInstance()
	{
		return new SearchSwitchFragment();
	}

    @Override
    public Fragment getShowFragment()
    {
        return SearchShowFragment.newInstance();
    }

    @Override
    public Fragment getMovieFragment()
    {
        return SearchMoviesFragment.newInstance();
    }

    public SearchSwitchFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
}
