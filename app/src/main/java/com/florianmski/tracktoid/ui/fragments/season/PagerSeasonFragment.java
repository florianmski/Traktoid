package com.florianmski.tracktoid.ui.fragments.season;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.pagers.PagerSeasonAdapter;
import com.florianmski.tracktoid.ui.fragments.base.pager.PagerFragment;

public class PagerSeasonFragment extends PagerFragment
{
    private String showId;
    private String[] seasonIds;
    private int[] seasons;

    public static PagerSeasonFragment newInstance(String showId, String[] seasonIds, int[] seasons, int position)
    {
        PagerSeasonFragment f = new PagerSeasonFragment();
        Bundle args = new Bundle();
        args.putString(TraktoidConstants.BUNDLE_SHOW_ID, showId);
        args.putStringArray(TraktoidConstants.BUNDLE_IDS, seasonIds);
        args.putIntArray(TraktoidConstants.BUNDLE_SEASONS, seasons);
        args.putInt(TraktoidConstants.BUNDLE_POSITION, position);
        f.setArguments(args);
        return f;
    }

    public PagerSeasonFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        showId = getArguments().getString(TraktoidConstants.BUNDLE_SHOW_ID);
        seasonIds = getArguments().getStringArray(TraktoidConstants.BUNDLE_IDS);
        seasons = getArguments().getIntArray(TraktoidConstants.BUNDLE_SEASONS);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tabs.setVisibility(View.GONE);
    }

    @Override
    protected PagerAdapter getPagerAdapter()
    {
        return new PagerSeasonAdapter(showId, seasonIds, seasons, getChildFragmentManager());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }

    @Override
    public void onPageSelected(int position)
    {
        super.onPageSelected(position);
        setSubtitle(getPagerAdapter().getPageTitle(position).toString());
    }
}
