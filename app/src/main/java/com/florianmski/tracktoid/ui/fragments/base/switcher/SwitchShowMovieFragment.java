package com.florianmski.tracktoid.ui.fragments.base.switcher;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.TraktoidTheme;

public abstract class SwitchShowMovieFragment extends SwitchFragment
{
    private enum State
    {
        SHOW,
        MOVIE
    }

    public abstract Fragment getShowFragment();
    public abstract Fragment getMovieFragment();

    public SwitchShowMovieFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Fragment getFragment(int index)
    {
        switch(State.values()[index])
        {
            case SHOW:
                return getShowFragment();
            case MOVIE:
                return getMovieFragment();
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public int getCount()
    {
        return State.values().length;
    }

    @Override
    protected void replaceFragment(Fragment previousFragment, Fragment nextFragment)
    {
        super.replaceFragment(previousFragment, nextFragment);

        setupFAB();
    }

    private void setupFAB()
    {
        // switch to correct color for FAB
        TraktoidTheme theme = getTheme();
        fab.setTheme(theme == TraktoidTheme.MOVIE ? TraktoidTheme.SHOW : TraktoidTheme.MOVIE);
        getActionBar().setSubtitle(theme == TraktoidTheme.MOVIE ? "Movie" : "Show");
    }
}
