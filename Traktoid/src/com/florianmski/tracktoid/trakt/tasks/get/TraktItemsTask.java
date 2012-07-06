package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;

import com.florianmski.tracktoid.ApiCache;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;

public class TraktItemsTask<T extends TraktoidInterface<T>> extends BaseTask<List<T>>
{	
	private List<T> traktItems = new ArrayList<T>();
	private TraktApiBuilder<?> builder;
	private boolean sort;
	private TraktItemsListener<T> listener;
	
	//TODO Split this task in more task
	//search task does no need caching
	//recommendation needs only if "all genres" and "no date"
	//trending needs it

	public TraktItemsTask(Activity context, TraktItemsListener<T> listener, TraktApiBuilder<?> builder, boolean sort) 
	{
		super(context);

		this.builder = builder;
		this.sort = sort;
		this.listener = listener;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<T> doTraktStuffInBackground()
	{		
		traktItems = (List<T>) ApiCache.read(builder, context);
		publishProgress(EVENT, traktItems);

		traktItems = (List<T>) builder.fire();

		if(sort && traktItems != null && traktItems.size() > 0)
			Collections.sort(traktItems);

		ApiCache.save(builder, traktItems, context);
		
		return traktItems;
	}

	@Override
	protected void sendEvent(List<T> result) 
	{
		if(context != null && listener != null)
			listener.onTraktItems(traktItems);
	}

	public interface TraktItemsListener<E extends TraktoidInterface<E>>
	{
		public void onTraktItems(List<E> traktItems);
	}
}
