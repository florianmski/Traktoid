package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.List;

import android.content.Context;

import com.florianmski.tracktoid.ApiCache;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.jakewharton.trakt.TraktApiBuilder;

public class TrendingTask<T> extends BaseTask<List<T>>
{
	private TrendingListener<T> listener;
	private TraktApiBuilder<List<T>> builder;

	public TrendingTask(Context context, TraktApiBuilder<List<T>> builder, TrendingListener<T> listener) 
	{
		super(context);

		this.builder = builder;
		this.listener = listener;
	}

	@Override
	protected List<T> doTraktStuffInBackground() 
	{
		List<T> traktItems = (List<T>) ApiCache.read(builder, context);
		if(traktItems != null)
			publishProgress(EVENT, traktItems);

		traktItems = (List<T>) builder.fire();

		ApiCache.save(builder, traktItems, context);

		return traktItems;
	}

	@Override
	protected void sendEvent(List<T> result) 
	{
		if(context != null && listener != null)
			listener.onTrending(result);
	}

	public interface TrendingListener<T>
	{
		public void onTrending(List<T> trending);
	}
}
