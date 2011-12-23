package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.service.MarketService;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.jakewharton.trakt.entities.TvShow;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.ui.activities.phone.AboutActivity;
import com.florianmski.tracktoid.ui.activities.phone.CalendarActivity;
import com.florianmski.tracktoid.ui.activities.phone.RecommendationActivity;
import com.florianmski.tracktoid.ui.activities.phone.SearchActivity;
import com.florianmski.tracktoid.ui.activities.phone.SettingsActivity;
import com.florianmski.tracktoid.ui.activities.phone.ShowActivity;
import com.florianmski.tracktoid.ui.activities.tablet.MyShowsTabletActivity;
import com.florianmski.tracktoid.widgets.AppRater;
import com.florianmski.tracktoid.widgets.Panel;
import com.florianmski.tracktoid.widgets.Panel.OnPanelListener;
import com.florianmski.tracktoid.widgets.coverflow.CoverFlow;
import com.florianmski.tracktoid.widgets.coverflow.CoverFlowImageAdapter;

public class HomeFragment extends TraktFragment
{
	private CoverFlow cv;
	private TextView tvPanelhandle;
	private Panel panel;
	private ProgressBar pb;

	private ArrayList<TvShow> shows;

	public HomeFragment() {}
	
	public HomeFragment(FragmentListener listener) 
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

		//check if db need an upgrade
		DatabaseWrapper dbw = new DatabaseWrapper(getActivity());
		dbw.open();

		//check if a new version of Traktoid is available and display a dialog if so
		MarketService ms = new MarketService(getActivity());
		ms.checkVersion();

		//show sometimes a dialog to rate the app on the market 
		AppRater.app_launched(getActivity());

		//Trying to set high definition image on high resolution
		//does not seem to be a great idea, it's slow and I sometimes get an outOfMemoryError :/
		//        Image.smallSize = (getWindowManager().getDefaultDisplay().getHeight() <= 960 && getWindowManager().getDefaultDisplay().getWidth() <= 540);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_home, null);

		Button btnSearch = (Button)v.findViewById(R.id.home_btn_search);
		Button btnMyShows = (Button)v.findViewById(R.id.home_btn_myshows);
		Button btnCalendar = (Button)v.findViewById(R.id.home_btn_calendar);
		Button btnRecommendations = (Button)v.findViewById(R.id.home_btn_recommendations);

		panel = (Panel)v.findViewById(R.id.panel);
		tvPanelhandle = (TextView)v.findViewById(R.id.panelHandle);
		pb = (ProgressBar)v.findViewById(R.id.progressBar);

		cv = (CoverFlow)v.findViewById(R.id.coverflow);

		btnSearch.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(getActivity(), SearchActivity.class));
			}
		});

		btnMyShows.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
//				if(Utils.isTabletDevice(getActivity()))
					startActivity(new Intent(getActivity(), MyShowsTabletActivity.class));
//				else
//					startActivity(new Intent(getActivity(), MyShowsActivity.class));
			}
		});

		btnRecommendations.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(getActivity(), RecommendationActivity.class);
				startActivity(i);
			}
		});

		btnCalendar.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(getActivity(), CalendarActivity.class);
				i.putExtra("position", 1);
				startActivity(i);
			}
		});


		cv.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				tvPanelhandle.setText(shows.get(position).title);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		cv.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Intent i = new Intent(getActivity(), ShowActivity.class);
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
				if(shows == null && (commonTask == null || commonTask.getStatus() != AsyncTask.Status.RUNNING))
				{
					commonTask = new ShowsTask(tm, HomeFragment.this, new ShowsListener() 
					{
						@Override
						public void onShows(ArrayList<TvShow> shows) 
						{
							HomeFragment.this.shows = shows;
							cv.setAdapter(new CoverFlowImageAdapter(shows));
							pb.setVisibility(View.GONE);
						}
					}, tm.showService().trending(), false);
					commonTask.execute();
				}
				else if(shows != null)
					tvPanelhandle.setText(shows.get(cv.getSelectedItemPosition()).title);
			}

			@Override
			public void onPanelClosed(Panel panel) 
			{
				tvPanelhandle.setText("Trending");
			}
		});

		return v;
	}

	public void handlePanel()
	{
		//if panel is open and user press on back, close the panel (like menu)
		if(panel.isOpen())
			panel.setOpen(false, false);
		else
			getActivity().finish();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		menu.add(0, R.id.action_bar_about, 0, "About")
		.setIcon(R.drawable.ab_icon_info)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, R.id.action_bar_settings, 0, "Settings")
		.setIcon(R.drawable.ab_icon_settings)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle item selection
		switch (item.getItemId()) 
		{
		case R.id.action_bar_settings:
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			return true;
		case R.id.action_bar_about:
			startActivity(new Intent(getActivity(), AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
