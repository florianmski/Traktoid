package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.data.TraktoidItem;
import com.florianmski.tracktoid.ui.fragments.traktitems.TraktItemFragment;
import com.florianmski.tracktoid.ui.widgets.NotifyingScrollView;
import com.uwetrottmann.trakt.v2.entities.MoreImageSizes;

public abstract class TraktItemActivity extends HeaderActivity
{
    protected abstract TraktItemFragment getFragment(String id, Bundle activityBundle);

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle activityBundle = getIntent().getExtras();
        String id = activityBundle.getString(TraktoidConstants.BUNDLE_ID);

        TraktItemFragment fragment;

        if(savedInstanceState == null)
        {
            fragment = getFragment(id, activityBundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment, null).commit();
        }
        else
            fragment = (TraktItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        fragment.addScrollListener(new NotifyingScrollView.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt)
            {
                TraktItemActivity.this.onScrollChanged(who, t);
            }
        });
    }

    protected static Bundle getBundle(TraktoidItem traktoidItem)
    {
        MoreImageSizes header = traktoidItem.getTraktItem().images.fanart;
        if(header == null)
            header = traktoidItem.getTraktItem().images.screenshot;

        Bundle b = new Bundle();
        b.putString(TraktoidConstants.BUNDLE_ID, String.valueOf(traktoidItem.getIds().trakt));
        b.putString(TraktoidConstants.BUNDLE_HEADER, header.medium);
        return b;
    }

    public static void launchActivity(Activity a, Class<?> activityToLaunch, Bundle args, View v)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            a.startActivity(makeIntent(a, activityToLaunch, args), ActivityOptions.makeSceneTransitionAnimation(a, v, v.getTransitionName()).toBundle());
        else
            BaseActivity.launchActivity(a, activityToLaunch, args);
    }
}