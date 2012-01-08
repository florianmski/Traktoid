package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
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
        
        lvSearch.setOnItemClickListener(new OnItemClickListener() 
        {	
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent i = new Intent(getActivity(), ShowActivity.class);
				i.putExtra("position", position);
				i.putExtra("results", shows);
				startActivity(i);
			}
		});
        
        btnSearch.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				Utils.showLoading(getActivity());
				commonTask = new ShowsTask(tm, SearchFragment.this, new ShowsListener() 
				{
					@Override
					public void onShows(ArrayList<TvShow> shows) 
					{
						SearchFragment.this.shows = shows;
						Utils.removeLoading();
						lvSearch.setAdapter(new ListSearchAdapter(getActivity(), shows));
					}
				}, tm.searchService().shows(edtSearch.getText().toString().trim()), false);
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
}
