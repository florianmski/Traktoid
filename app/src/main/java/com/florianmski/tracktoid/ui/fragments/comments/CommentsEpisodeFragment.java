package com.florianmski.tracktoid.ui.fragments.comments;

import android.os.Bundle;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.Comment;
import com.uwetrottmann.trakt.v2.enums.Extended;

import java.util.List;

public class CommentsEpisodeFragment extends CommentsFragment
{
    private int season;
    private int episode;

    public static CommentsEpisodeFragment newInstance(String showId, int season, int episode)
    {
        CommentsEpisodeFragment f = new CommentsEpisodeFragment();
        Bundle args = getBundle(showId);
        args.putInt(TraktoidConstants.BUNDLE_SEASON, season);
        args.putInt(TraktoidConstants.BUNDLE_EPISODE, episode);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        season = getArguments().getInt(TraktoidConstants.BUNDLE_SEASON);
        episode = getArguments().getInt(TraktoidConstants.BUNDLE_EPISODE);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }

    @Override
    public List<Comment> getComments()
    {
        return TraktManager.getInstance().episodes().comments(id, season, episode, null, null, Extended.FULLIMAGES);
    }
}