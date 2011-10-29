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
import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.OnNavigationListener;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.GridPosterAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBShowsTask;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.RateTask;
import com.florianmski.tracktoid.trakt.tasks.RemoveShowTask;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.UpdateShowsTask;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask.ShowsListener;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public class MyShowsActivity extends TraktActivity
{		
	private GridView gd;
	private QuickAction quickAction;

	private int padding = 2;
	private int nbColumns;
	private int posterClickedPosition = -1;

	private GridPosterAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_shows);

		Utils.showLoading(this);
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] items = new String[] {"All shows", "Unwatched shows", "Loved shows"};
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.abs__simple_spinner_item, items);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		getSupportActionBar().setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() 
		{
			@Override
			public boolean onNavigationItemSelected(int filter, long itemId) 
			{
				adapter.setFilter(filter);
				return false;
			}
		});
				
		DatabaseWrapper dbw = new DatabaseWrapper(this);
		dbw.open();
		boolean isDBEmpty = dbw.isEmpty();
		dbw.close();

		gd = (GridView)findViewById(R.id.gridViewShows);

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			nbColumns = 5;
		else
			nbColumns = 3;
		
		gd.setNumColumns(nbColumns);
		
		adapter = new GridPosterAdapter(this, new ArrayList<TvShow>(), calculatePosterHeight());
		gd.setAdapter(adapter);

		if(isDBEmpty)
		{
			Utils.removeLoading();
			if(!tm.isUpdateTaskRunning())
				tm.addToQueue(new ShowsTask(tm, this, new ShowsListener() 
				{
					@Override
					public void onShows(ArrayList<TvShow> shows) 
					{
						createShowsDialog(shows);						
					}
				}, tm.userService().libraryShowsAll(TraktManager.getUsername()), true));
		}
		else
			new DBShowsTask(this, new DBAdapter() 
			{
				@Override
				public void onDBShows(List<TvShow> shows)
				{
					Utils.removeLoading();
					adapter = new GridPosterAdapter(MyShowsActivity.this, shows, calculatePosterHeight());
					gd.setAdapter(adapter);
				}
			}).execute();

		gd.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				Intent i = new Intent(MyShowsActivity.this, MyShowActivity.class);
				i.putExtra("show", (TvShow)adapter.getItem(position));
				startActivity(i);
			}
		});

		quickAction = new QuickAction(this);

		ActionItem aiRefresh = new ActionItem();
		aiRefresh.setTitle("Refresh");
		aiRefresh.setIcon(getResources().getDrawable(R.drawable.ab_icon_refresh));

		ActionItem aiDelete = new ActionItem();
		aiDelete.setTitle("Delete");
		aiDelete.setIcon(getResources().getDrawable(R.drawable.ab_icon_delete));
		
		ActionItem aiRating = new ActionItem();
		aiRating.setTitle("Rate");
		aiRating.setIcon(getResources().getDrawable(R.drawable.ab_icon_rate));

		quickAction.addActionItem(aiRefresh);
		quickAction.addActionItem(aiDelete);
		quickAction.addActionItem(aiRating);

		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() 
		{			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) 
			{
				switch(pos)
				{
				case 0 :
					ArrayList<TvShow> showsSelected = new ArrayList<TvShow>();
					showsSelected.add((TvShow)adapter.getItem(posterClickedPosition));
					tm.addToQueue(new UpdateShowsTask(tm, MyShowsActivity.this, showsSelected));
					break;
				case 1 :
					tm.addToQueue(new RemoveShowTask(tm, MyShowsActivity.this, (TvShow)adapter.getItem(posterClickedPosition)));
					break;
				case 2:
					final CharSequence[] items = {"Totally ninja!", "Week sauce :(", "Unrate"};
					final Rating[] ratings = {Rating.Love, Rating.Hate, Rating.UNRATE};

					AlertDialog.Builder builder = new AlertDialog.Builder(MyShowsActivity.this);
					builder.setTitle("Rate");
					builder.setItems(items, new DialogInterface.OnClickListener() 
					{
					    public void onClick(DialogInterface dialog, int item) 
					    {
					        tm.addToQueue(new RateTask(tm, MyShowsActivity.this, (TvShow)adapter.getItem(posterClickedPosition), ratings[item]));
					    }
					});
					AlertDialog alert = builder.create();
					alert.show();
					break;
				}
			}
		});

		gd.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long arg3) 
			{
				onShowQuickAction(v, position);
				return false;
			}

		});

	}

	public void onShowQuickAction(View v, int position) 
	{
		//maybe add a setTag() function in quickAction to avoid this
		posterClickedPosition = position;
		quickAction.show(v);
	}
	
	private int calculatePosterHeight()
	{
		int width = (getWindowManager().getDefaultDisplay().getWidth()/nbColumns)-padding*2;
		return (int) (width*Image.RATIO_POSTER)-padding*2;
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		if(!tm.isUpdateTaskRunning())
		{
			menu.add(0, R.id.action_bar_refresh, 0, "Refresh")
				.setIcon(R.drawable.ab_icon_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		else
		{
			int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
			ProgressBar pbRefresh = new ProgressBar(this);
			pbRefresh.setIndeterminate(true);
			pbRefresh.setLayoutParams(new LinearLayout.LayoutParams(value, value));
			
			menu.add(0, R.id.action_bar_refresh, 0, "Refresh")
				.setActionView(pbRefresh)
				.setEnabled(false)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
			
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == R.id.action_bar_refresh) 
		{
			tm.addToQueue(new ShowsTask(tm, this, new ShowsListener() 
			{
				@Override
				public void onShows(ArrayList<TvShow> shows) 
				{
					createShowsDialog(shows);
				}
			}, tm.userService().libraryShowsAll(TraktManager.getUsername()), true));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBeforeTraktRequest()
	{
		invalidateOptionsMenu();
	}

	@Override
	public void onAfterTraktRequest(boolean success) 
	{
		invalidateOptionsMenu();
	}
	
	@Override
	public void onShowUpdated(TvShow show)
	{
		if(adapter != null)
			adapter.updateShow(show);
	}
	
	
	@Override
	public void onShowRemoved(TvShow show)
	{
		if(adapter != null)
			adapter.removeShow(show);
	}
	
	public void createShowsDialog(final ArrayList<TvShow> shows)
	{
		final ArrayList<TvShow> selectedShows = new ArrayList<TvShow>();
		
		String[] items = new String[shows.size()];

		for(int i = 0; i < shows.size(); i++)
			items[i] = shows.get(i).getTitle();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Which show(s) do you want to refresh ?");
		builder.setMultiChoiceItems(items, null, new OnMultiChoiceClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) 
			{
				if(isChecked)
					selectedShows.add(shows.get(which));
				else
					selectedShows.remove(shows.get(which));
			}
		});

		builder.setPositiveButton("Go!", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				if(selectedShows.size() > 0)
					tm.addToQueue(new UpdateShowsTask(tm, MyShowsActivity.this, selectedShows));
				else
					Toast.makeText(MyShowsActivity.this, "Nothing selected...", Toast.LENGTH_SHORT).show();
			}
		});

		builder.setNeutralButton("All", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				tm.addToQueue(new UpdateShowsTask(tm, MyShowsActivity.this, shows));
			}
		});

		builder.setNegativeButton("Cancel", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		
		AlertDialog alert = builder.create();
		
		//avoid trying to show dialog if activity no longer exist
		if(!isFinishing())
			alert.show();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		invalidateOptionsMenu();
	}

}
