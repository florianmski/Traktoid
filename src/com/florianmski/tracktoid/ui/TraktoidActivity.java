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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.androidquery.service.MarketService;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.widgets.AppRater;
import com.florianmski.tracktoid.widgets.Panel;
import com.florianmski.tracktoid.widgets.Panel.OnPanelListener;
import com.florianmski.tracktoid.widgets.coverflow.CoverFlow;
import com.florianmski.tracktoid.widgets.coverflow.CoverFlowImageAdapter;
import com.jakewharton.trakt.entities.TvShow;

public class TraktoidActivity extends TraktActivity
{
	private CoverFlow cv;
	private TextView tvPanelhandle;
	private Panel panel;
	private ProgressBar pb;
	
	private ArrayList<TvShow> shows;
	private ShowsTask trendingTask;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        //check if db need an upgrade
        DatabaseWrapper dbw = new DatabaseWrapper(this);
        dbw.open();
        
        //check if a new version of Traktoid is available and display a dialog if so
        MarketService ms = new MarketService(this);
        ms.checkVersion();
        
        //show sometimes a dialog to rate the app on the market 
        AppRater.app_launched(this);
        
        //Trying to set high definition image on high resolution
        //does not seem to be a great idea, it's slow and I sometimes get an outOfMemoryError :/
//        Image.smallSize = (getWindowManager().getDefaultDisplay().getHeight() <= 960 && getWindowManager().getDefaultDisplay().getWidth() <= 540);
                
        Button btnSearch = (Button)findViewById(R.id.home_btn_search);
        Button btnMyShows = (Button)findViewById(R.id.home_btn_myshows);
        Button btnCalendar = (Button)findViewById(R.id.home_btn_calendar);
        Button btnRecommendations = (Button)findViewById(R.id.home_btn_recommendations);
        
        panel = (Panel)findViewById(R.id.panel);
        tvPanelhandle = (TextView)findViewById(R.id.panelHandle);
        pb = (ProgressBar)findViewById(R.id.progressBar);
        
		cv = (CoverFlow)findViewById(R.id.coverflow);
        
        btnSearch.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(TraktoidActivity.this, SearchActivity.class));
			}
		});
        
        btnMyShows.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(TraktoidActivity.this, MyShowsActivity.class));
			}
		});
        
        btnRecommendations.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(TraktoidActivity.this, RecommendationActivity.class);
				startActivity(i);
			}
		});
        
        btnCalendar.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(TraktoidActivity.this, CalendarActivity.class);
				i.putExtra("position", 1);
				startActivity(i);
			}
		});
        
		
		cv.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				tvPanelhandle.setText(shows.get(position).getTitle());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		cv.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Intent i = new Intent(TraktoidActivity.this, ShowActivity.class);
				i.putExtra("results", shows);
				i.putExtra("position", position);
				startActivity(i);
			}
		});
		
		panel.setOnPanelListener(new OnPanelListener() 
		{
			@Override
			public void onPanelOpened(Panel panel) 
			{
				//if we don't already downloaded trending shows, do it
				if(shows == null && trendingTask.getStatus() != AsyncTask.Status.RUNNING)
					trendingTask.execute();
				else if(shows != null)
					tvPanelhandle.setText(shows.get(cv.getSelectedItemPosition()).getTitle());
			}
			
			@Override
			public void onPanelClosed(Panel panel) 
			{
				tvPanelhandle.setText("Trending");
			}
		});
		
		trendingTask = new ShowsTask(tm, TraktoidActivity.this, new ShowsListener() 
        {
			@Override
			public void onShows(ArrayList<TvShow> shows) 
			{
				TraktoidActivity.this.shows = shows;
		        cv.setAdapter(new CoverFlowImageAdapter(shows));
		        pb.setVisibility(View.GONE);
			}
		}, tm.showService().trending(), false);
    }
    
    @Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
    	menu.add(0, R.id.action_bar_about, 0, "About")
			.setIcon(R.drawable.ab_icon_info)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	menu.add(0, R.id.action_bar_settings, 0, "Settings")
			.setIcon(R.drawable.ab_icon_settings)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	return super.onCreateOptionsMenu(menu);
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle item selection
		switch (item.getItemId()) 
		{
		case R.id.action_bar_settings:
			startActivity(new Intent(TraktoidActivity.this, SettingsActivity.class));
			return true;
		case R.id.action_bar_about:
			startActivity(new Intent(TraktoidActivity.this, AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
    	//if panel is open and user press on back, close the panel (like menu)
        if ((keyCode == KeyEvent.KEYCODE_BACK) && panel.isOpen()) 
        	panel.setOpen(false, false);
        else
        	return super.onKeyDown(keyCode, event);
        
        return false;
    }
}