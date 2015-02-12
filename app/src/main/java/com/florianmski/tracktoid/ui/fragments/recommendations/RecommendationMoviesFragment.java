package com.florianmski.tracktoid.ui.fragments.recommendations;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.data.WMovie;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.Genre;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import java.util.List;

import retrofit.client.Response;

public class RecommendationMoviesFragment extends RecommendationFragment<WMovie> implements RecyclerAdapter.OnItemClickListener
{	
	private final static String[] GENRES = new String[] {
		"action", 
		"adventure", 
		"animation", 
		"comedy",
		"crime", 
		"documentary", 
		"drama", 
		"family", 
		"fantasy", 
		"film-noir",
		"history", 
		"horror", 
		"indie", 
		"music", 
		"musical", 
		"mystery", 
		"romance",
		"science-fiction", 
		"sport", 
		"suspense", 
		"thriller", 
		"war", 
		"western"
	};

	public static RecommendationMoviesFragment newInstance()
	{
		return new RecommendationMoviesFragment();
	}

	public RecommendationMoviesFragment() {}

    @Override
	public Response fireDismiss(String id) throws OAuthUnauthorizedException
    {
		return TraktManager.getInstance().recommendations().dismissMovie(id);
	}

	@Override
	public List<WMovie> fireRecommendations() throws OAuthUnauthorizedException
    {
        return WMovie.toList(TraktManager.getInstance().recommendations().movies(Extended.IMAGES));
	}

	@Override
	public List<Genre> fireGenres()
	{
		return TraktManager.getInstance().genres().movies();
	}

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.MOVIE;
    }
}
