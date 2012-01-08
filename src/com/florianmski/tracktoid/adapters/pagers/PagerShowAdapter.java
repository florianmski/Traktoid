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

package com.florianmski.tracktoid.adapters.pagers;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.DayOfTheWeek;
import com.jakewharton.trakt.enumerations.Rating;

public class PagerShowAdapter extends PagerAdapter
{
	private List<TvShow> shows;
	private Context context;

	public PagerShowAdapter(List<TvShow> shows, Context context)
	{
		this.context = context;

		DatabaseWrapper dbw = new DatabaseWrapper(context);
		dbw.open();
				
		//if a show on this list is in the db, get infos so we can display them (watched, loved...)
		for(int i = 0; i < shows.size(); i++)
		{
			TvShow s = shows.get(i);
			if(dbw.showExist(s.tvdbId))
				shows.set(i, dbw.getShow(s.tvdbId));
		}
		
		dbw.close();

		this.shows = shows;
	}

	@Override
	public int getCount() 
	{
		return shows.size();
	}
	
	public TvShow getItem(int position)
	{
		return shows.get(position);
	}
	
	@Override
	/** @see http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view */
	public int getItemPosition(Object object) 
	{
	    return POSITION_NONE;
	}

	@Override
	public void destroyItem(View pager, int position, Object view) 
	{
		((ViewPager)pager).removeView((View)view);
	}

	@Override
	public void finishUpdate(View container) {}

	@Override
	public Object instantiateItem(View pager, int position) 
	{
		View v = LayoutInflater.from(context).inflate(R.layout.pager_item_show, null, false);
		TextView tvAir = (TextView)v.findViewById(R.id.textViewAir);
		TextView tvOverview = (TextView)v.findViewById(R.id.textViewOverview);
		TextView tvPercentage = (TextView)v.findViewById(R.id.textViewPercentage);
		ImageView ivFanart = (ImageView)v.findViewById(R.id.imageViewFanart);
		final ImageView ivWatched = (ImageView)v.findViewById(R.id.imageViewBadge);
		final ImageView ivRating = (ImageView)v.findViewById(R.id.imageViewRating);

		//sometimes pager.getWidth = 0, don't know why so I use this trick
		int width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width*Image.RATIO_FANART));
		ivFanart.setLayoutParams(params);
		ivFanart.setScaleType(ScaleType.CENTER_CROP);

		final TvShow s = shows.get(position);

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
		AQuery aq = new AQuery(v);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback();

		//configure the callback
		cb.url(i.getUrl()).animation(android.R.anim.fade_in).fileCache(false).memCache(true);
		aq.id(ivFanart).image(cb);
		
		TransitionDrawable td = null;

		if(s.rating == Rating.Love)
			td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_loved)});
		else if(s.rating == Rating.Hate)
			td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_hated)});

		if(td != null)
			td.startTransition(1000);

		ivRating.setImageDrawable(td);

		if(s.progress == 100)
		{
			td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_watched)});
			td.startTransition(1000);
			ivWatched.setImageDrawable(td);
		}
		else
			ivWatched.setImageDrawable(null);

		if(s.ratings != null)
			tvPercentage.setText(s.ratings.percentage+"%");
		else
			tvPercentage.setText("?%");

		((ViewPager)pager).addView(v, 0);

		return v;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) 
	{
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {}

	@Override
	public Parcelable saveState() 
	{
		return null;
	}

	@Override
	public void startUpdate(View container) {}

}