package com.florianmski.tracktoid.ui.fragments.search;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.WMovie;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.SearchResult;
import com.uwetrottmann.trakt.v2.enums.Type;

import java.util.ArrayList;
import java.util.List;

public class SearchMoviesFragment extends SearchFragment<WMovie>
{
    public static SearchMoviesFragment newInstance()
    {
        return new SearchMoviesFragment();
    }

    public SearchMoviesFragment() {}

    @Override
    protected List<WMovie> getSearch(String query)
    {
        List<WMovie> wMovies = new ArrayList<>();
        List<SearchResult> results = TraktManager.getInstance().search().textQuery(query, Type.MOVIE, null, null);
        for(SearchResult result : results)
            wMovies.add(new WMovie(result.movie));
        return wMovies;
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.MOVIE;
    }
}
