package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.ui.fragments.traktitems.MediaBaseFragment;
import com.florianmski.tracktoid.ui.fragments.traktitems.ShowFragment;

public class ShowActivity extends TraktItemActivity
{
    @Override
    protected MediaBaseFragment getFragment(String id, Bundle activityBundle)
    {
        return ShowFragment.newInstance(id);
    }

    public static void launch(Activity a, WShow wShow)
    {
        launchActivity(a, ShowActivity.class, getBundle(wShow));
    }
}