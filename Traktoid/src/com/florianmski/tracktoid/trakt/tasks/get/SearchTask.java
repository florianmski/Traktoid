package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.List;

import android.content.Context;

import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.florianmski.tracktoid.ui.fragments.SearchFragment;

public class SearchTask extends BaseTask<List<?>>
{
	private int searchType;
	private String query;
	private SearchListener listener;

	public SearchTask(Context context, int searchType, String query, SearchListener listener) 
	{
		super(context);
		
		this.searchType = searchType;
		this.query = query;
		this.listener = listener;
	}

	@Override
	protected void sendEvent(List<?> results) 
	{
		if(context != null && listener != null)
			listener.onSearch(results);
	}

	@Override
	protected List<?> doTraktStuffInBackground() 
	{		
		switch(searchType)
		{
		case SearchFragment.SHOWS:
			return tm.searchService().shows(query).fire();
		case SearchFragment.MOVIES:
			return tm.searchService().movies(query).fire();
//		case SearchFragment.EPISODES:
//			return tm.searchService().episodes(query).fire();
//		case SearchFragment.PEOPLES:
//			return tm.searchService().people(query).fire();
//		case SearchFragment.USERS:
//			return tm.searchService().users(query).fire();
		}
		
		return null;
	}

	public interface SearchListener
	{
		public void onSearch(List<?> results);
	}
}
