package com.florianmski.tracktoid.ui.fragments.recommendations;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.base.switcher.SwitchShowMovieFragment;

public class RecommendationSwitchFragment extends SwitchShowMovieFragment
{
    public RecommendationSwitchFragment() {}

    public static RecommendationSwitchFragment newInstance()
    {
        return new RecommendationSwitchFragment();
    }

    @Override
    public Fragment getShowFragment()
    {
        return RecommendationShowsFragment.newInstance();
    }

    @Override
    public Fragment getMovieFragment()
    {
        return RecommendationMoviesFragment.newInstance();
    }
}
