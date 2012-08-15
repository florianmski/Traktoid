package com.florianmski.tracktoid.ui.fragments.trending;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListTrendingAdapter;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.florianmski.tracktoid.trakt.tasks.get.TrendingTask;
import com.florianmski.tracktoid.trakt.tasks.get.TrendingTask.TrendingListener;
import com.florianmski.tracktoid.ui.activities.TraktItemsActivity;
import com.florianmski.tracktoid.ui.fragments.BaseFragment.TaskListener;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;

public abstract class TrendingFragment<T extends TraktoidInterface<T>> extends TraktFragment implements TaskListener
{		
	private ListView lvTrending;

	protected ListTrendingAdapter<T> adapter;
	
	private List<T> items;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setTaskListener(this);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	public abstract TraktApiBuilder<List<T>> getTrendingBuilder();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		lvTrending.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Bundle b = new Bundle();
				//TODO this is too big to use bundles
				//instead get them in the cache ;)
				b.putSerializable(TraktoidConstants.BUNDLE_RESULTS, (ArrayList<T>) adapter.getItems());
				b.putInt(TraktoidConstants.BUNDLE_POSITION, position);
				launchActivity(TraktItemsActivity.class, b);
			}
		});
	}

	protected BaseTask<?> createGetRecommendationsTask(boolean sendCachedContent, boolean silent)
	{
		getStatusView().show().text("Retrieving trending,\nPlease wait...");

		return task = new TrendingTask<T>(getSherlockActivity(), getTrendingBuilder(), new TrendingListener<T>() {
			@Override
			public void onTrending(List<T> trending) 
			{
				TrendingFragment.this.items = trending;
				setAdapter();
			}
		});
	}
	
	protected void setAdapter()
	{
		if(adapter == null)
			lvTrending.setAdapter(adapter = new ListTrendingAdapter<T>(items, getActivity()));
		else
		{
			adapter.refreshItems(items);
			if(lvTrending.getAdapter() == null)
				lvTrending.setAdapter(adapter);
		}

		if(adapter.getCount() == 0)
			getStatusView().hide().text("No trending, strange...");
		else
			getStatusView().hide().text(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_trending, null);

		lvTrending = (ListView)v.findViewById(R.id.listViewTrending);

		return v;
	}

	@Override
	public void onCreateTask() 
	{
		createGetRecommendationsTask(true, false);
		task.fire();
	}

	@Override
	public void onTaskIsDone() 
	{
		setAdapter();
	}

	@Override
	public void onTaskIsRunning() {}
}