package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListSearchAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.ui.activities.phone.ShowActivity;
import com.jakewharton.trakt.entities.TvShow;

public class SearchFragment extends TraktFragment
{
	private ArrayList<TvShow> shows = new ArrayList<TvShow>();
	
	private ListView lvSearch;
	private EditText edtSearch;
	private Button btnSearch;
	
	private ListSearchAdapter adapter;
	
	public static SearchFragment newInstance(Bundle args)
	{
		SearchFragment f = new SearchFragment();
		f.setArguments(args);
		return f;
	}
	
	public SearchFragment() {}
	
	public SearchFragment(FragmentListener listener) 
	{
		super(listener);
	}
	
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
        
		if(savedInstanceState != null)
		{
			adapter = new ListSearchAdapter(getActivity(), shows);
			lvSearch.setAdapter(adapter);
		}
		
        lvSearch.setOnItemClickListener(new OnItemClickListener() 
        {	
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent i = new Intent(getActivity(), ShowActivity.class);
				i.putExtra(TraktoidConstants.BUNDLE_POSITION, position);
				i.putExtra(TraktoidConstants.BUNDLE_RESULTS, shows);
				startActivity(i);
			}
		});
        
        btnSearch.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				if(adapter != null)
					adapter.clear();
				
				String search = edtSearch.getText().toString().trim();
				getStatusView().show().text("Searching for \"" + search + "\",\nPlease wait...");
				
				commonTask = new ShowsTask(tm, SearchFragment.this, new ShowsListener() 
				{
					@Override
					public void onShows(ArrayList<TvShow> shows) 
					{
						SearchFragment.this.shows = shows;
						
						if(adapter == null)
						{
							adapter = new ListSearchAdapter(getActivity(), shows);
							lvSearch.setAdapter(adapter);
						}
						else
							adapter.reloadData(shows);
						
						if(adapter.isEmpty())
							getStatusView().hide().text("Nothing found, sorry man...");
						else
							getStatusView().hide().text(null);
					}
				}, tm.searchService().shows(search), false);
        		commonTask.execute();
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_search, null);
		
		lvSearch = (ListView)v.findViewById(android.R.id.list);
        edtSearch = (EditText)v.findViewById(R.id.editTextSearch);
        btnSearch = (Button)v.findViewById(R.id.buttonSearch);
		
		return v;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		shows = (ArrayList<TvShow>) savedInstanceState.getSerializable(TraktoidConstants.BUNDLE_RESULTS);
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		toSave.putSerializable(TraktoidConstants.BUNDLE_RESULTS, shows);
	}
}
