package com.florianmski.tracktoid.adapters.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.florianmski.tracktoid.TraktoidPrefs;
import com.florianmski.tracktoid.ui.fragments.EmptyFragment;
import com.florianmski.tracktoid.ui.fragments.user.NetworkFragment;
import com.florianmski.tracktoid.ui.fragments.user.ProfileFragment;

public class PagerUserAdapter extends FragmentStatePagerAdapter
{
    private String userId;
    private boolean isAppUser;

    private enum Title
    {
        Profile,
        Network
    }

    public PagerUserAdapter(FragmentManager fm, String userId)
    {
        super(fm);

        this.userId = userId;
        isAppUser = userId.toUpperCase().equals(TraktoidPrefs.INSTANCE.getUsername());
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return Title.values()[position].name();
    }

    @Override
    public Fragment getItem(int i)
    {
        switch(Title.values()[i])
        {
            case Profile:
                return ProfileFragment.newInstance(userId);
            case Network:
                return NetworkFragment.newInstance(userId);
            default:
                return EmptyFragment.newInstance();
        }
    }

    @Override
    public int getCount()
    {
        return isAppUser ? 1 : Title.values().length;
    }
}
