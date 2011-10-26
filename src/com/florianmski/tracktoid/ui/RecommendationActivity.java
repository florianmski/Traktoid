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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.ListRecommendationAdapter;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask.ShowsListener;
import com.jakewharton.trakt.entities.TvShow;

public class RecommendationActivity extends TraktActivity
{
	private ArrayList<TvShow> recommendations;
	
	private ListView lvRecommendations;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_recommendation);
		
		lvRecommendations = (ListView)findViewById(R.id.listViewRecommendation);

		Utils.showLoading(this);

		new ShowsTask(tm, this, new ShowsListener() 
		{
			@Override
			public void onShows(ArrayList<TvShow> shows) 
			{
				Utils.removeLoading();
				recommendations = shows;
				lvRecommendations.setAdapter(new ListRecommendationAdapter(shows, RecommendationActivity.this));
			}
		}, tm.recommendationsService().shows(), false).execute();

		lvRecommendations.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Intent intent = new Intent(RecommendationActivity.this, ShowActivity.class);
				intent.putExtra("results", recommendations);
				intent.putExtra("position", position);
				startActivity(intent);
			}

		});

	}
}
