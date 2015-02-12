package com.florianmski.tracktoid.ui.fragments.library;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.ui.fragments.base.switcher.SwitchShowMovieFragment;

public class LibrarySwitchFragment extends SwitchShowMovieFragment
{
    public LibrarySwitchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public static LibrarySwitchFragment newInstance()
    {
        return new LibrarySwitchFragment();
    }

    @Override
    public Fragment getShowFragment()
    {
        return LibraryShowFragment.newInstance();
    }

    @Override
    public Fragment getMovieFragment()
    {
        return LibraryMovieFragment.newInstance();
    }
}
