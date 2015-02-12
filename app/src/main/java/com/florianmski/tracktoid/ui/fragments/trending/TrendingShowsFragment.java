package com.florianmski.tracktoid.ui.fragments.trending;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.TrendingShow;
import com.uwetrottmann.trakt.v2.enums.Extended;

import java.util.ArrayList;
import java.util.List;

public class TrendingShowsFragment extends TrendingFragment<WShow>
{
    public static TrendingShowsFragment newInstance()
    {
        return new TrendingShowsFragment();
    }

    @Override
    public List<WShow> getTrending()
    {
        List<TrendingShow> trendingShows = TraktManager.getInstance().shows().trending(null, null, Extended.IMAGES);
        List<WShow> wShows = new ArrayList<WShow>();
        for(TrendingShow item : trendingShows)
            wShows.add(new WShow(item.show));
        return wShows;
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }
}
