package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.florianmski.tracktoid.data.WMovie;
import com.florianmski.tracktoid.ui.fragments.traktitems.MediaBaseFragment;
import com.florianmski.tracktoid.ui.fragments.traktitems.MovieFragment;

public class MovieActivity extends TraktItemActivity
{
    @Override
    protected MediaBaseFragment getFragment(String id, Bundle activityBundle)
    {
        return MovieFragment.newInstance(id);
    }

    public static void launch(Activity a, WMovie wMovie)
    {
        launchActivity(a, MovieActivity.class, getBundle(wMovie));
    }
}