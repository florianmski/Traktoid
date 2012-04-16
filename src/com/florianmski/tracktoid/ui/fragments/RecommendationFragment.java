package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListRecommendationAdapter;
import com.florianmski.tracktoid.adapters.lists.ListRecommendationAdapter.DismissListener;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.trakt.tasks.get.GenresTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.ui.activities.phone.ShowActivity;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Genre;

public abstract class RecommendationFragment<T extends TraktoidInterface> extends TraktFragment implements ActionBar.OnNavigationListener
{	
	protected ListView lvRecommendations;

	protected ListRecommendationAdapter<T> adapter;

	protected List<Genre> genres;
	protected ArrayList<T> items;

	protected int recreation;

	public RecommendationFragment() {}

	public RecommendationFragment(FragmentListener listener) 
	{
		super(listener);
	}
	
	public abstract GenresTask getGenresTask();
	public abstract PostTask getDismissTask(String id);
	public abstract TraktTask getItemsTask(Genre genre);

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		recreation = (items != null || savedInstanceState != null) ? 2 : 0;

		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		if(recreation == 0)
		{
			getStatusView().show().text("Retrieving genres,\nPlease wait...");

			getGenresTask().fire();
		}
		else
		{
			setListNavigationMode();
			setAdapter();
		}

		lvRecommendations.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Intent intent = new Intent(getActivity(), ShowActivity.class);
				intent.putExtra(TraktoidConstants.BUNDLE_RESULTS, adapter.getRecommendations());
				intent.putExtra(TraktoidConstants.BUNDLE_POSITION, position);
				startActivity(intent);
			}

		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_recommendation, null);

		lvRecommendations = (ListView)v.findViewById(R.id.listViewRecommendation);

		return v;
	}

	protected void setListNavigationMode()
	{
		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] items = new String[genres.size()+1];
		items[0] = "All Genres";

		for(int i = 1; i < items.length; i++)
			items[i] = genres.get(i-1).name;

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.sherlock_spinner_item, items);
		spinnerAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSherlockActivity().getSupportActionBar().setListNavigationCallbacks(spinnerAdapter, this);
	}

	protected void setAdapter()
	{
		if(adapter == null)
		{
			adapter = new ListRecommendationAdapter<T>(items, getActivity());
			lvRecommendations.setAdapter(adapter);
		}
		else
		{
			adapter.refreshData(items);
			if(lvRecommendations.getAdapter() == null)
				lvRecommendations.setAdapter(adapter);
		}

		if(adapter.getCount() == 0)
			getStatusView().hide().text("No recommendations, strange...");
		else
			getStatusView().hide().text(null);

		adapter.setOnDismissListener(new DismissListener() 
		{
			@Override
			public void onDismiss(String id) 
			{
				getDismissTask(id).fire();
			}
		});
	}

	protected TraktTask createGetRecommendationsTask()
	{
		int index = getSherlockActivity().getSupportActionBar().getSelectedNavigationIndex();
		Genre genre = index <= 0 || index > genres.size() ? null : genres.get(index-1);
		getStatusView().show().text("Retrieving recommendations" + ((genre == null) ? "" : " in \"" + genre.name + "\"") + ",\nPlease wait...");	

		return commonTask = getItemsTask(genre);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{		
//		items = (ArrayList<T>) savedInstanceState.get("items");
//		genres = (ArrayList<Genre>) savedInstanceState.get("genres");
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
//		toSave.putSerializable("items", items);
//		toSave.putSerializable("genres", (Serializable) genres);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId)
	{
		//don't know why but this event is fired two times when activity is recreated, strange...
		if(recreation == 0)
		{
			if(adapter != null)
				adapter.clear();
			createGetRecommendationsTask().fire();
		}
		else 
			recreation--;
		return false;
	}
}
