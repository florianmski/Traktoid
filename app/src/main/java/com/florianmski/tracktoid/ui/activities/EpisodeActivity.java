package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.data.WEpisode;
import com.florianmski.tracktoid.ui.fragments.traktitems.EpisodeFragment;
import com.florianmski.tracktoid.ui.fragments.traktitems.TraktItemFragment;

public class EpisodeActivity extends TraktItemActivity
{
    @Override
    protected TraktItemFragment<?> getFragment(String id, Bundle activityBundle)
    {
        String showId = activityBundle.getString(TraktoidConstants.BUNDLE_SHOW_ID);
        int season = activityBundle.getInt(TraktoidConstants.BUNDLE_SEASON);
        int episode = activityBundle.getInt(TraktoidConstants.BUNDLE_EPISODE);

        return EpisodeFragment.newInstance(showId, season, id, episode);
    }

    public static void launch(Activity a, WEpisode wEpisode)
    {
        Bundle b = getBundle(wEpisode);
//        b.putString(TraktoidConstants.BUNDLE_TITLE, title.toString());
        b.putString(TraktoidConstants.BUNDLE_SHOW_ID, String.valueOf(wEpisode.showId));
        b.putInt(TraktoidConstants.BUNDLE_SEASON, wEpisode.getTraktItem().season);
        b.putInt(TraktoidConstants.BUNDLE_EPISODE, wEpisode.getTraktItem().number);
        launchActivity(a, EpisodeActivity.class, b);
    }
}
