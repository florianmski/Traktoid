package com.florianmski.tracktoid.ui.fragments.search;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.SearchResult;
import com.uwetrottmann.trakt.v2.enums.Type;

import java.util.ArrayList;
import java.util.List;

public class SearchShowFragment extends SearchFragment<WShow>
{
    public static SearchShowFragment newInstance()
    {
        return new SearchShowFragment();
    }

    public SearchShowFragment() {}

    @Override
    protected List<WShow> getSearch(String query)
    {
        List<WShow> wShows = new ArrayList<>();
        List<SearchResult> results = TraktManager.getInstance().search().textQuery(query, Type.SHOW, null, null);
        for(SearchResult result : results)
            wShows.add(new WShow(result.show));
        return wShows;
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }
}
