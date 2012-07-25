package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.florianmski.tracktoid.ApiCache;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Genre;

public class RecommendationsTask<T> extends BaseTask<List<T>>
{
	private List<Genre> genres = new ArrayList<Genre>();
	private RecommendationsListener<T> listener;
	private TraktApiBuilder<List<Genre>> genresBuilder;
	private TraktApiBuilder<List<T>> recommendationsBuilder;
	
	private boolean sendCachedContent = true;
	private boolean shouldSaveResultInCache = true;

	public RecommendationsTask(Activity context, 
			TraktApiBuilder<List<Genre>> genresBuilder, 
			TraktApiBuilder<List<T>> recommendationsBuilder, 
			boolean sendCachedContent,
			boolean shouldSaveResultInCache,
			RecommendationsListener<T> listener) 
	{
		super(context);

		this.listener = listener;
		this.sendCachedContent = sendCachedContent;
		this.shouldSaveResultInCache = shouldSaveResultInCache;
		this.genresBuilder = genresBuilder;
		this.recommendationsBuilder = recommendationsBuilder;
	}

	@Override
	protected List<T> doTraktStuffInBackground()
	{		
		genres = ApiCache.read(genresBuilder, context);

		// we don't need to retrieve everytime genres
		// they might not change so we always use what we have in cache except the first time
		if(genres == null)
		{
			genres = genresBuilder.fire();
			Genre allGenre = new Genre();
			allGenre.name = "All genres";
			allGenre.slug = "all-genres";
			genres.add(0, allGenre);
			ApiCache.save(genresBuilder, genres, context);
		}
		
		List<T> recommendations = ApiCache.read(recommendationsBuilder, context);
		if(sendCachedContent)
			publishProgress(EVENT, recommendations);

		recommendations = recommendationsBuilder.fire();

		if(shouldSaveResultInCache)
			ApiCache.save(recommendationsBuilder, recommendations, context);
		
		return recommendations;
	}
	
	@Override
	protected List<T> doOfflineTraktStuff()
	{	
		return ApiCache.read(recommendationsBuilder, context);
	}

	@Override
	protected void sendEvent(List<T> result) 
	{
		if(context != null && listener != null && genres != null)
			listener.onRecommendations(genres, result);
	}

	public interface RecommendationsListener<T>
	{
		public void onRecommendations(List<Genre> genres, List<T> recommendations);
	}
}
