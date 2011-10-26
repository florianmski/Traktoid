/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.ListSearchAdapter;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask.ShowsListener;
import com.jakewharton.trakt.entities.TvShow;

public class SearchActivity extends TraktActivity
{		
	private ArrayList<TvShow> shows = new ArrayList<TvShow>();
	
	private ListView lvSearch;
	private EditText edtSearch;
	private Button btnSearch;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        lvSearch = (ListView)findViewById(android.R.id.list);
        edtSearch = (EditText)findViewById(R.id.editTextSearch);
        btnSearch = (Button)findViewById(R.id.buttonSearch);
        
        lvSearch.setOnItemClickListener(new OnItemClickListener() 
        {	
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent i = new Intent(SearchActivity.this, ShowActivity.class);
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
				Utils.showLoading(SearchActivity.this);
				tm.addToQueue(new ShowsTask(tm, SearchActivity.this, new ShowsListener() 
				{
					@Override
					public void onShows(ArrayList<TvShow> shows) 
					{
						SearchActivity.this.shows = shows;
						Utils.removeLoading();
						lvSearch.setAdapter(new ListSearchAdapter(SearchActivity.this, shows));
					}
				}, tm.searchService().shows(edtSearch.getText().toString().trim()), false));
			}
		});    
    }	
}
