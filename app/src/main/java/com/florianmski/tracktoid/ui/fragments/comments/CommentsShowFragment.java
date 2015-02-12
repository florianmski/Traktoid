package com.florianmski.tracktoid.ui.fragments.comments;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.Comment;
import com.uwetrottmann.trakt.v2.enums.Extended;

import java.util.List;

public class CommentsShowFragment extends CommentsFragment
{
    public static CommentsShowFragment newInstance(String id)
    {
        CommentsShowFragment f = new CommentsShowFragment();
        f.setArguments(getBundle(id));
        return f;
    }

    @Override
    public List<Comment> getComments()
    {
        return TraktManager.getInstance().shows().comments(id, null, null, Extended.FULLIMAGES);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }
}
