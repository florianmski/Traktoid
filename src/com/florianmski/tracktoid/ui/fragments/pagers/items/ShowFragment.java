package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.trakt.tasks.get.UpdateShowsTask;
import com.florianmski.tracktoid.ui.activities.phone.ShoutsActivity;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.DayOfTheWeek;
import com.jakewharton.trakt.enumerations.Rating;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ShowFragment extends PagerItemFragment
{
	private TvShow s;
	private boolean existsInDb = false;

	public static ShowFragment newInstance(Bundle args)
	{
		ShowFragment f = new ShowFragment();
		f.setArguments(args);
		return f;
	}

	public static ShowFragment newInstance(TvShow s)
	{
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_TVSHOW, s);

		return newInstance(args);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		s = (TvShow) (getArguments() != null ? getArguments().getSerializable(TraktoidConstants.BUNDLE_TVSHOW) : null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		DatabaseWrapper dbw = new DatabaseWrapper(getActivity());
		dbw.open();
		existsInDb = dbw.showExist(s.tvdbId);
		dbw.close();
		getSupportActivity().invalidateOptionsMenu();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_show, null);

		TextView tvAir = (TextView)v.findViewById(R.id.textViewAir);
		TextView tvOverview = (TextView)v.findViewById(R.id.textViewOverview);
		TextView tvPercentage = (TextView)v.findViewById(R.id.textViewPercentage);
		ImageView ivFanart = (ImageView)v.findViewById(R.id.imageViewFanart);
		final ImageView ivWatched = (ImageView)v.findViewById(R.id.imageViewBadge);
		final ImageView ivRating = (ImageView)v.findViewById(R.id.imageViewRating);

		//sometimes pager.getWidth = 0, don't know why so I use this trick
		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width*Image.RATIO_FANART));
		ivFanart.setLayoutParams(params);
		ivFanart.setScaleType(ScaleType.CENTER_CROP);

		DayOfTheWeek airDay = s.airDay;

		String airTime = s.airTime == null || s.airTime.equals("") || s.airTime.equals("null") ? "?" : s.airTime;
		String network = s.network == null || s.network.equals("") || s.network.equals("null") ? "?" : s.network;
		String runtime = s.runtime == 0 ? "?min" : s.runtime + "min";
		String firstAired = s.firstAired == null ? "" : "First Aired : " + new SimpleDateFormat("MM/dd/yyyy").format(s.firstAired) + "\n";

		tvAir.setText(firstAired + ((airDay == null || airDay.toString().equals("")) ? "?" : airDay) + " at " + airTime + " on " + network + " (" + runtime + ")");

		tvOverview.setText(s.overview);
		ivRating.setImageBitmap(null);
		ivWatched.setImageBitmap(null);

		Image i = new Image(s.tvdbId, s.images.screen, Image.FANART);
		final AQuery aq = new AQuery(v);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback().url(i.getUrl()).animation(android.R.anim.fade_in).fileCache(false).memCache(true);
		aq.id(ivFanart).image(cb);

		TransitionDrawable td = null;

		if(s.rating == Rating.Love)
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_loved)});
		else if(s.rating == Rating.Hate)
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_hated)});

		if(td != null)
			td.startTransition(1000);

		ivRating.setImageDrawable(td);

		if(s.progress == 100)
		{
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_watched)});
			td.startTransition(1000);
			ivWatched.setImageDrawable(td);
		}
		else
			ivWatched.setImageDrawable(null);

		if(s.ratings != null)
			tvPercentage.setText(s.ratings.percentage+"%");
		else
			tvPercentage.setText("?%");

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		
		//check if user has already this show in his lib. If so hide the "add" button
		if(!existsInDb)
		{
			menu.add(0, R.id.action_bar_add, 0, "Add")
			.setIcon(R.drawable.ab_icon_add)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		menu.add(0, R.id.action_bar_shouts, 0, "Shouts")
		.setIcon(R.drawable.ab_icon_shouts)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.action_bar_add :
			ArrayList<TvShow> shows = new ArrayList<TvShow>();
			shows.add(s);
			tm.addToQueue(new UpdateShowsTask(tm, this, shows));
			return true;
		case R.id.action_bar_shouts :
			Intent i = new Intent(getActivity(), ShoutsActivity.class);
			i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, s.tvdbId);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		// TODO Auto-generated method stub
	}
}
