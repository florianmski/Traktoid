package com.florianmski.tracktoid.ui.fragments.trending;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.base.switcher.SwitchShowMovieFragment;

public class TrendingSwitchFragment extends SwitchShowMovieFragment
{
    public TrendingSwitchFragment() {}

    public static TrendingSwitchFragment newInstance()
    {
        return new TrendingSwitchFragment();
    }

    @Override
    public Fragment getShowFragment()
    {
        return TrendingShowsFragment.newInstance();
    }

    @Override
    public Fragment getMovieFragment()
    {
        return TrendingMoviesFragment.newInstance();
    }
}
