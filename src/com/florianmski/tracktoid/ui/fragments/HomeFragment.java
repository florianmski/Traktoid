package com.florianmski.tracktoid.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.service.MarketService;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.pagers.PagerDashboardAdapter;
import com.florianmski.tracktoid.adapters.pagers.PagerDashboardAdapter.onDashboardButtonClicked;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.trakt.tasks.get.ActivityTask;
import com.florianmski.tracktoid.trakt.tasks.get.CheckinTask;
import com.florianmski.tracktoid.trakt.tasks.get.CheckinTask.CheckinListener;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.ui.activities.phone.AboutActivity;
import com.florianmski.tracktoid.ui.activities.phone.CalendarActivity;
import com.florianmski.tracktoid.ui.activities.phone.LibraryActivity;
import com.florianmski.tracktoid.ui.activities.phone.RecommendationActivity;
import com.florianmski.tracktoid.ui.activities.phone.SearchActivity;
import com.florianmski.tracktoid.ui.activities.phone.SettingsActivity;
import com.florianmski.tracktoid.ui.activities.phone.TrendingActivity;
import com.florianmski.tracktoid.widgets.AppRater;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.jakewharton.trakt.entities.ActivityItemBase;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.enumerations.ActivityAction;
import com.jakewharton.trakt.enumerations.ActivityType;
import com.viewpagerindicator.CirclePageIndicator;

public class HomeFragment extends TraktFragment implements onDashboardButtonClicked
{
	private BadgesView bvWatchingNow;
	private TextView tvEpisodeTitle;
	private TextView tvEpisodeEpisode;
	private ImageView ivScreen;
	
	private TvShowEpisode episode;
	private String tvdbId;

	public static HomeFragment newInstance(Bundle args)
	{
		HomeFragment f = new HomeFragment();
		f.setArguments(args);
		return f;
	}
	
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
		getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		//TODO make something smart
		//check if db need an upgrade
//		DatabaseWrapper dbw = new DatabaseWrapper(getActivity());
//		dbw.open();

		//check if a new version of Traktoid is available and display a dialog if so
		MarketService ms = new MarketService(getActivity());
		ms.checkVersion();

		//show sometimes a dialog to rate the app on the market 
		AppRater.app_launched(getActivity());
		
		//sync with trakt
		new ActivityTask(tm, this).silentConnectionError(true).fire();

		//Trying to set high definition image on high resolution
		//does not seem to be a great idea, it's slow and I sometimes get an outOfMemoryError :/
//		Image.smallSize = !Utils.isTabletDevice(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_home, null);

		bvWatchingNow = (BadgesView)v.findViewById(R.id.badgesLayoutWatchingNow);
		tvEpisodeTitle = (TextView)v.findViewById(R.id.textViewTitle);
		tvEpisodeEpisode = (TextView)v.findViewById(R.id.textViewEpisode);
		ivScreen = (ImageView)v.findViewById(R.id.imageViewScreen);
		
		ViewPager vp = (ViewPager)v.findViewById(R.id.paged_view);
		CirclePageIndicator pageIndicator = (CirclePageIndicator) v.findViewById(R.id.page_indicator_circle);
		
		vp.setAdapter(new PagerDashboardAdapter(this));
		pageIndicator.setViewPager(vp);

		bvWatchingNow.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("Cancel the checkin ?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int id) 
					{
						new PostTask(tm, HomeFragment.this, tm.showService().cancelCheckin(), new PostListener() 
						{
							@Override
							public void onComplete(Response r, boolean success) 
							{
								if(success)
								{
									//unseen the episode we've canceled
									DatabaseWrapper dbw = new DatabaseWrapper(getActivity());
									dbw.markEpisodeAsWatched(false, tvdbId, episode.season, episode.number);
									dbw.refreshPercentage(tvdbId);
									dbw.close();
									bvWatchingNow.setVisibility(View.INVISIBLE);
								}
							}
						}).fire();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int id) 
					{
						dialog.cancel();
					}
				});
				builder.create().show();
			}
		});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		menu.add(0, R.id.action_bar_about, 0, "About")
		.setIcon(R.drawable.ab_icon_info)
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

	@Override
	public void onResume()
	{
		super.onResume();

		new CheckinTask(tm, this, new CheckinListener() 
		{
			@Override
			public void onCheckin(ActivityItemBase checkin) 
			{
				if(checkin != null && checkin.type == ActivityType.Episode && checkin.action == ActivityAction.Checkin)
				{
					tvdbId = checkin.show.tvdbId;
					episode = checkin.episode;
					bvWatchingNow.setVisibility(View.VISIBLE);
					bvWatchingNow.initialize();
					bvWatchingNow.setTraktItem(episode);
					tvEpisodeTitle.setText(episode.title);
					tvEpisodeEpisode.setText(Utils.addZero(episode.number) + "x" + Utils.addZero(episode.season));
					TraktImage i = TraktImage.getScreen(checkin.show);
					final AQuery aq = new AQuery(getActivity());
					BitmapAjaxCallback cb = new BitmapAjaxCallback()
			        {
			        	@Override
			            public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
			        	{     
			        		aq.id(ivScreen).image(Utils.borderBitmap(bm, getActivity())).animate(android.R.anim.fade_in);
			            }

			        }.url(i.getUrl()).fileCache(false).memCache(true).ratio(9.0f / 16.0f);
			        aq.id(ivScreen).image(cb);
				}
				else
				{
					bvWatchingNow.setVisibility(View.INVISIBLE);
				}
			}
		}).silent(true).fire();
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

	@Override
	public void onClick(int buttonId) 
	{
		switch(buttonId)
		{
		case R.id.home_btn_calendar:
			Intent i = new Intent(getActivity(), CalendarActivity.class);
			i.putExtra(TraktoidConstants.BUNDLE_POSITION, 1);
			startActivity(i);
			break;
		case R.id.home_btn_myshows:
			startActivity(new Intent(getActivity(), LibraryActivity.class));
			break;
		case R.id.home_btn_recommendations:
			startActivity(new Intent(getActivity(), RecommendationActivity.class));
			break;
		case R.id.home_btn_search:
			startActivity(new Intent(getActivity(), SearchActivity.class));
			break;
		case R.id.home_btn_trending:
			startActivity(new Intent(getActivity(), TrendingActivity.class));
			break;
		}
	}
}
