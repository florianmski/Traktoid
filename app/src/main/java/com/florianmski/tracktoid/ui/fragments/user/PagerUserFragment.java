package com.florianmski.tracktoid.ui.fragments.user;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.pagers.PagerUserAdapter;
import com.florianmski.tracktoid.ui.fragments.base.pager.PagerHeaderImageFragment;

public class PagerUserFragment extends PagerHeaderImageFragment
{
    public PagerUserFragment() {}

    public static PagerUserFragment newInstance(String id, int position)
    {
        PagerUserFragment f = new PagerUserFragment();
        Bundle args = getBundle(id, position);
        f.setArguments(args);
        return f;
    }

    public static PagerUserFragment newInstance(String id)
    {
        return newInstance(id, 0);
    }

    @Override
    protected PagerAdapter getPagerAdapter()
    {
        return new PagerUserAdapter(getChildFragmentManager(), id);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.DEFAULT;
    }
}