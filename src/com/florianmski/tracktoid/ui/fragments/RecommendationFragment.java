package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

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

public abstract class RecommendationFragment<T extends TraktoidInterface<T>> extends TraktFragment
{	
	protected final static int START_YEAR = 1919;
	protected final static int END_YEAR = 2019;
	
	protected ListView lvRecommendations;
	protected Spinner spGenre, spStartYear, spEndYear;
	protected ImageView ivSend;

	protected ListRecommendationAdapter<T> adapter;

	protected List<Genre> genres;
	protected List<T> items;

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
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		
		if(savedInstanceState == null)
		{
			getStatusView().show().text("Retrieving genres,\nPlease wait...");

			getGenresTask().fire();
		}
		else
			setAdapter();

		lvRecommendations.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Intent intent = new Intent(getActivity(), ShowActivity.class);
				intent.putExtra(TraktoidConstants.BUNDLE_RESULTS, (ArrayList<T>) adapter.getItems());
				intent.putExtra(TraktoidConstants.BUNDLE_POSITION, position-1);
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

	protected void setAdapter()
	{
		if(adapter == null)
		{
			adapter = new ListRecommendationAdapter<T>(items, getActivity());
			View header = LayoutInflater.from(getActivity()).inflate(R.layout.view_recommendation_header_list, null);
			spGenre = (Spinner) header.findViewById(R.id.spinnerGenre);
			spStartYear = (Spinner) header.findViewById(R.id.spinnerStartYear);
			spEndYear = (Spinner) header.findViewById(R.id.spinnerEndYear);
			ivSend = (ImageView) header.findViewById(R.id.imageViewSend);
			lvRecommendations.addHeaderView(header);
			lvRecommendations.setAdapter(adapter);
			
			String[] itemsGenres = new String[genres.size()+1];
			itemsGenres[0] = "All Genres";

			for(int i = 1; i < itemsGenres.length; i++)
				itemsGenres[i] = genres.get(i-1).name;

			ArrayAdapter<String> spinnerAdapterGenres = new ArrayAdapter<String>(getActivity(), R.layout.sherlock_spinner_item, itemsGenres);
			spinnerAdapterGenres.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
			spGenre.setAdapter(spinnerAdapterGenres);
			
			int size = END_YEAR - START_YEAR + 1;
			String[] itemsStartYear = new String[size];
			String[] itemsEndYear = new String[size];
			for(int i = 0; i < size; i++)
			{
				itemsStartYear[i] = String.valueOf(START_YEAR+i);
				itemsEndYear[i] = String.valueOf(END_YEAR-i);
			}
			ArrayAdapter<String> spinnerAdapterStartYear = new ArrayAdapter<String>(getActivity(), R.layout.sherlock_spinner_item, itemsStartYear);
			spinnerAdapterStartYear.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
			spStartYear.setAdapter(spinnerAdapterStartYear);
			ArrayAdapter<String> spinnerAdapterEndYear = new ArrayAdapter<String>(getActivity(), R.layout.sherlock_spinner_item, itemsEndYear);
			spinnerAdapterEndYear.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
			spEndYear.setAdapter(spinnerAdapterEndYear);
			
			ivSend.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					adapter.clear();
					createGetRecommendationsTask().fire();
				}
			});
		}
		else
		{
			adapter.updateItems(items);
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
		int index = spGenre == null ? -1 : spGenre.getSelectedItemPosition();
		Genre genre = index <= 0 || index > genres.size() ? null : genres.get(index-1);
		getStatusView().show().text("Retrieving recommendations" + ((genre == null) ? "" : " in \"" + genre.name + "\"") + ",\nPlease wait...");	

		return commonTask = getItemsTask(genre);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
