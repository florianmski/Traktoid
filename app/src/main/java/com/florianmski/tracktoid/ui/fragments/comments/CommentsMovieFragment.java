package com.florianmski.tracktoid.ui.fragments.comments;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.Comment;
import com.uwetrottmann.trakt.v2.enums.Extended;

import java.util.List;

public class CommentsMovieFragment extends CommentsFragment
{
    public static CommentsMovieFragment newInstance(String id)
    {
        CommentsMovieFragment f = new CommentsMovieFragment();
        f.setArguments(getBundle(id));
        return f;
    }

    @Override
    public List<Comment> getComments()
    {
        return TraktManager.getInstance().movies().comments(id, null, null, Extended.FULLIMAGES);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.MOVIE;
    }
}