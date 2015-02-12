package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.ui.fragments.comments.CommentsEpisodeFragment;
import com.florianmski.tracktoid.ui.fragments.comments.CommentsFragment;
import com.florianmski.tracktoid.ui.fragments.comments.CommentsMovieFragment;
import com.florianmski.tracktoid.ui.fragments.comments.CommentsShowFragment;

public class CommentsActivity extends TranslucentActivity
{
    private final static int SHOW = 0, MOVIE = 1, EPISODE = 2;

    @Override
    protected int getContentViewId()
    {
        return R.layout.activity_comments;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null)
        {
            CommentsFragment f;

            Bundle activityBundle = getIntent().getExtras();
            String id = activityBundle.getString(TraktoidConstants.BUNDLE_ID);

            switch(getIntent().getExtras().getInt(TraktoidConstants.BUNDLE_TABLE))
            {
                case SHOW:
                    f = CommentsShowFragment.newInstance(id);
                    break;
                case MOVIE:
                    f = CommentsMovieFragment.newInstance(id);
                    break;
                case EPISODE:
                    int season = activityBundle.getInt(TraktoidConstants.BUNDLE_SEASON);
                    int episode = activityBundle.getInt(TraktoidConstants.BUNDLE_EPISODE);
                    f = CommentsEpisodeFragment.newInstance(id, season, episode);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_comments, f, null).commit();
        }
    }

    public static void launchShow(Activity a, String traktId)
    {
        launchActivity(a, CommentsActivity.class, getBundle(traktId, SHOW));
    }

    public static void launchMovie(Activity a, String traktId)
    {
        launchActivity(a, CommentsActivity.class, getBundle(traktId, MOVIE));
    }

    public static void launchEpisode(Activity a, String traktId, int season, int episode)
    {
        Bundle b = getBundle(traktId, EPISODE);
        b.putInt(TraktoidConstants.BUNDLE_SEASON, season);
        b.putInt(TraktoidConstants.BUNDLE_EPISODE, episode);
        launchActivity(a, CommentsActivity.class, b);
    }

    private static Bundle getBundle(String traktId, int type)
    {
        Bundle b = new Bundle();
        b.putString(TraktoidConstants.BUNDLE_ID, traktId);
        b.putInt(TraktoidConstants.BUNDLE_TABLE, type);
        return b;
    }
}
