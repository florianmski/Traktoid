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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.db.DatabaseWrapper;

public class TracktoidActivity extends TraktActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        //check if db need an upgrade
        DatabaseWrapper dbw = new DatabaseWrapper(this);
        dbw.open();
                
        Button btnSearch = (Button)findViewById(R.id.home_btn_search);
        Button btnMyShows = (Button)findViewById(R.id.home_btn_myshows);
        Button btnCalendar = (Button)findViewById(R.id.home_btn_calendar);
        Button btnRecommendations = (Button)findViewById(R.id.home_btn_recommendations);
        
        btnSearch.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(TracktoidActivity.this, SearchActivity.class));
			}
		});
        
        btnMyShows.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(TracktoidActivity.this, MyShowsActivity.class));
			}
		});
        
        btnRecommendations.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(TracktoidActivity.this, RecommendationActivity.class);
				startActivity(i);
			}
		});
        
        btnCalendar.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(TracktoidActivity.this, CalendarActivity.class);
				i.putExtra("position", 1);
				startActivity(i);
			}
		});
    }
    
    @Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
    	menu.add(0, R.id.menu_settings, 0, "Settings")
    		.setIcon(R.drawable.gd_action_bar_settings)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    	menu.add(0, R.id.action_bar_about, 0, "About")
			.setIcon(R.drawable.gd_action_bar_info)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	return super.onCreateOptionsMenu(menu);
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle item selection
		switch (item.getItemId()) 
		{
		case R.id.menu_settings:
			startActivity(new Intent(TracktoidActivity.this, SettingsActivity.class));
			return true;
		case R.id.action_bar_about:
			startActivity(new Intent(TracktoidActivity.this, AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}