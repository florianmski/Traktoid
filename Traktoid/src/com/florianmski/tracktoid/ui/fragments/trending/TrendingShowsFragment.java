package com.florianmski.tracktoid.ui.fragments.trending;

import java.util.List;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.TvShow;

public class TrendingShowsFragment  extends TrendingFragment<TvShow>
{
	@Override
	public TraktApiBuilder<List<TvShow>> getTrendingBuilder() 
	{
		return TraktManager.getInstance().showService().trending();
	}
}
