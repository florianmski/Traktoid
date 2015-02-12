package com.florianmski.tracktoid.ui.fragments.recommendations;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.Genre;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import java.util.List;

import retrofit.client.Response;

public class RecommendationShowsFragment extends RecommendationFragment<WShow> implements RecyclerAdapter.OnItemClickListener
{
	public static RecommendationShowsFragment newInstance()
	{
		return new RecommendationShowsFragment();
	}

	public RecommendationShowsFragment() {}

	@Override
	public Response fireDismiss(String id) throws OAuthUnauthorizedException
	{
		return TraktManager.getInstance().recommendations().dismissShow(id);
	}

	@Override
	public List<WShow> fireRecommendations() throws OAuthUnauthorizedException
	{
		return WShow.toList(TraktManager.getInstance().recommendations().shows(Extended.IMAGES));
	}

	@Override
	public List<Genre> fireGenres()
	{
        return TraktManager.getInstance().genres().shows();
	}

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }
}