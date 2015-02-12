package com.florianmski.tracktoid.ui.fragments.trending;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.WMovie;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.TrendingMovie;
import com.uwetrottmann.trakt.v2.enums.Extended;

import java.util.ArrayList;
import java.util.List;

public class TrendingMoviesFragment extends TrendingFragment<WMovie>
{
    public static TrendingMoviesFragment newInstance()
    {
        return new TrendingMoviesFragment();
    }

	@Override
	public List<WMovie> getTrending()
	{
        List<TrendingMovie> trendingMovies = TraktManager.getInstance().movies().trending(null, null, Extended.IMAGES);
        List<WMovie> wMovies = new ArrayList<WMovie>();
        for(TrendingMovie item : trendingMovies)
            wMovies.add(new WMovie(item.movie));
        return wMovies;
	}

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.MOVIE;
    }
}
